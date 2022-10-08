package com.ay.exchange.board.entity;

import com.ay.exchange.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_content_id")
    private Long id;

    @OneToMany(mappedBy = "boardContent"
            , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment>comments;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private String filePath;

    @OneToOne(fetch = FetchType.LAZY
            , cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private Board board;

}
