package com.ay.exchange.board.facade;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.dto.request.WriteRequest;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;

import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.exception.FailWriteBoardException;
import com.ay.exchange.board.service.BoardContentService;
import com.ay.exchange.board.service.BoardService;


import com.ay.exchange.board.service.ModificationBoardService;
import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.ay.exchange.comment.service.CommentService;
import com.ay.exchange.common.util.ExchangeType;
import com.ay.exchange.exchange.service.ExchangeCompletionService;
import com.ay.exchange.exchange.service.ExchangeService;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ay.exchange.common.util.DateUtil.getAvailableDate;
import static com.ay.exchange.common.util.ExchangeType.COMPLETION;
import static com.ay.exchange.common.util.ExchangeType.OWNER;


@Service
@RequiredArgsConstructor
public class BoardFacade {
    private final BoardService boardService;
    private final BoardContentService boardContentService;
    private final ExchangeService exchangeService;
    private final CommentService commentService;
    private final ExchangeCompletionService exchangeCompletionService;
    private final ModificationBoardService modificationBoardService;
    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final int UPLOAD_FILE = 0;
    private final String SEPARATOR = ";";

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

    public BoardResponse getBoardList(Integer page,
                                      Integer category,
                                      String department,
                                      String grade,
                                      String type) { //파라미터 값들을 dto로 묶는 것이 좀 더 깔끔해 보일 것 같다.
        Page<BoardInfoDto> pages = boardService.getBoardList(page, category, department, grade, type);

        return new BoardResponse(pages.getTotalPages(), pages.getContent());
    }

    @Transactional(readOnly = true)
    public BoardContentResponse getBoardContent(Long boardId, String token) {
        Long commentCount = commentService.getCommentCount(boardId);
        String email = token == null ? "" : jwtTokenProvider.getUserEmail(token); //토큰이 없어도 email을 ""로 줘서 쿼리를 할 때 게시글 조회는 가능하도록 함

        if (commentCount == 0) { //댓글이 없으면 댓글 테이블을 제외하고 join시킴
            BoardContentInfo2Dto boardContentInfo2Dto = boardContentService.findBoardContentWithNoComments(boardId, email);
            return new BoardContentResponse(commentCount,
                    new ArrayList<>(),
                    boardContentInfo2Dto.getContent(),
                    boardContentInfo2Dto.getTitle(),
                    boardContentInfo2Dto.getWriter(),
                    boardContentInfo2Dto.getBoardCategory(),
                    boardContentInfo2Dto.getNumberOfFilePages(),
                    boardContentInfo2Dto.getNumberOfSuccessfulExchanges(),
                    boardContentInfo2Dto.getCreatedDate(),
                    boardContentInfo2Dto.getEmail().equals(email) ? OWNER.getType() : (boardContentInfo2Dto.getExchangeType() >= COMPLETION.getType() ? COMPLETION.getType() : boardContentInfo2Dto.getExchangeType()), //-1이면 내가 쓴 글임,
                    splitDesiredData(boardContentInfo2Dto.getDesiredData()));
        }

        //댓글 존재
        List<BoardContentInfoDto> boardContentInfos = boardContentService.findBoardContentWithComments(boardId, email);

        List<CommentInfoDto> comments = createComments(boardContentInfos);

        BoardContent resultBoardContent = boardContentInfos.get(0).getBoardContent();
        Board resultBoard = boardContentInfos.get(0).getBoard();
        return new BoardContentResponse(commentCount,
                comments,
                resultBoardContent.getContent(),
                resultBoard.getTitle(),
                boardContentInfos.get(0).getNickName(),
                resultBoard.getBoardCategory(),
                resultBoard.getNumberOfFilePages(),
                resultBoard.getExchangeSuccessCount(),
                resultBoard.getCreatedDate(),
                resultBoard.getEmail().equals(email) ? OWNER.getType() : (boardContentInfos.get(0).getExchangeType() >= COMPLETION.getType() ? COMPLETION.getType() : boardContentInfos.get(0).getExchangeType()),
                splitDesiredData(boardContentInfos.get(0).getDesiredData()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBoard(String token, DeleteRequest deleteRequest) {
        String email = jwtTokenProvider.getUserEmail(token);

        exchangeCompletionService.checkDeleteable(email, deleteRequest.getBoardId()); //삭제 가능한 지
        boardService.isBoardOwner(email, deleteRequest.getBoardId()); //게시글 주인만 삭제 가능

        String filePath = boardService.findFilePathByBoardId(deleteRequest.getBoardId()); //s3에 저장된 파일들으 삭제하기 위해서 파일 경로 가져옴

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

    private void checkModifiable(String email, Long boardId) {
        String date = getAvailableDate();
        exchangeService.checkExchangeDate(date, boardId); //최근 교환 중인 날짜가 3일이 넘었는 지
        exchangeCompletionService.checkExchangeCompletionDate(date, email, boardId); //최근 교환 완료한 날짜가 3일이 넘었는 지
    }

    private List<String> splitDesiredData(String desiredData) {
        return Arrays.stream(desiredData.split(SEPARATOR))
                .collect(Collectors.toList());
    }

    private List<CommentInfoDto> createComments(List<BoardContentInfoDto> boardContentInfos) {
        return boardContentInfos.stream()
                .map(boardContentInfo -> new CommentInfoDto(
                        boardContentInfo.getCommentId(),
                        boardContentInfo.getWriter(),
                        boardContentInfo.getContent(),
                        boardContentInfo.getDepth(),
                        boardContentInfo.getGroupId(),
                        boardContentInfo.getCreatedDate(),
                        boardContentInfo.getProfileImage()
                ))
                .collect(Collectors.toList());
    }
}
