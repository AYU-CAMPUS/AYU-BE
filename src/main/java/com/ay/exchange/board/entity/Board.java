package com.ay.exchange.board.entity;

import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.common.entity.BaseEntity;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.entity.ExchangeCompletion;
import com.ay.exchange.management.entity.ModificationBoard;
import com.ay.exchange.report.entity.ReportBoard;
import com.ay.exchange.user.entity.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Embedded
    private BoardCategory boardCategory;

    @Column(nullable = false)
    private Integer numberOfFilePages;

    @Column(nullable = false)
    private Integer exchangeSuccessCount;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private Integer approval;

    @OneToOne(mappedBy = "board", fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BoardContent boardContent;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Exchange> exchanges;

    @OneToMany(mappedBy = "requesterBoard", fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Exchange> requesterExchanges;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ReportBoard> reportBoards;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ExchangeCompletion> exchangeCompletions;

    @OneToMany(mappedBy = "requesterBoard", orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ExchangeCompletion> requesterExchangeCompletions;

    @OneToOne(mappedBy = "board", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ModificationBoard modificationBoard;
}