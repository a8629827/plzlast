package com.lyj.securitydomo.service;

import com.lyj.securitydomo.dto.ReplyDTO;
import com.lyj.securitydomo.domain.Reply;

import java.util.List;

public interface ReplyService {
    public Reply createReply(Long postId, ReplyDTO replyDTO);

    public void modifyReply(Long replyId, ReplyDTO replyDTO);

    public void deleteReply(Long replyId);

    public List<Reply> getReplies(Long postId);  // 반환 타입을 List<ReplyDTO>로 변경
}