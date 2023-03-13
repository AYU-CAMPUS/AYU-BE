package com.ay.exchange.board.facade;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.request.WriteRequest;

import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.entity.Board;

import com.ay.exchange.board.exception.FailWriteBoardException;
import com.ay.exchange.board.service.BoardContentService;
import com.ay.exchange.board.service.BoardService;


import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class BoardFacade {
    private final BoardService boardService;
    private final BoardContentService boardContentService;
    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(rollbackFor = Exception.class)
    public void writeBoard(WriteRequest writeRequest, MultipartFile multipartFile, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        //예외 발생 시 커스텀 예외를 사용하기 위해 로직들을 try문으로 감쌌는데 이게 옳은 지는 잘 모르겠다.
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String filePath = awsS3Service.buildFileName(originalFileName, email, 0);
            Board board = boardService.save(writeRequest, email, filePath, originalFileName);

            boardContentService.save(board, writeRequest.getContent());

            awsS3Service.uploadFile(multipartFile, filePath);
        } catch (Exception e) {
            throw new FailWriteBoardException();
        }
    }

    public BoardResponse getBoardList( //파라미터 값들을 dto로 묶는 것이 좀 더 깔끔해 보일 것 같다.
            Integer page,
            Integer category,
            String department,
            String grade,
            String type
    ) {
        Page<BoardInfoDto> pages = boardService.getBoardList(
                page,
                category,
                department,
                grade,
                type);

        return new BoardResponse(pages.getTotalPages(), pages.getContent());
    }
}
