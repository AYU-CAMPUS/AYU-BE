package com.ay.exchange.comment.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.user.entity.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", updatable = false, insertable = false, nullable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean depth;

    private Long groupId;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdDate;

}