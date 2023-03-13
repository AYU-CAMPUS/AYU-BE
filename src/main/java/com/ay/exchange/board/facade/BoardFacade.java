package com.ay.exchange.board.facade;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.dto.request.WriteRequest;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;

import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.exception.FailWriteBoardException;
import com.ay.exchange.board.service.BoardContentService;
import com.ay.exchange.board.service.BoardService;


import com.ay.exchange.board.service.ModificationBoardService;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.ay.exchange.common.util.DateUtil.getAvailableDate;


@Service
@RequiredArgsConstructor
public class BoardFacade {
    private final BoardService boardService;
    private final BoardContentService boardContentService;
    private final ModificationBoardService modificationBoardService;
    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final int UPLOAD_FILE = 0;

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
            e.printStackTrace();
            throw new FailWriteBoardException();
        }
    }

    public BoardResponse getBoardList(
            Integer page,
            Integer category,
            String department,
            String grade,
            String type
    ) { //파라미터 값들을 dto로 묶는 것이 좀 더 깔끔해 보일 것 같다.
        Page<BoardInfoDto> pages = boardService.getBoardList(
                page,
                category,
                department,
                grade,
                type);

        return new BoardResponse(pages.getTotalPages(), pages.getContent());
    }

    public BoardContentResponse getBoardContent(Long boardId, String token) {
        if (token == null) { //로그인하지 않은 유저여도 게시글은 볼 수 있다.
            return boardContentService.getBoardContent(boardId, "");
        }
        return boardContentService.getBoardContent(boardId, jwtTokenProvider.getUserEmail(token));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBoard(String token, DeleteRequest deleteRequest) {
        String email = jwtTokenProvider.getUserEmail(token);

        boardContentService.checkDeleteable(email, deleteRequest.getBoardId()); //삭제 가능한 지

        String filePath = boardService.findFilePathByBoardId(deleteRequest.getBoardId());

        boardService.delete(email, deleteRequest.getBoardId());

        awsS3Service.deleteUserFile(filePath);
    }

    public ModifiableBoardResponse findModifiableBoard(String token, Long boardId) {
        String email = jwtTokenProvider.getUserEmail(token);

        checkModifiable(email, boardId);

        ModifiableBoardResponse modifiableBoardResponse = boardContentService.findModifiableBoard(email, boardId);
        if (modifiableBoardResponse == null) {
            throw new FailModifyBoardException();
        }
        return modifiableBoardResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    public void requestModificationBoard(ModificationRequest modificationRequest, MultipartFile multipartFile, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        //수정 가능한 지 확인
        checkModifiable(email, modificationRequest.getBoardId());
        boardService.updateApproval(email, modificationRequest.getBoardId()); //기존 게시글은 삭제하지 않고 approval만 수정해서 게시글 목록에 조회되지 않도록 한다.

        if (multipartFile == null) { //기존 파일을 유지하고 내용만 변경
            modificationBoardService.save(modificationRequest, email, null, null);
            return;
        }

        //파일도 변경
        String filePath = awsS3Service.buildFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()), email, UPLOAD_FILE);
        modificationBoardService.save(modificationRequest, email, multipartFile.getOriginalFilename(), filePath);
        awsS3Service.uploadFile(multipartFile, filePath);
    }

    private void checkModifiable(String email, Long boardId){
        String date=getAvailableDate();
        boardContentService.checkExchangeDate(date, boardId); //최근 교환 중인 날짜가 3일이 넘었는 지
        boardContentService.checkExchangeCompletionDate(date, email, boardId); //최근 교환 완료한 날짜가 3일이 넘었는 지
    }
}
