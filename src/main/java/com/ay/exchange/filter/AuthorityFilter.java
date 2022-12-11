//package com.ay.exchange.filter;
//
//import com.ay.exchange.jwt.JwtTokenProvider;
//import com.ay.exchange.user.entity.vo.Authority;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Component
//@RequiredArgsConstructor
//public class AuthorityFilter {
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public boolean isAdmin(String token
//            , HttpServletRequest request){
//        Authority authority =jwtTokenProvider.getAuthority(token);
//        if(authority!=null && authority.equals("Admin")){
//            return true;
//        }
//        request.setAttribute("exception","AuthorizeError");
//        return false;
//    }
//
//    public boolean isMaster(String token
//            , HttpServletRequest request){
//        Authority authority =jwtTokenProvider.getAuthority(token);
//        if(authority!=null && authority.equals("Master")){
//            return true;
//        }
//        request.setAttribute("exception","AuthorizeError");
//        return false;
//    }
//
//
//
//}
