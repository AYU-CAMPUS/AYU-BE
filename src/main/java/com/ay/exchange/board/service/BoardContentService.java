package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    //추후 accessToken 권한 검증
    public void deleteBoard(String accessToken, DeleteRequest deleteRequest) {
        boardContentRepository.deleteById(deleteRequest.getBoardContentId());
//        if(isAuthorized(token)){
//            boardContentRepository.deleteByBoardId(deleteRequest.getBoardId());
//        }else{
//            throw new InvalidUserRoleException();
//        }
    }

//    private boolean isAuthorized(String token) { //삭제기능에서 이 메소드가 무조건 사용됨 중복코드 나중에 리팩토링
//        return jwtTokenProvider.getEmail(token).equals(token);
//    }
}