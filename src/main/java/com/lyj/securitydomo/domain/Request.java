package com.lyj.securitydomo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="user_id",nullable = false) // 외래키 매핑
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="post_id",nullable = false) // 외래키 매핑
    private Post post;

//    private String title;

    @Column(nullable = false,length = 2000)
    private String content;

    @Enumerated(EnumType.STRING) // 문자열로 저장
    @Column(nullable = false)
    private Request.RequestStatus status; //신청 진행 상태(PENDING,APPROVED,REJECTED)

    public enum RequestStatus {
        PENDING,   // 승인 대기 중
        APPROVED,  // 승인
        REJECTED   // 거절
    }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern ="yyyy-MM-dd")
    private Date regDate;





}