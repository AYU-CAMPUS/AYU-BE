package com.ay.exchange.exchange.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.common.entity.BaseEntity;
import com.ay.exchange.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
                        columnNames = {"board_id", "requester_board_id", "user_id", "requester_user_id"}
                )
        }
)
public class Exchange extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_id")
    private Long Id;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_board_id", nullable = false, insertable = false, updatable = false)
    private Board requesterBoard;

    @Column(name = "requester_board_id", nullable = false)
    private Long requesterBoardId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false, insertable = false, updatable = false)
    private User requesterUser;

    @Column(name = "requester_user_id", nullable = false)
    private String requesterUserId;

    @Column(nullable = false)
    private Integer type;

}