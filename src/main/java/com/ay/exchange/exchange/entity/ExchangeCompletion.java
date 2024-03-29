package com.ay.exchange.exchange.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "constraintExchangeCompletion",
                        columnNames = {"email", "requester_board_id"}
                )
        }
)
public class ExchangeCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_completion_id")
    private Long Id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board; //다운 가능한 게시물

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_board_id", nullable = false, insertable = false, updatable = false)
    private Board requesterBoard; //다운 가능한 게시물

    @Column(name = "requester_board_id", nullable = false)
    private Long requesterBoardId; //요청자 게시물 번호

    @Column(nullable = false)
    private String date;

}