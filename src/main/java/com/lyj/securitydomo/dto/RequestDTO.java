
package com.lyj.securitydomo.dto;

import com.lyj.securitydomo.domain.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Long requestId;
    private Long postId;
    private String postTitle;//게시글 제목
    private String content;
    private Long userId;
    private String username; // 신청자 이름
    private String contentText;
    private String status;
    private Date regDate;

    // Request 엔티티를 받아 DTO를 초기화하는 생성자 추가
    public RequestDTO(Request request) {
        this.requestId = request.getRequestId();
        this.postId = request.getPost().getPostId();
        this.userId = request.getUser().getUserId();
        this.username = request.getUser().getUsername(); // 신청자 이름 설정
        this.postTitle = request.getPost().getTitle(); // Post의 제목 가져오기
        this.contentText = request.getContent();
        this.status = request.getStatus().toString();
        this.regDate = request.getRegDate();
    }

}