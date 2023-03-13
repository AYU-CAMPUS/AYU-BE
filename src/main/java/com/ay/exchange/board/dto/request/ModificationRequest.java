package com.ay.exchange.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModificationRequest {
    @Schema(description = "게시글 번호")
    @NotNull
    private Long boardId;

    @Schema(description = "글 제목")
    @NotNull
    private String title;

    @Schema(description = "신학대학(0), 인문대학(1), 예술체육대학(2), 사회과학대학(3), 창의융합대학(4), 인성양성(5), 리더십(6), 융합실무(7), 문제해결(8), 글로벌(9), 의사소통(10)," +
            " 레포트(11), PPT템플릿(12), 한국사자격증(13), 토익(14), 토플(15), 논문(16), 이력서(17), 컴활자격증(18)")
    @Pattern(regexp = "^(0|[1-9]|1[0-8])$")
    private String category;

    @Schema(description = "신학과(0), 기독교교육과(1), 국어국문학과(2), 영미언어문화학과(3), " +
            "러시아언어문화학과(4), 중국언어문화학과(5), 유아교육과(6), 공연예술학과(7), 음악학과(8), " +
            "디지털미디어디자인학과(9), 화장품발명디자인학과(10), 뷰티메디컬디자인학과(11), " +
            "글로벌경영학과(12), 행정학과(13), 관광경영학과(14), 식품영양학과(15), " +
            "컴퓨터공학과(16), 정보전기전자공학과(17), 통계데이터사이언스학과(18), 소프트웨어학과(19), " +
            "도시정보공학과(20), 환경에너지공학과(21), AI융합학과(22)")
    @Pattern(regexp = "^([0-9]|1[0-9]|2[0-2])$")
    private String departmentType;

    @Schema(description = "중간고사(0), 기말고사(1), 요약(2)")
    @Pattern(regexp = "^[0-3]$")
    private String fileType;

    @Schema(description = "Freshman(1), Sophomore(2), Junior(3), Senior(4)")
    @Pattern(regexp = "^[1-4]$")
    private String gradeType;

    @Schema(description = "전공 또는 교양 선택 시 과목명 입력 (30자 제한)")
    @Size(max = 30)
    private String subjectName;

    @Schema(description = "전공 또는 교양 선택 시 교수명 입력 (15자 제한)")
    @Size(max = 15)
    private String professorName;

    @Schema(description = "파일 페이지 수 => 1~1000으로 제한")
    @Pattern(regexp = "^([1-9]|[1-9][0-9]|[1-9][0-9][0-9])$")
    private String numberOfFilePages;

    @Schema(description = "글 내용 (200자 제한)")
    @NotNull
    @Size(max = 200)
    private String content;
}