package com.ay.exchange.report.entity;

import com.ay.exchange.board.entity.Board;
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
                        name = "constraintReportBoard",
                        columnNames = {"board_id", "email"}
                )
        }
)
public class ReportBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_board_id")
    private Long id;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", insertable = false, updatable = false, nullable = false)
    private Board board;

    @Column(name = "board_id", nullable = false)
    private Long boardId; //신고 대상 글

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", insertable = false, updatable = false, nullable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 200)
    private String email; //신고자 아이디

    @Column(length = 100, nullable = false)
    private String reason; //사유

    private String date;

}