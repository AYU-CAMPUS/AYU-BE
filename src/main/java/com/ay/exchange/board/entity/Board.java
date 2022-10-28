package com.ay.exchange.board.entity;

import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
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

//    @ManyToOne(fetch = FetchType.LAZY) jwt에서 id를 확인하는데 굳이 연관관계를 맺을 필요가 있을까?
//    @JoinColumn(name = "user_id")
//    private User user;

    @Column(nullable = false)
    private String userId;
}