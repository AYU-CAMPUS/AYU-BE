package com.ay.exchange.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteRequest{
    @Schema(description = "게시글 고유 식별 번호")
    private Long boardContentId;
}
