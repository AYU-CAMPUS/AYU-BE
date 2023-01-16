package com.ay.exchange.exchange.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.common.entity.BaseEntity;
import com.ay.exchange.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "constraintExchange",
                        columnNames = {"requester_board_id", "email", "requester_email"}
                )
        }
)
public class Exchange extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_id")
    private Long Id;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board; //현재 게시물

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "email", nullable = false, insertable = false, updatable = false)
    private User user; //게시물 작성자

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "requester_board_id", nullable = false, insertable = false, updatable = false)
    private Board requesterBoard; //요청자 게시물

    @Column(name = "requester_board_id", nullable = false)
    private Long requesterBoardId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "requester_email", nullable = false, insertable = false, updatable = false)
    private User requesterUser; //요청자 아이디

    @Column(name = "requester_email", nullable = false)
    private String requesterEmail;

    @Column(nullable = false)
    private Integer type;

}