package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
@Log4j2
public class ReportController {

    private final ReportService reportService;


////@RequestBody, @RequestParam:요청 파라미터
////(@RequestParam Long id String name) 형태 -> "/create?name="홍길동"&id=hkd 하나하나씩보낸다
////(@RequestBody ReportDTO) 몸통으로 전체를 받겟다,insert일때, form으로 통째로)
////JSON타입으로 받아서 ReportDTO에서 객체로 받음
@PostMapping("/create")
public ResponseEntity<String> createReport(@RequestBody ReportDTO reportDTO) {
    log.info("신고 요청 수신: postId={}, userId={}, category={}, reason={}",
            reportDTO.getPostId(), reportDTO.getUserId(), reportDTO.getCategory(), reportDTO.getReason());

    try {
        // 서비스 호출하여 신고 처리
        reportService.createReport(reportDTO);
        log.info("신고가 성공적으로 처리되었습니다.");
        return ResponseEntity.ok("신고가 접수되었습니다."); // 성공 메시지 반환
    } catch (IllegalStateException e) {
        // 중복 신고 또는 작성자 본인 신고 방지 예외 처리
        log.error("신고 처리 중 상태 오류: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalArgumentException e) {
        // 유효하지 않은 데이터로 인한 예외 처리
        log.error("신고 처리 중 데이터 오류: {}", e.getMessage());
        return ResponseEntity.badRequest().body("신고 데이터가 유효하지 않습니다: " + e.getMessage());
    } catch (Exception e) {
        // 기타 서버 오류 처리
        log.error("신고 처리 중 예기치 못한 오류 발생: {}", e.getMessage());
        return ResponseEntity.badRequest().body("신고 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
}

//    @GetMapping("/list")
//    public ResponseEntity<List<ReportDTO>> getReports() {
//        try {
//            List<ReportDTO> reports = reportService.getAllReports(); // 모든 신고 리스트 가져오기
//            if (reports.isEmpty()) {
//                return ResponseEntity.noContent().build(); // 신고 리스트가 비어있을 경우 204 반환
//            }
//            return ResponseEntity.ok(reports); // 조회된 리스트 반환
//        } catch (Exception e) {
//            log.error("Error retrieving report list", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 오류 반환
//        }
//    }

}