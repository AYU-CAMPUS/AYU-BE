package com.ay.exchange.board.repository.querydsl;


import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.vo.BoardCategory;
import org.springframework.data.domain.Pageable;

public interface BoardContentQueryRepository {
    BoardContentResponse findBoardContent(Long boardId, Pageable page, String email);
    ModifiableBoardResponse findModifiableBoard(String email, Long boardId);

    Boolean canDeleted(String email, Long boardId);

    void requestModificationBoard(ModificationRequest modificationRequest, String email, String originalFilename, String filePath, BoardCategory boardCategory);

    boolean updateApproval(String email, Long boardId);

    Boolean checkExchangeCompletionDate(String date, String email, Long boardId);

    Boolean checkExchangeDate(String date, Long boardId);
}