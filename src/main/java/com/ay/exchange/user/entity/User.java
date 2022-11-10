package com.ay.exchange.user.entity;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.common.entity.BaseEntity;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.user.entity.vo.Authority;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends BaseEntity {
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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY,orphanRemoval = true)
    private Exchange exchange;

//    @OneToMany(mappedBy = "user")
//    private List<Board> boards;

}
