package com.ay.exchange.board.repository;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.repository.querydsl.BoardQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board,Long>, BoardQueryRepository {
    //승인된 페이지이면서, MediumCategory 기준으로 페이지 조회
    Page<BoardInfoDto> findByApprovalAndBoardCategoryMediumCategory(boolean apporval, Category category, PageRequest pageRequest);
}
