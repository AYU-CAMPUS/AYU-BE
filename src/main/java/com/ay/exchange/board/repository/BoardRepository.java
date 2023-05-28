package com.ay.exchange.board.repository;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.repository.querydsl.BoardQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board,Long>, BoardQueryRepository {

    Long countByCreatedDateAndEmail(String createdDate, String email);
}
