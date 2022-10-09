package com.ay.exchange.board.repository;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.repository.querydsl.BoardQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board,Long>, BoardQueryRepository {

}
