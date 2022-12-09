package com.ay.exchange.management.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.BoardCategory;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "constraintModificationBoard",
                        columnNames = {"board_id"}
                )
        }
)
public class ModificationBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "modification_board_id")
    private Long id;

    @OneToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(length = 200, nullable = false)
    private String title;

    private BoardCategory boardCategory;

    @Column(nullable = false)
    private Integer numberOfFilePages;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String date;
}