package com.ay.exchange.board.repository.querydsl.impl;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.entity.vo.GradeType;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.repository.querydsl.BoardQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;

@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardInfoDto> findBoards(boolean apporval, Category category, Pageable pageable, List<String> departments, List<String> grades, List<String> types) {
        List<BoardInfoDto> pages = queryFactory
                .select(Projections.constructor(BoardInfoDto.class
                        , board.id
                        , board.title
                        , board.writer
                        , board.views
                        , board.boardCategory
                        , board.numberOfFilePages
                        , board.numberOfSuccessfulExchanges
                        , board.createdDate
                ))
                .from(board)
                .where(departmentEq(departments)
                        , gradeEq(grades)
                        , typeEq(types)
                        , board.approval.eq(apporval)
                        , board.boardCategory.mediumCategory.eq(Category.valueOf(category.name()))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count=queryFactory
                .select(board.count())
                .from(board)
                .where(departmentEq(departments)
                        , gradeEq(grades)
                        , typeEq(types)
                        , board.approval.eq(apporval)
                        , board.boardCategory.mediumCategory.eq(Category.valueOf(category.name()))
                )
                .fetchOne();

        return new PageImpl<>(pages,pageable,count);
    }

    private BooleanBuilder typeEq(List<String> types) {
        if (types.size() == 0) return null;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String type : types) {
            booleanBuilder.or(board.boardCategory.fileType.eq(FileType.valueOf(type)));
        }
        return booleanBuilder;
    }

    private BooleanBuilder gradeEq(List<String> grades) {
        if (grades.size() == 0) return null;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String grade : grades) {
            booleanBuilder.or(board.boardCategory.gradeType.eq(GradeType.valueOf(grade)));
        }
        return booleanBuilder;
    }

    private BooleanBuilder departmentEq(List<String> departments) {
        if (departments.size() == 0) return null;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String deparment : departments) {
            booleanBuilder.or(board.boardCategory
                    .smallCategory.eq(DepartmentType.valueOf(deparment)));
        }
        return booleanBuilder;
    }
}
