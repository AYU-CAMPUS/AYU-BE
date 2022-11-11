package com.ay.exchange.board.dto.query;

import com.ay.exchange.board.entity.vo.BoardCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardContentInfo2Dto {
    private String content;
    private String title;
    private String writer;
    private BoardCategory boardCategory;
    private Integer views;
    private Integer numberOfFilePages;
    private Integer numberOfSuccessfulExchanges;
    private String createdDate;
    private Integer exchangeType;
    private String userId;
}
