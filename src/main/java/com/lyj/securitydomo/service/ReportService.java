package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.dto.ReportDTO;
import java.util.List;

public interface ReportService {
    void createReport(ReportDTO reportDTO); // 신고 생성
    List<ReportDTO> getAllReports(); // 모든 신고 조회
    List<ReportDTO> getReportsInProgress(); // 진행 중인 신고 조회
    List<ReportDTO> getReportsByPostId(Long postId); // 특정 게시글의 신고 조회
    void markAsVisible(Long reportId); // 공개 처리
    void markAsHidden(Long reportId); // 비공개 처리
//    int countReportsByPostId(Long postId); // 특정 게시글의 신고 횟수 조회
    Long getPostIdByReportId(Long reportId); // 신고 ID로 게시글 ID 반환

}