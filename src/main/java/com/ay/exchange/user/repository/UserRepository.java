package com.ay.exchange.user.repository;

import com.ay.exchange.user.dto.query.UserIdDto;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.dto.response.SignInResponse;
import com.ay.exchange.user.dto.response.SignUpResponse;
import com.ay.exchange.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);

    boolean existsByUserId(String userId);

    Optional<UserInfoDto> findUserInfoByUserId(String userId);

    Optional<UserIdDto> findUserIdByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User SET password=:password WHERE email=:email", nativeQuery = true)
    void updatePassword(
            @Param("email") String email
            , @Param("password")String password
    );
}
