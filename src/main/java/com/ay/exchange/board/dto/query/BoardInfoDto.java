package com.ay.exchange.board.dto.query;

import com.ay.exchange.board.entity.vo.GradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardInfoDto {
    @Schema(description = "게시물 고유 번호")
    private Long id;

    @Schema(description = "자료명")
    private String title;

    @Schema(description = "작성자")
    private String writer;

    @Schema(description = "과목명")
    private String subjectName;

    @Schema(description = "학년")
    private GradeType gradeType;

    @Schema(description = "교환수")
    private Integer numberOfSuccessfulExchanges;

    @Schema(description = "등록일")
    private String createdDate;
}
