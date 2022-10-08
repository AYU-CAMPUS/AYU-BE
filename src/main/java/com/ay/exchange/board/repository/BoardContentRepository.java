package com.ay.exchange.board.repository;

import com.ay.exchange.board.entity.BoardContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardContentRepository extends JpaRepository<BoardContent,Long> {
    void deleteByBoardId(Long boardId);
}
