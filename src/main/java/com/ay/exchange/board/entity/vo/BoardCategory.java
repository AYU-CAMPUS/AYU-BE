package com.ay.exchange.board.entity.vo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Embeddable
@Access(AccessType.FIELD)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCategory {

    @Builder
    public BoardCategory(Category category, DepartmentType departmentType, FileType fileType, String gradeType, String subjectName, String professorName) {
        this.category = category;
        this.departmentType = departmentType;
        this.fileType = fileType;
        this.gradeType = gradeType;
        this.subjectName = subjectName;
        this.professorName = professorName;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private DepartmentType departmentType;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private String gradeType;

    private String subjectName;
    private String professorName;
}
