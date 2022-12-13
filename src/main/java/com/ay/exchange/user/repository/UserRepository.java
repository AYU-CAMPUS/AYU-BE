package com.ay.exchange.user.repository;

import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {

    //boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);

    //boolean existsByUserId(String userId);

    Optional<UserInfoDto> findUserInfoByEmail(String email);

//    Optional<UserIdDto> findUserIdByEmail(String email);
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE User SET password=:password WHERE email=:email", nativeQuery = true)
//    void updatePassword(
//            @Param("email") String email
//            , @Param("password")String password
//    );
}
