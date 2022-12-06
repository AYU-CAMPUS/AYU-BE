package com.ay.exchange.comment.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeleteRequest {
    @Schema(description = "댓글 고유 식별 번호")
    private Long commentId;
}