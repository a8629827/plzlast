package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportService 구현 클래스
 * 신고 관련 비즈니스 로직을 처리합니다.
 */
@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository; // 신고 레포지토리 의존성
    private final PostRepository postRepository; // 게시글 레포지토리 의존성
    private final UserRepository userRepository; // 사용자 레포지토리 의존성
    private final ModelMapper modelMapper; // 엔티티와 DTO 변환용

    /**
     * 새로운 신고를 생성합니다.
     *
     * @param reportDTO 신고 데이터 전달 객체
     */
    @Override
    public void createReport(ReportDTO reportDTO) {
        log.info("신고 생성 요청: postId={}, userId={}, category={}, reason={}",
                reportDTO.getPostId(), reportDTO.getUserId(), reportDTO.getCategory(), reportDTO.getReason());

        if (reportDTO.getPostId() == null || reportDTO.getUserId() == null) {
            throw new IllegalArgumentException("postId 또는 userId가 null입니다.");
        }

        // 게시글 조회
        Post post = postRepository.findById(reportDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 사용자 조회
        User user = userRepository.findById(reportDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 중복 신고 방지
        boolean isDuplicate = reportRepository.existsByPostAndUser(post, user);
        if (isDuplicate) {
            throw new IllegalStateException("이미 해당 게시글을 신고하셨습니다.");
        }

        // 신고 생성 및 저장
        Report report = Report.builder()
                .post(post)
                .user(user)
                .category(Report.ReportCategory.valueOf(reportDTO.getCategory().toUpperCase()))
                .reason(reportDTO.getReason())
                .status(Report.ReportStatus.PENDING)
                .createdAt(new Date())
                .build();

        reportRepository.save(report);
        log.info("신고가 저장되었습니다: {}", report);
    }

    /**
     * 모든 신고를 조회합니다.
     *
     * @return 모든 신고의 DTO 리스트
     */
    @Override
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
    }

    /**
     * 진행 중인(PENDING 상태) 신고를 조회합니다.
     *
     * @return 진행 중인 신고의 DTO 리스트
     */
    @Override
    public List<ReportDTO> getReportsInProgress() {
        List<Report> reports = reportRepository.findByStatus(Report.ReportStatus.PENDING);
        return reports.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시글에 대한 신고를 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 해당 게시글의 신고 DTO 리스트
     */
    @Override
    public List<ReportDTO> getReportsByPostId(Long postId) {
        List<Report> reports = reportRepository.findByPost_PostId(postId);
        return reports.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 신고를 공개(VISIBLE) 상태로 변경합니다.
     *
     * @param reportId 신고 ID
     */
    @Override
    public void markAsVisible(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));

        report.setStatus(Report.ReportStatus.VISIBLE); // 신고 상태를 VISIBLE로 변경
        reportRepository.save(report);

        Post post = report.getPost();
        post.setIsVisible(true); // 게시글도 공개로 설정
        postRepository.save(post);

        log.info("신고와 게시글이 공개 처리되었습니다: reportId={}, postId={}", reportId, post.getPostId());
    }

    /**
     * 특정 신고를 비공개(HIDDEN) 상태로 변경합니다.
     *
     * @param reportId 신고 ID
     */
    @Override
    public void markAsHidden(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));

        report.setStatus(Report.ReportStatus.HIDDEN); // 신고 상태를 HIDDEN으로 변경
        reportRepository.save(report);

        Post post = report.getPost();
        post.setIsVisible(false); // 게시글도 비공개로 설정
        postRepository.save(post);

        log.info("신고와 게시글이 비공개 처리되었습니다: reportId={}, postId={}", reportId, post.getPostId());
    }

    /**
     * 신고 ID로 게시글 ID를 반환합니다.
     *
     * @param reportId 신고 ID
     * @return 해당 신고와 관련된 게시글 ID
     */
    @Override
    public Long getPostIdByReportId(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));
        return report.getPost().getPostId();
    }

    /**
     * Report 엔티티를 ReportDTO로 변환합니다.
     *
     * @param report 변환할 Report 엔티티
     * @return 변환된 ReportDTO
     */
    private ReportDTO convertToReportDTO(Report report) {
        ReportDTO reportDTO = modelMapper.map(report, ReportDTO.class);
        reportDTO.setPostTitle(report.getPost().getTitle()); // 게시글 제목 설정
        reportDTO.setPostId(report.getPost().getPostId()); // 게시글 ID 설정
        return reportDTO;
    }
}