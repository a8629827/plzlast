package com.lyj.securitydomo.dto;

import com.lyj.securitydomo.dto.upload.UploadResultDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 게시글(Post) 데이터 전송 객체
 * - 서비스 계층과 컨트롤러 간 데이터 전달에 사용
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDTO {

    private Long postId; // 게시글 ID

    @NotEmpty
    @Size(min = 3, max = 200)
    private String title; // 제목

    @NotEmpty
    private String contentText; // 게시글 본문 내용

    private Date createdAt; // 등록 날짜

    private Date updatedAt; // 수정 날짜

    private List<String> fileNames; // 파일 이름 리스트 (썸네일 및 원본 파일 이름)

    private Integer requiredParticipants; // 모집 인원

    private String status; // 모집 상태 (모집중 또는 모집완료)

    private String author; // 작성자 정보

    private List<String> originalImageLinks; // 원본 이미지 링크 리스트

    private String thumbnailLink; // 썸네일 이미지 URL

    private double lat; // 위도

    private double lng; // 경도

    @Builder.Default
    private boolean isVisible = true; // 기본값은 true (공개 상태)

    @DateTimeFormat(pattern = "yyyy-MM-dd") // HTML에서 yyyy-MM-dd 형식으로 넘어오는 데이터를 변환
    private Date deadline; // 모집 마감일

    private boolean firstComeFirstServe; // 선착순 여부

    private int replyCount; // 댓글 수

    private Integer reportCount; // 신고 건수
    /**
     * 썸네일 이미지 링크를 반환합니다.
     * - 업로드된 이미지가 있으면 첫 번째 이미지 링크를 반환
     * - 업로드된 이미지가 없으면 랜덤 이미지를 반환
     *
     * @return 썸네일 이미지 링크
     */
    public String getThumbnail() {
        if (fileNames != null && !fileNames.isEmpty()) {
            return "/view/s_" + fileNames.get(0);
        } else {
            return UploadResultDTO.getRandomImage();
        }
    }

    /**
     * 원본 이미지 링크 리스트를 반환합니다.
     *
     * @return 원본 이미지 링크 리스트
     */
    public List<String> getOriginalImageLinks() {
        return this.originalImageLinks;
    }

}