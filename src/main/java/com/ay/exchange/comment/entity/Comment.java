package com.ay.exchange.comment.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.user.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "constraintComment",
                        columnNames = {"user_id"}
                )
        }
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String writer;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn( name = "user_id", updatable = false, insertable = false, nullable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean depth;

    @Column(nullable = false)
    private Long groupId;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdDate;

}
