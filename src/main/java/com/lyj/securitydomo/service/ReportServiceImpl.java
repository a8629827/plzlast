package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository; // ReportRepository 의존성 주입
    private final PostRepository postRepository; // PostRepository 의존성 주입
    private final ModelMapper modelMapper; // Entity-DTO 간 변환을 위한 ModelMapper 의존성 주입
    private final UserRepository userRepository;


    /**
     * 새로운 신고를 생성하고 저장하는 메서드
     * @param reportDTO 신고 데이터 전달 객체
     */
    @Override
    public void createReport(ReportDTO reportDTO) {
        log.info("신고 생성 요청: postId={}, userId={}, category={}, reason={}",
                reportDTO.getPostId(), reportDTO.getUserId(), reportDTO.getCategory(), reportDTO.getReason());

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

        // 신고 엔티티 생성 및 저장
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
     * 모든 신고를 게시글별로 그룹화하여 조회하고, 각 게시글에 대한 신고 횟수를 포함한 DTO 리스트로 반환
     * @return 게시글별 신고 목록 (ReportDTO 리스트)
     */
    @Override
    public List<ReportDTO> getAllReports() {
        // 모든 Report 엔티티를 조회
        List<Report> reports = reportRepository.findAll();

        // 게시글별로 그룹화하여 각 게시글에 대한 신고 횟수를 포함하는 DTO 리스트 생성
        return reports.stream()
                .collect(Collectors.groupingBy(Report::getPost)) // 게시글별로 그룹화
                .entrySet().stream()
                .map(entry -> {
                    Post post = entry.getKey();
                    List<Report> postReports = entry.getValue();
                    int reportCount = postReports.size();

                    // ReportDTO 생성 및 설정
                    ReportDTO reportDTO = new ReportDTO();
                    reportDTO.setPostId(post.getPostId());
                    reportDTO.setPostTitle(post.getTitle());
                    reportDTO.setReportCount(reportCount); // 신고 횟수 설정

                    return reportDTO; // ReportDTO 반환
                })
                .collect(Collectors.toList()); // 최종적으로 List<ReportDTO> 형태로 반환
    }

    /**
     * 진행 중인(PENDING 상태) 신고만 조회하여 DTO 리스트로 반환하는 메서드
     * @return 진행 중인 신고 목록 (ReportDTO 리스트)
     */
    @Override
    public List<ReportDTO> getReportsInProgress() {
        // PENDING 상태의 Report 엔티티만 조회
        List<Report> reports = reportRepository.findByStatus(Report.ReportStatus.PENDING);
        return reports.stream()
                .map(this::convertToReportDTO) // Report 엔티티를 ReportDTO로 변환
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시글에 대한 모든 신고를 조회하여 DTO 리스트로 반환하는 메서드
     * @param postId 게시글 ID
     * @return 특정 게시글에 대한 신고 목록 (ReportDTO 리스트)
     */
    @Override
    public List<ReportDTO> getReportsByPostId(Long postId) {
        // 특정 postId에 대한 모든 Report 엔티티를 조회
        List<Report> reports = reportRepository.findByPost_PostId(postId);
        return reports.stream()
                .map(this::convertToReportDTO) // Report 엔티티를 ReportDTO로 변환
                .collect(Collectors.toList());
    }

    /**
     * 특정 신고의 상태를 공개(VISIBLE)로 변경하는 메서드
     * @param reportId 상태를 변경할 신고 ID
     */
    @Override
    public void markAsVisible(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        log.info("변경 전 상태 (신고): {}", report.getStatus());
        report.setStatus(Report.ReportStatus.VISIBLE);  // Report 상태를 VISIBLE로 설정
        reportRepository.save(report);
        log.info("변경 후 상태 (신고): {}", report.getStatus());

        // 필요한 경우 Post 가시성도 설정
        Post post = report.getPost();
        if (!post.isVisible()) {
            post.setIsVisible(true); // 게시글을 공개로 설정
            postRepository.save(post);
        }
        log.info("신고글이 공개 처리되었습니다: {}", report);
    }

    @Override
    public void markAsHidden(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        log.info("변경 전 상태 (신고): {}", report.getStatus());
        report.setStatus(Report.ReportStatus.HIDDEN);  // Report 상태를 HIDDEN으로 설정
        reportRepository.save(report);
        log.info("변경 후 상태 (신고): {}", report.getStatus());

        // 필요한 경우 Post 가시성도 설정
        Post post = report.getPost();
        if (post.isVisible()) {
            post.setIsVisible(false); // 게시글을 비공개로 설정
            postRepository.save(post);
        }
        log.info("신고글이 비공개 처리되었습니다: {}", report);
    }

    /**
     * 특정 게시글의 신고 횟수를 반환하는 메서드
     * @param postId 게시글 ID
     * @return 신고 횟수
     */


    /**
     * Report 엔티티를 ReportDTO로 변환하는 메서드
     * @param report 변환할 Report 엔티티
     * @return 변환된 ReportDTO 객체
     */
    private ReportDTO convertToReportDTO(Report report) {
        ReportDTO reportDTO = modelMapper.map(report, ReportDTO.class);
        reportDTO.setPostTitle(report.getPost().getTitle()); // post의 title을 ReportDTO에 설정
        return reportDTO;
    }

    //신고 ID에 해당하는 게시글 ID를 반환
    @Override
    public Long getPostIdByReportId(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));
        return report.getPost().getPostId(); // 신고에 해당하는 게시글 ID 반환
    }
}