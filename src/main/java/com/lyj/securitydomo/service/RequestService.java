package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Request;
import com.lyj.securitydomo.dto.RequestDTO;

import java.util.List;

/**
 * RequestService는 요청과 관련된 주요 기능을 정의하는 인터페이스입니다.
 */
public interface RequestService {


//    List<RequestDTO> getRequests();


    void createRequest(RequestDTO requestDTO);

//    /**
//     * 게시물 ID, 제목, 내용을 기반으로 새로운 요청을 생성하고 저장합니다.
//     * @param postId 게시물 ID
//     * @param title 요청 제목
//     * @param content 요청 내용
//     */
//    void saveRequest(long postId, String title, String content);

    /**
     * 특정 게시물에 대한 요청 목록을 조회하여 RequestDTO 리스트로 반환합니다.
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 대한 요청 목록 (RequestDTO 리스트)
     */
    List<RequestDTO> getRequestsByPostId(Long postId);

    /**
     * 특정 사용자가 신청한 요청 목록을 조회하여 RequestDTO 리스트로 반환합니다.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자가 신청한 요청 목록 (RequestDTO 리스트)
     */
    List<RequestDTO> getRequestsByUserId(Long userId);

    /**
     * 특정 요청을 삭제합니다.
     * @param requestId 삭제할 요청의 ID
     */
    void deleteRequest(Long requestId);

    // 수락 또는 거절 상태를 변경하는 메소드
    void updateRequestStatus(Long requestId, Request.RequestStatus status);
}