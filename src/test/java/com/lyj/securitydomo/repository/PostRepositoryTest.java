//package com.lyj.securitydomo.repository;
//
//import com.lyj.securitydomo.domain.Post;
//import com.lyj.securitydomo.domain.User;
//import groovy.util.logging.Log4j2;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.IntStream;
//
//@SpringBootTest
//@Log4j2
//public class PostRepositoryTest {
//
//    private static final Logger log = LogManager.getLogger(PostRepositoryTest.class);
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private UserRepository userRepository; // UserRepository 주입
//
//    @Test
//    public void testDeleteAll() {
//        postRepository.deleteAll(); // 모든 게시글 삭제
//    }
//
////    @Test
////    public void testInsert() {
////        Long userId = 1L; // 실제로 존재하는 사용자 ID
////
////        // User 엔티티를 영속화된 상태로 가져오기
////        User user = userRepository.findById(userId).orElseThrow();
////
////        IntStream.rangeClosed(1, 10).forEach(i -> {
////            Post post = Post.builder()
////                    .title("title" + i)
////                    .contentText("content" + i)
////                    .author(user) // 가져온 사용자 정보를 사용
////                    .requiredParticipants(i + 5) // 모집 인원 설정 (예: 6~15)
////                    .status(Post.Status.모집중) // 모집 상태 설정
////                    .build();
////
////            Post result = postRepository.save(post);
////            log.info(result);
////        });
////    }
//
//    @Test
//    public void testSelect() {
//        Long postId = 10L;
//
//        Optional<Post> result = postRepository.findById(postId);
//
//        Post post = result.orElseThrow();
//        log.info(post);
//    }
//
//
//
//
//
//        @Test
//        public void testFindAll() {
//            List<Post> posts = postRepository.findAll();
//            if (posts.isEmpty()) {
//                log.info("No posts found.");
//            } else {
//                log.info("Posts found: {}", posts);
//            }
//        }
//    }
