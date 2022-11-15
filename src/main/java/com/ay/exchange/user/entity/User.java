package com.ay.exchange.user.entity;

import com.ay.exchange.common.entity.BaseEntity;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.user.entity.vo.Authority;
import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;

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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Exchange exchange;

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }

//    @OneToMany(mappedBy = "user")
//    private List<Board> boards;

}
