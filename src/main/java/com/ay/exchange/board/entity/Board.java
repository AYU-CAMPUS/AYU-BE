package com.ay.exchange.board.entity;

import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.user.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String writer;

    @Embedded
    private BoardCategory boardCategory;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer numberOfFilePages;

    @Column(nullable = false)
    private Integer numberOfSuccessfulExchanges;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private String createdDate;

    @Column(nullable = false)
    private Boolean approval;

    @OneToOne(mappedBy = "board", fetch = FetchType.LAZY,
            orphanRemoval = true)
    private BoardContent boardContent;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<Exchange> exchanges;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private String userId;
}