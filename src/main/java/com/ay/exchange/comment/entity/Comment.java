package com.ay.exchange.comment.entity;

import com.ay.exchange.board.entity.Board;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean depth;

    @Column(nullable = false)
    private Long groupId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private String createdDate;

}
