package com.ay.exchange.board.repository;

import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.repository.querydsl.BoardContentQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardContentRepository extends JpaRepository<BoardContent,Long>, BoardContentQueryRepository {
    void deleteByBoardId(Long boardId);
}
