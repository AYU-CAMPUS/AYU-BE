package com.ay.exchange.report.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.comment.entity.Comment;
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
                        name = "constraintReportBoard",
                        columnNames = {"comment_id", "user_id"}
                )
        }
)
public class ReportComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false, nullable = false)
    private Comment comment;

    @Column(name = "comment_id", nullable = false)
    private Long commentId; //신고 대상 댓글 아이디

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId; //신고자 아이디

    @Column(length = 100, nullable = false)
    private String reason; //사유

    private String content; //댓글 내용

    private String date;
}