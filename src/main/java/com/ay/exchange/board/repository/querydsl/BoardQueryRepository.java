package com.ay.exchange.board.repository.querydsl;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.entity.vo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardQueryRepository {
    Page<BoardInfoDto> findBoards(Integer apporval, Category category, Pageable pageable, List<String> departments, List<String> grades, List<String> types);
    void deleteBoard(String userId, Long boardID);

    String findFilePathByBoardId(Long boardId);
}
