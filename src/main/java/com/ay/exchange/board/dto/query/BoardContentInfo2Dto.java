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
    private Integer numberOfFilePages;
    private Integer numberOfSuccessfulExchanges;
    private String createdDate;
    private Long exchangeType;
    private String email;
    private String desiredData;
}
