package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Request;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.RequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {


    /**
     * 특정 게시물에 대한 요청 목록을 조회합니다.
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 대한 요청 목록
     */
    List<Request> findByPost_PostId(Long postId);

    /**
     * 특정 사용자가 신청한 요청 목록을 조회합니다.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자가 신청한 요청 목록
     */
    List<Request> findByUser_UserId(Long userId);

    /**
     * 특정 게시물과 사용자 조합으로 신청 여부를 확인하는 메서드.
     * @param post 대상 게시물 객체
     * @param user 대상 사용자 객체
     * @return 해당 게시물에 대해 사용자가 이미 신청한 경우 true, 그렇지 않으면 false
     *
     * 예) 신청 중복 방지 기능 구현 시 사용.
     */
    boolean existsByPostAndUser(Post post, User user);
}