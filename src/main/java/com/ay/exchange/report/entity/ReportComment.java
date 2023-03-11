package com.ay.exchange.report.entity;


import com.ay.exchange.user.entity.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
                        columnNames = {"email", "target_email"}
                )
        }
)
public class ReportComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", insertable = false, updatable = false, nullable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 200)
    private String email; //신고자 아이디

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "target_email", insertable = false, updatable = false, nullable = false)
    private User targetUser;

    @Column(name = "target_email", nullable = false, length = 200)
    private String targetEmail; //신고자 아이디

    @Column(length = 100, nullable = false)
    private String reason; //사유

    private String content; //댓글 내용

    private String date;
}