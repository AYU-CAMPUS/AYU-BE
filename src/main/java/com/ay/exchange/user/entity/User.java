package com.ay.exchange.user.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.common.entity.BaseEntity;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.entity.ExchangeCompletion;
import com.ay.exchange.user.entity.vo.Authority;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends BaseEntity implements Persistable<String> {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Temporal(TemporalType.DATE)
    private Date suspendedDate;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(nullable = false)
    private Integer exchangeSuccessCount;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Exchange> exchanges;

    @OneToMany(mappedBy = "requesterUser", orphanRemoval = true)
    private List<Exchange> requesterExchanges;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<ExchangeCompletion> exchangeCompletions;

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }

}