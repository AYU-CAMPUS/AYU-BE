package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.request.WriteRequest;

import com.ay.exchange.board.dto.response.FilePathInfo;
import com.ay.exchange.board.dto.response.MyDataResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.*;
import com.ay.exchange.board.exception.FailDeleteBoardException;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.common.util.Approval;
import com.ay.exchange.common.util.PagingGenerator;
import com.ay.exchange.user.exception.NotExistsFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ay.exchange.common.util.BoardTypeGenerator.*;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final String REGEX = "[0-9]+";

    public Page<BoardInfoDto> getBoardList(Integer page, Integer category,
                                      String department, String grade, String type
    ) {
        Page<BoardInfoDto> pages = boardRepository.findBoards(
                Approval.AGREE.getApproval(),
                getCategory(category),
                PagingGenerator.getPageRequest(page),
                getSeparateDepartmentConditions(department),
                getSeparateGradeConditions(grade),
                getSeparateTypeConditions(type));

        return pages;
    }

    private List<String> getSeparateTypeConditions(String type) {
        return Arrays.stream(type.split(","))
                .filter(t -> t.matches(REGEX))
                .map(Integer::parseInt)
                .filter(t -> (t >= 0 && t <= 3))
                .map(t -> getFileType(t).name())
                .collect(Collectors.toList());
    }

    private List<String> getSeparateGradeConditions(String grade) {
        return Arrays.stream(grade.split(","))
                .filter(g -> g.matches(REGEX))
                .map(Integer::parseInt)
                .filter(g -> (g >= 1 && g <= 4))
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    private List<String> getSeparateDepartmentConditions(String department) {
        return Arrays.stream(department.split(","))
                .filter(d -> d.matches(REGEX))
                .map(Integer::parseInt)
                .filter(d -> (d >= 0 && d <= 22)) //[하드코딩 리팩토링] 구현이 바뀔수도 있어서 나중에 할 예정
                .map(d -> getDepartmentType(d).name())
                .collect(Collectors.toList());
    }

    public Board save(WriteRequest writeRequest, String email, String filePath, String originalFileName) {
        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(Integer.parseInt(writeRequest.getCategory())))
                .departmentType(getDepartmentType(Integer.parseInt(writeRequest.getDepartmentType())))
                .fileType(getFileType(Integer.parseInt(writeRequest.getFileType())))
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
        if(cnt != 1L){
            throw new FailModifyBoardException();
        }
    }

    public void isBoardOwner(String email, Long boardId) {
        boolean isOwner = boardRepository.existsBoard(email, boardId);
        if(isOwner){
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
}
