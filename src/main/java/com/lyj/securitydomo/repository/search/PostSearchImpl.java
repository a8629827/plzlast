
package com.lyj.securitydomo.repository.search;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.QPost;

import com.lyj.securitydomo.domain.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {

    public PostSearchImpl() {
        super(Post.class);
    }

    /**
     * 동적 검색 메서드: 게시글의 다양한 조건을 기반으로 검색합니다.
     *
     * @param types 검색 타입 배열 (예: 제목, 내용 등)
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @param isVisible 게시글의 가시성 필터 (null: 모든 게시글, true: 공개, false: 비공개)
     * @return 조건에 맞는 게시글 페이지 결과
     */
    @Override
    public Page<Post> searchAll(String[] types, String keyword, Pageable pageable, Boolean isVisible) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        JPQLQuery<Post> query = from(post)
                .leftJoin(post.user, user)
                .fetchJoin();

        // 검색 조건을 적용
        if (types != null && types.length > 0 && keyword != null) {
            BooleanBuilder builder = new BooleanBuilder();

            // 각 검색 타입에 따라 조건을 추가
            for (String type : types) {
                switch (type) {
                    case "t": // 제목 검색
                        builder.or(post.title.contains(keyword));
                        break;
                    case "c": // 내용 검색
                        builder.or(post.contentText.contains(keyword));
                        break;
                    case "u": // 유저 아이디 검색
                        builder.or(user.username.contains(keyword));
                        break;
                }
            }
            query.where(builder); // 검색 조건 적용
        }

        // 가시성 필터를 동적으로 추가
        if (isVisible != null) {
            query.where(post.isVisible.eq(isVisible)); // true 또는 false에 따라 필터링
            log.info("레포지토리에서 비공개 처리: {}", isVisible);
        }

        query.where(post.postId.gt(0L)); // 기본 조건: postId가 0보다 큰 게시글만 조회
        this.getQuerydsl().applyPagination(pageable, query); // 페이지네이션 적용

        List<Post> list = query.fetch(); // 결과 리스트 조회
        long total = query.fetchCount(); // 전체 게시글 수 조회
        log.info("Fetched posts: {}", list); //조횓된 게시글 로그 출력
        return new PageImpl<>(list, pageable, total); // PageImpl로 반환하여 페이지 정보 포함
    }
}
