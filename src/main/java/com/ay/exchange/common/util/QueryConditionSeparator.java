package com.ay.exchange.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ay.exchange.common.util.BoardTypeGenerator.getDepartmentType;
import static com.ay.exchange.common.util.BoardTypeGenerator.getFileType;

public class QueryConditionSeparator {
    private static final String REGEX = "\\d+";
    private static final String DELIMITED = ",";
    private static final int TYPE_START=0;
    private static final int TYPE_END=3;
    private static final int GRADE_START=1;
    private static final int GRADE_END=4;
    private static final int DEPARTMENT_START=0;
    private static final int DEPARTMENT_END=22;

    public static List<String> separateTypeConditions(String type) {
        return Arrays.stream(type.split(DELIMITED))
                .filter(t -> t.matches(REGEX))
                .map(Integer::parseInt)
                .filter(t -> (t >= TYPE_START && t <= TYPE_END))
                .map(t -> getFileType(t).name())
                .collect(Collectors.toList());
    }

    public static List<String> separateGradeConditions(String grade) {
        return Arrays.stream(grade.split(DELIMITED))
                .filter(g -> g.matches(REGEX))
                .map(Integer::parseInt)
                .filter(g -> (g >= GRADE_START && g <= GRADE_END))
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    public static List<String> separateDepartmentConditions(String department) {
        return Arrays.stream(department.split(DELIMITED))
                .filter(d -> d.matches(REGEX))
                .map(Integer::parseInt)
                .filter(d -> (d >= DEPARTMENT_START && d <= DEPARTMENT_END))
                .map(d -> getDepartmentType(d).name())
                .collect(Collectors.toList());
    }
}
