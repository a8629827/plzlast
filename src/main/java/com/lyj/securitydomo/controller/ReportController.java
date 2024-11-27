package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
import jakarta.validation.Valid; // 유효성 검증을 위한 import
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController // RESTful API 제공
@RequestMapping("report") // RESTful API URL 기본 경로
@RequiredArgsConstructor
@Log4j2
public class ReportController {

    private final ReportService reportService;

    /**
     * 신고 생성
     * @param reportDTO 신고 데이터
     * @return 성공 또는 실패 메시지
     */
    @PostMapping("/create")
    public ResponseEntity<String> createReport(
            @RequestBody ReportDTO reportDTO,
            @AuthenticationPrincipal PrincipalDetails principal) {

        log.info("신고 요청 수신: postId={}, category={}, reason={}",
                reportDTO.getPostId(), reportDTO.getCategory(), reportDTO.getReason());

        try {
            // 현재 로그인된 사용자 ID를 설정
            Long currentUserId = principal.getUser().getUserId();
            reportDTO.setUserId(currentUserId); // ReportDTO에 사용자 ID 추가
            log.info("신고 처리 중 사용자 ID 설정: {}", currentUserId);

            // 필수 데이터 검증
            if (reportDTO.getPostId() == null || reportDTO.getPostId() <= 0) {
                log.error("유효하지 않은 게시글 ID: {}", reportDTO.getPostId());
                return ResponseEntity.badRequest().body("유효하지 않은 게시글 ID입니다.");
            }
            if (reportDTO.getReason() == null || reportDTO.getReason().trim().isEmpty()) {
                log.error("신고 사유가 비어 있습니다.");
                return ResponseEntity.badRequest().body("신고 사유를 입력해주세요.");
            }

            // ReportService를 사용하여 신고 생성
            reportService.createReport(reportDTO);
            log.info("신고가 성공적으로 처리되었습니다. 게시글 ID: {}", reportDTO.getPostId());

            return ResponseEntity.ok("신고가 접수되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("신고 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("신고 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("신고 처리 중 문제가 발생했습니다. 다시 시도해주세요.");
        }
    }
}