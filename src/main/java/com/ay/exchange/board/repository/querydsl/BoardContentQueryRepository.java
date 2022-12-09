package com.ay.exchange.board.repository.querydsl;


import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.vo.BoardCategory;
import org.springframework.data.domain.Pageable;

public interface BoardContentQueryRepository {
    BoardContentResponse findBoardContent(Long boardId, Pageable page, String userId);
    ModifiableBoardResponse findModifiableBoard(String userId, Long boardId);

    Boolean canDeleted(String userId, Long boardId);

    void requestModificationBoard(ModificationRequest modificationRequest, String userId, String originalFilename, String filePath, BoardCategory boardCategory);
}