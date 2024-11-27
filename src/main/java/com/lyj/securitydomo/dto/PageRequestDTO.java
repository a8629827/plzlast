package com.lyj.securitydomo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1; // 현재 페이지 번호
    @Builder.Default
    private int size = 8; // 한 페이지에 보여줄 데이터 수

    private String type; // 검색의 종류
    private String keyword; // 검색어
    private Boolean isVisible; // 게시글 공개 여부

    private Long authorId; // 작성자 ID
    /**
     * 검색의 종류(type)를 쉼표(,)로 구분하여 배열로 반환
     *
     * @return 검색의 종류 배열
     */
    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split(","); // 여러 검색 타입을 쉼표로 구분하여 배열로 반환
    }

    /**
     * Pageable 객체를 반환하여 페이지 요청에 필요한 정보를 전달
     *
     * @param props 정렬 기준
     * @return Pageable 객체 (정렬 방식은 descending)
     */
    public Pageable getPageable(String... props) {
        // 기본적으로 내림차순 정렬, 오름차순 정렬을 지원할 수 있도록 수정 가능
        if (props.length > 1 && "asc".equals(props[1])) {
            return PageRequest.of(this.page - 1, this.size, Sort.by(props[0]).ascending()); // 오름차순 정렬
        }
        return PageRequest.of(this.page - 1, this.size, Sort.by(props[0]).descending()); // 내림차순 정렬
    }

    private String link; // 페이지 링크 저장

    /**
     * 페이지 링크를 생성하여 반환
     *
     * @return 생성된 페이지 링크
     */
    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=").append(this.page);
            builder.append("&size=").append(this.size);

            // 검색 유형(type)이 설정되어 있으면 링크에 추가
            if (type != null && !type.isEmpty()) {
                builder.append("&type=").append(type);
            }

            // 검색어(keyword)가 설정되어 있으면 URL 인코딩하여 추가
            if (keyword != null) {
                try {
                    builder.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // isVisible 필드 추가
            if (isVisible != null) {
                builder.append("&isVisible=").append(isVisible);
            }

            // authorId 필드 추가
            if (authorId != null) {
                builder.append("&authorId=").append(authorId);
            }

            link = builder.toString(); // 최종 링크 생성
        }
        return link; // 생성된 링크 반환
    }
}