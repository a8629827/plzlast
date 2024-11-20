

package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.domain.Reply;
import com.lyj.securitydomo.dto.ReplyDTO;
import com.lyj.securitydomo.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ReplyDTO> createReply(@PathVariable Long postId, @RequestBody ReplyDTO replyDTO) {
        log.info("댓글 등록 요청: postId={}, replyDTO={}", postId, replyDTO);
        replyService.createReply(postId, replyDTO);
        return ResponseEntity.ok(replyDTO);
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
    public ResponseEntity<Void> modifyReply(@PathVariable Long replyId, @RequestBody String content) {
        replyService.modifyReply(replyId, content);
        return ResponseEntity.noContent().build();
    }

}
