package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.request.WriteRequest;

import com.ay.exchange.board.dto.response.FilePathInfo;
import com.ay.exchange.board.dto.response.MyDataResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.*;
import com.ay.exchange.board.exception.ExceedCreationBoardException;
import com.ay.exchange.board.exception.FailDeleteBoardException;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.common.util.Approval;
import com.ay.exchange.common.util.PagingGenerator;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.user.exception.NotExistsFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static com.ay.exchange.common.util.BoardTypeGenerator.*;
import static com.ay.exchange.common.util.QueryConditionSeparator.*;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Page<BoardInfoDto> getBoardList(Integer page, Integer category,
                                           String department, String grade, String type
    ) {
        Page<BoardInfoDto> pages = boardRepository.findBoards(
                Approval.AGREE.getApproval(),
                getCategory(category),
                PagingGenerator.getPageRequest(page),
                separateDepartmentConditions(department),
                separateGradeConditions(grade),
                separateTypeConditions(type));

        return pages;
    }

    public Board save(WriteRequest writeRequest, String email, String filePath, String originalFileName) {
        //교양자료에서 departmentType은 존재하지 않는다.
        //카테고리별에서 departmentType과 fileType은 존재하지 않는데 Integer.parseInt(null)로 인하여 문제가 발생한다.

        Integer categoryIndex = Integer.parseInt(writeRequest.getCategory());

        Integer departmentIndex = writeRequest.getDepartmentType() == null
                ? null
                : Integer.parseInt(writeRequest.getDepartmentType());

        Integer fileTypeIndex = writeRequest.getFileType() == null
                ? null
                : Integer.parseInt(writeRequest.getFileType());

        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(categoryIndex))
                .departmentType(getDepartmentType(departmentIndex))
                .fileType(getFileType(fileTypeIndex))
                .gradeType(writeRequest.getGradeType())
                .subjectName(writeRequest.getSubjectName())
                .professorName(writeRequest.getProfessorName())
                .build();

        Board board = Board.builder()
                .title(writeRequest.getTitle())
                .numberOfFilePages(Integer.parseInt(writeRequest.getNumberOfFilePages()))
                .exchangeSuccessCount(0)
                .approval(Approval.WAITING.getApproval())
                .boardCategory(boardCategory)
                .originalFileName(originalFileName)
                .email(email)
                .filePath(filePath)
                .build();

        return boardRepository.save(board);
    }

    public String findFilePathByBoardId(Long boardId) {
        return boardRepository.findFilePathByBoardId(boardId);
    }

    public void delete(String email, Long boardId) {
        boardRepository.deleteBoard(email, boardId);
    }

    public void updateApproval(String email, Long boardId) {
        Long cnt = boardRepository.updateApproval(email, boardId);
        if (cnt != 1L) {
            throw new FailModifyBoardException();
        }
    }

    public void isBoardOwner(String email, Long boardId) {
        boolean isOwner = boardRepository.existsBoard(email, boardId);
        if (isOwner) {
            return;
        }
        throw new FailDeleteBoardException();
    }

    public FilePathInfo getFilePath(Long requesterBoardId, String email) {
        FilePathInfo filePathInfo = boardRepository.getFilePath(requesterBoardId, email);
        if (filePathInfo == null) {
            throw new NotExistsFileException();
        }
        return filePathInfo;
    }

    public MyDataResponse getMyData(Integer page, String email) {
        return boardRepository.getMyData(PagingGenerator.getPageRequest(page), email);
    }

    public String findBoardOwnerEmail(Long boardId, String email) {
        String boardOwnerEmail = boardRepository.findBoardOwnerEmail(boardId, email);
        if (boardOwnerEmail == null) {
            throw new UnableExchangeException();
        }
        return boardOwnerEmail;
    }

    public void checkCreationExceed(String currentDate, String email) {
        Long dailyCreateBoardCount = boardRepository.countByCreatedDateAndEmail(currentDate, email);
        if (dailyCreateBoardCount >= 7L) {
            throw new ExceedCreationBoardException();
        }
    }
}
