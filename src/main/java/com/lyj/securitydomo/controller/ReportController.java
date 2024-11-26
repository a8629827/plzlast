package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
import jakarta.validation.Valid; // 유효성 검증을 위한 import
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createReport(@Valid @RequestBody ReportDTO reportDTO) { // @Valid로 유효성 검증 추가
        log.info("신고 요청 수신: postId={}, userId={}, category={}, reason={}",
                reportDTO.getPostId(), reportDTO.getUserId(), reportDTO.getCategory(), reportDTO.getReason());

        try {
            // 서비스 호출하여 신고 처리
            reportService.createReport(reportDTO);
            log.info("신고가 성공적으로 처리되었습니다. 신고된 게시글 ID: {}", reportDTO.getPostId());
            return ResponseEntity.ok("신고가 접수되었습니다."); // 성공 메시지 반환
        } catch (IllegalStateException e) {
            // 중복 신고 또는 작성자 본인 신고 방지 예외 처리
            log.error("신고 처리 중 상태 오류: {}", e.getMessage());
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 데이터로 인한 예외 처리
            log.error("신고 처리 중 데이터 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body("신고 데이터가 유효하지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            // 기타 서버 오류 처리
            log.error("신고 처리 중 예기치 못한 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("신고 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
}