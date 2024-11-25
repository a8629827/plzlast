package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.Request;
import com.lyj.securitydomo.dto.RequestDTO;
import com.lyj.securitydomo.service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RequestController는 요청(Request)와 관련된 웹 요청을 처리하는 컨트롤러입니다.
 * 신청 저장, 조회, 삭제 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/request")
@Log4j2
public class RequestController {

    private final RequestService requestService;


    /**
     * 새로운 요청을 저장합니다.
     *
     * @param requestDTO 저장할 요청의 정보를 담은 DTO
     * @return 성공 메시지 또는 오류 메시지를 포함한 ResponseEntity
     */
    @PostMapping("/create")
    public ResponseEntity<String> createRequest(@RequestBody RequestDTO requestDTO) {
        log.info("새로운 요청 생성 시도: {}", requestDTO);

        try {
            // 요청 필수 데이터 검증 (예: postId와 content 확인)
            if (requestDTO.getPostId() == null || requestDTO.getPostId() <= 0) {
                log.error("유효하지 않은 게시글 ID: {}", requestDTO.getPostId());
                return ResponseEntity.badRequest().body("유효하지 않은 게시글 ID입니다.");
            }

            if (requestDTO.getContentText() == null || requestDTO.getContentText().trim().isEmpty()) {
                log.error("신청 사유가 비어 있습니다.");
                return ResponseEntity.badRequest().body("신청 사유를 입력해주세요.");
            }

            // RequestService를 통해 요청 저장
            requestService.createRequest(requestDTO);
            log.info("신청이 성공적으로 처리되었습니다. 게시글 ID: {}", requestDTO.getPostId(), requestDTO.getContentText());

            return ResponseEntity.ok("신청이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("신청 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("신청 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            // 기타 서버 오류 발생
            log.error("서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미 신청한 게시글입니다.");
        }
    }

    /**
     * 특정 게시물에 대한 요청 목록을 조회합니다.
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 대한 요청 목록 (RequestDTO 리스트 형태)
     */
    @GetMapping("/requests/{postId}")
    @ResponseBody
    public ResponseEntity<List<RequestDTO>> getRequestsByPostId(@PathVariable Long postId) {
        try {
            if (postId == null || postId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 게시물 ID입니다.");
            }
            List<RequestDTO> requests = requestService.getRequestsByPostId(postId);
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            log.error("요청 목록 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 현재 로그인한 사용자의 신청 목록을 조회합니다.
     * @param principal 현재 로그인한 사용자의 인증 정보
     * @return 해당 사용자가 신청한 요청 목록 (RequestDTO 리스트 형태)
     */
    @GetMapping("/requests/my")
    @ResponseBody
    public List<RequestDTO> getMyRequests(@AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        log.info("현재 로그인한 사용자 신청 목록 조회 - 사용자 ID: {}", userId);

        return requestService.getRequestsByUserId(userId);
    }

    /**
     * 요청을 ID를 기준으로 삭제합니다.
     * @param requestId 삭제할 요청의 ID
     * @return 성공 메시지 또는 오류 메시지를 포함한 ResponseEntity
     */
    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long requestId) {
        try {
            requestService.deleteRequest(requestId);
            return ResponseEntity.ok("신청이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 수락 처리 (POST)
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<String> approveRequest(@PathVariable Long requestId) {
        try {
            requestService.updateRequestStatus(requestId, Request.RequestStatus.APPROVED);
            return ResponseEntity.ok("Request approved successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
        }
    }

    // 거절 처리 (POST)
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectRequest(@PathVariable Long requestId) {
        try {
            requestService.updateRequestStatus(requestId, Request.RequestStatus.REJECTED);
            return ResponseEntity.ok("Request rejected successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
        }
    }
}