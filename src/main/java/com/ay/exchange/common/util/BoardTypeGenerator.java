package com.ay.exchange.common.util;

import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.entity.vo.GradeType;

public class BoardTypeGenerator {
    public static GradeType getGradeType(Integer gradeType) {
        if (gradeType == null) return null;
        switch (gradeType) {
            case 0:
                return GradeType.Freshman;
            case 1:
                return GradeType.Sophomore;
            case 2:
                return GradeType.Junior;
            case 3:
                return GradeType.Senior;
            default:
                return null;
        }
    }

    public static FileType getFileType(Integer fileType) {
        if (fileType == null) return null;
        switch (fileType) {
            case 0:
                return FileType.중간고사;
            case 1:
                return FileType.기말고사;
            case 2:
                return FileType.필기요약;
            default:
                return null;
        }
    }

    public static DepartmentType getDepartmentType(Integer departmentType) {
        if (departmentType == null) return null;
        switch (departmentType) {
            case 0:
                return DepartmentType.신학과;
            case 1:
                return DepartmentType.기독교교육과;
            case 2:
                return DepartmentType.국어국문학과;
            case 3:
                return DepartmentType.영미언어문화학과;
            case 4:
                return DepartmentType.러시아언어문화학과;
            case 5:
                return DepartmentType.중국언어문화학과;
            case 6:
                return DepartmentType.유아교육과;
            case 7:
                return DepartmentType.공연예술학과;
            case 8:
                return DepartmentType.음악학과;
            case 9:
                return DepartmentType.디지털미디어디자인학과;
            case 10:
                return DepartmentType.화장품발명디자인학과;
            case 11:
                return DepartmentType.뷰티메디컬디자인학과;
            case 12:
                return DepartmentType.글로벌경영학과;
            case 13:
                return DepartmentType.행정학과;
            case 14:
                return DepartmentType.관광경영학과;
            case 15:
                return DepartmentType.식품영양학과;
            case 16:
                return DepartmentType.컴퓨터공학과;
            case 17:
                return DepartmentType.정보전기전자공학과;
            case 18:
                return DepartmentType.통계데이터사이언스학과;
            case 19:
                return DepartmentType.소프트웨어학과;
            case 20:
                return DepartmentType.도시정보공학과;
            case 21:
                return DepartmentType.환경에너지공학과;
            case 22:
                return DepartmentType.AI융합학과;
            default:
                return null;
        }
    }

    public static Category getCategory(Integer category) {
        switch (category) {
            case 0:
                return Category.신학대학;
            case 1:
                return Category.인문대학;
            case 2:
                return Category.예술체육대학;
            case 3:
                return Category.사회과학대학;
            case 4:
                return Category.창의융합대학;
            case 5:
                return Category.인성양성;
            case 6:
                return Category.리더십;
            case 7:
                return Category.융합실무;
            case 8:
                return Category.문제해결;
            case 9:
                return Category.글로벌;
            case 10:
                return Category.의사소통;
            case 11:
                return Category.레포트;
            case 12:
                return Category.PPT템플릿;
            case 13:
                return Category.한국사자격증;
            case 14:
                return Category.토익;
            case 15:
                return Category.토플;
            case 16:
                return Category.논문;
            case 17:
                return Category.이력서;
            case 18:
                return Category.컴활자격증;
            default:
                return null;
        }
    }
}
