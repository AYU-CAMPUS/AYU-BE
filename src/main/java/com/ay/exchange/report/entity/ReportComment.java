package com.ay.exchange.report.entity;


import com.ay.exchange.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "constraintReportComment",
                        columnNames = {"user_id", "target_user_id", "content"}
                )
        }
)
public class ReportComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private String userId; //신고자 아이디

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", insertable = false, updatable = false, nullable = false)
    private User targetUser;

    @Column(name = "target_user_id", nullable = false)
    private String targetUserId; //신고자 아이디

    @Column(length = 100, nullable = false)
    private String reason; //사유

    private String content; //댓글 내용

    private String date;
}