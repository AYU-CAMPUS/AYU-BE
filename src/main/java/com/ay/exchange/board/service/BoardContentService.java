package com.ay.exchange.board.service;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.exception.FailDeleteBoardException;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.exception.FileInvalidException;
import com.ay.exchange.board.exception.NotFoundBoardException;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.common.util.FileValidator;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.ay.exchange.common.util.BoardTypeGenerator.*;
import static com.ay.exchange.common.util.DateUtil.getAvailableDate;

@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AwsS3Service awsS3Service;
    private final int UPLOAD_FILE = 0;
    private final int PAGE_LIMIT_LENGTH = 2;

    public BoardContentResponse getBoardContent(Long boardId, String email) {
        PageRequest pageRequest = PageRequest.of(0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));
        try {
            return boardContentRepository.findBoardContent(boardId, pageRequest, email);
        } catch (Exception e) {
            throw new NotFoundBoardException();
        }
    }

    public ModifiableBoardResponse findModifiableBoard(String email, Long boardId) {
        String date = getAvailableDate();

        ModifiableBoardResponse modifiableBoardResponse = boardContentRepository.findModifiableBoard(date, email, boardId);

        boolean isExchangeDatePassed3Days = boardContentRepository.checkExchangeDate(date, boardId);
        boolean isExchangeCompletionDatePassed3Days = boardContentRepository.checkExchangeCompletionDate(date, email, boardId);

        if (modifiableBoardResponse != null && isExchangeDatePassed3Days && isExchangeCompletionDatePassed3Days) {
            return modifiableBoardResponse;
        }

        throw new FailModifyBoardException();
    }

    @Transactional(rollbackFor = Exception.class)
    public void requestModificationBoard(ModificationRequest modificationRequest, MultipartFile multipartFile, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(Integer.parseInt(modificationRequest.getCategory())))
                .departmentType(getDepartmentType(Integer.parseInt(modificationRequest.getDepartmentType())))
                .fileType(getFileType(Integer.parseInt(modificationRequest.getFileType())))
                .gradeType(modificationRequest.getGradeType())
                .subjectName(modificationRequest.getSubjectName())
                .professorName(modificationRequest.getProfessorName())
                .build();

        if (multipartFile == null) { //기존 파일을 유지하고 내용만 변경
            boardContentRepository.requestModificationBoard(modificationRequest, email, null, null, boardCategory);
            return;
        }

        if (FileValidator.isAllowedFileType(multipartFile)) { //파일도 변경
            try {
                String filePath = awsS3Service.buildFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()), email, UPLOAD_FILE);
                boardContentRepository.requestModificationBoard(modificationRequest, email, multipartFile.getOriginalFilename(), filePath, boardCategory);
                awsS3Service.uploadFile(multipartFile, filePath);
            } catch (Exception e) {
                throw new FailModifyBoardException();
            }
        } else {
            throw new FileInvalidException();
        }
    }

//    private boolean canModification(String date, String email, Long boardId) {
//        if (!boardContentRepository.updateApproval(email, boardId)
//                && !boardContentRepository.checkExchangeCompletionDate(date, email, boardId)
//                && !boardContentRepository.checkExchangeDate(date, boardId)) {
//            return false;
//        }
//        return true;
//    }

    public void save(Board board, String content) {
        BoardContent boardContent = BoardContent.builder()
                .content(content)
                .board(board)
                .build();
        boardContentRepository.save(boardContent);
    }

    public void checkDeleteable(String email, Long boardId) {
        if (boardContentRepository.canDeleted(email, boardId)) {
            return;
        }
        throw new FailDeleteBoardException();
    }

    public void checkExchangeDate(String date, Long boardId) {
        boolean isExchangeDatePassed3Days = boardContentRepository.checkExchangeDate(date, boardId);
        if (isExchangeDatePassed3Days) {
            return;
        }
        throw new FailModifyBoardException();
    }

    public void checkExchangeCompletionDate(String date, String email, Long boardId) {
        boolean isExchangeCompletionDatePassed3Days = boardContentRepository.checkExchangeCompletionDate(date, email, boardId);

    }
}