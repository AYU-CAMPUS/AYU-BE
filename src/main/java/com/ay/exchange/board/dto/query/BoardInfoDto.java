package com.ay.exchange.board.dto.query;

import com.ay.exchange.board.entity.vo.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardInfoDto {
    private Long id;
    private String title;
    private String writer;
    private Integer views;
    private BoardCategory boardCategory;  //게시글 조회기 때문에 필요 없을 듯 나중에 지울 예정
    private Integer numberOfFilePages;
    private Integer numberOfSuccessfulExchanges;
    private String createDate;
}
