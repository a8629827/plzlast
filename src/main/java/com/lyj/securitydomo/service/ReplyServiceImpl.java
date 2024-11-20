package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Reply;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.ReplyDTO;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.ReplyRepository;
import com.lyj.securitydomo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Reply createReply(Long postId, ReplyDTO replyDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 작성자 정보 조회
        User user = userRepository.findByUsername(replyDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Reply reply = new Reply();
        reply.setPost(post);
        reply.setUsername(replyDTO.getUsername());
        reply.setContent(replyDTO.getContent());

        if (replyDTO.getParentId() != null) {
            Reply parentReply = replyRepository.findById(replyDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent reply not found"));
            reply.setParent(parentReply);
        }


        return replyRepository.save(reply);
    }

    @Override
    public void modifyReply(Long replyId,ReplyDTO replyDTO) {
        Optional<Reply> optionalReply = replyRepository.findById(replyId);
        Reply reply = optionalReply.orElseThrow(() -> new RuntimeException("Reply not found"));
        reply.changText(replyDTO.getContent());
        replyRepository.save(reply);
    }

    @Override
    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    @Override
    public List<Reply> getReplies(Long postId) {
        // 게시글을 찾습니다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 댓글 조회 및 필터링
        List<Reply> replies = replyRepository.findByPostAndParentIsNull(post);
        replies.forEach(reply -> {
            log.info(reply.toString());
        });

        // Reply 엔티티를 ReplyDTO로 변환
        return replies;
    }

}