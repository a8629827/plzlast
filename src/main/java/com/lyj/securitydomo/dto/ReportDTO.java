package com.lyj.securitydomo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long reportId;
    private Long postId; // Post와의 관계
    private String reason; // 신고 사유
    private String status; // 신고 진행 상태 (PENDING, HIDDEN, VISIBLE)
    private String category; // 신고 분류
    private Date createdAt; // 생성 날짜
    private String postTitle;//게시글 제목
    private int reportCount; // 신고 횟수
    private boolean isVisible; // 게시글의 공개/비공개 상태 (관리자용)
    private Long userId;       // 신고를 생성한 사용자 ID

}