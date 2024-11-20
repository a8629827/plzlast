//1. 레포지토리 (Repository):
//
//	•	목적: 데이터베이스와의 직접적인 상호작용을 담당
//	•	기능: CRUD(Create, Read, Update, Delete) 작업을 처리
//	•	구체적 구현: 보통 JpaRepository, CrudRepository 등 Spring Data JPA에서 제공하는 인터페이스를 상속받아 사용
//	•	예시: findAll(), findById(), save(), deleteById() 등
//
//레포지토리에서는 데이터베이스에 직접 접근하는 기능만 제공하며, create 같은 기능은 save()를 통해 간단하게 처리합니다.
// Spring Data JPA는 기본적인 CRUD 메서드를 자동으로 생성해 주기 때문에, create 메서드는 사실상 save()로 대체됩니다.
package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 모든 신고를 가져오는 기본 메서드
    List<Report> findAll();

    // 특정 상태의 신고를 가져오는 메서드 (예: PENDING, HIDDEN, VISIBLE)
    List<Report> findByStatus(Report.ReportStatus status);

    // 특정 postId에 해당하는 신고들을 조회하는 메서드
    List<Report> findByPost_PostId(Long postId);

    // 특정 게시글의 신고 횟수를 조회하는 메서드
//    int countByPost_PostId(Long postId);

    // 특정 reportId에 해당하는 신고의 상태를 업데이트 (실제로 save로 처리됨)
    // JPA에서 기본적으로 제공되는 save()를 사용하여 상태를 갱신할 수 있음
    Report save(Report report);

    // 특정 게시글과 사용자 조합으로 중복 신고 여부 확인
    boolean existsByPostAndUser(Post post, User user);
}
