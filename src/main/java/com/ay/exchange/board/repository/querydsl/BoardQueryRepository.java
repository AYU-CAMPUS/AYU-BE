package com.ay.exchange.board.repository.querydsl;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.response.FilePathInfo;
import com.ay.exchange.board.dto.response.MyDataResponse;
import com.ay.exchange.board.entity.vo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardQueryRepository {
    Page<BoardInfoDto> findBoards(Integer apporval, Category category, Pageable pageable, List<String> departments, List<String> grades, List<String> types);
    void deleteBoard(String email, Long boardID);
    String findFilePathByBoardId(Long boardId);
    Long updateApproval(String email, Long boardId);
    boolean existsBoard(String email, Long boardId);

    FilePathInfo getFilePath(Long requesterBoardId, String email);

    MyDataResponse getMyData(PageRequest pageRequest, String email);

    String findBoardOwnerEmail(Long boardId, String email);

    boolean existsRequesterBoard(Long requesterBoardId, String email);

}
