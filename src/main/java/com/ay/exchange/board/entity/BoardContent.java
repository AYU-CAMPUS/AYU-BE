package com.ay.exchange.board.entity;

import com.ay.exchange.comment.entity.Comment;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_content_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private String filePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


}
