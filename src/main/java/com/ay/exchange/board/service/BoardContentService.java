package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public BoardContentResponse getBoardContent(Long boardId, String token) {
        PageRequest pageRequest = PageRequest.of(0, 2,
                Sort.by(Sort.Direction.DESC, "id"));

        return boardContentRepository.findBoardContent(boardId, pageRequest, jwtTokenProvider.getUserId(token));
    }

//    private boolean isAuthorized(String token) { //삭제기능에서 이 메소드가 무조건 사용됨 중복코드 나중에 리팩토링
//        return jwtTokenProvider.getEmail(token).equals(token);
//    }
}