package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.Reply;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.ReplyDTO;
import com.lyj.securitydomo.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Log4j2
@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor  // ReplyService를 생성자로 주입
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{postId}")
    public ResponseEntity<Long> createReply(@PathVariable Long postId, @RequestBody ReplyDTO replyDTO,@AuthenticationPrincipal PrincipalDetails principal) {
        log.info("ReplyDTO: " +replyDTO);
        // 로그인된 사용자 정보 설정
        replyDTO.setUsername(principal.getUsername());
        Reply reply=replyService.createReply(postId, replyDTO);
        return ResponseEntity.ok(reply.getReplyId());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Reply>> getReplies(
            @PathVariable Long postId
    ) {
        log.info("getReplies"+postId);
        List<Reply> replies = replyService.getReplies(postId);
        log.info(replies.size());
        return ResponseEntity.ok(replies);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<Void> modifyReply(@PathVariable Long replyId, @RequestBody ReplyDTO replyDTO) {
        replyService.modifyReply(replyId, replyDTO);
        return ResponseEntity.noContent().build();
    }

}