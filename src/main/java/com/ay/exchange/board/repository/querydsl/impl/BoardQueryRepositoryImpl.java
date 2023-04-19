package com.ay.exchange.board.repository.querydsl.impl;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.response.FilePathInfo;
import com.ay.exchange.board.dto.response.MyDataInfo;
import com.ay.exchange.board.dto.response.MyDataResponse;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.exception.FailDeleteBoardException;
import com.ay.exchange.board.repository.querydsl.BoardQueryRepository;
import com.ay.exchange.common.util.Approval;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;

@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardInfoDto> findBoards(Integer approval, Category category, Pageable pageable, List<String> departments, List<String> grades, List<String> types) {
        List<BoardInfoDto> pages = queryFactory
                .select(Projections.fields(BoardInfoDto.class,
                        board.id,
                        board.title,
                        user.nickName.as("writer"),
                        board.boardCategory.subjectName,
                        board.boardCategory.gradeType,
                        board.exchangeSuccessCount.as("numberOfSuccessfulExchanges"),
                        board.createdDate
                ))
                .from(board)
                .innerJoin(user)
                .on(board.email.eq(user.email))
                .where(departmentEq(departments),
                        gradeEq(grades),
                        typeEq(types),
                        board.approval.eq(approval),
                        board.boardCategory.category.eq(category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.id.desc())
                .fetch();

        Long count = queryFactory
                .select(board.count())
                .from(board)
                .where(departmentEq(departments),
                        gradeEq(grades),
                        typeEq(types),
                        board.approval.eq(approval),
                        board.boardCategory.category.eq(category)
                )
                .fetchOne();

        return new PageImpl<>(pages, pageable, count);
    }

    @Override
    public void deleteBoard(String email, Long boardId) {
        if (queryFactory.delete(board)
                .where(board.email.eq(email)
                        .and(board.id.eq(boardId))
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .execute() != 1L) {
            throw new FailDeleteBoardException();
        }
    }

    @Override
    public String findFilePathByBoardId(Long boardId) {
        return queryFactory.select(board.filePath)
                .from(board)
                .where(board.id.eq(boardId))
                .fetchOne();
    }

    @Override
    public Long updateApproval(String email, Long boardId) { //게시글 관리자에게 수정을 허가 받기 위해 approval을 false로 변경
        return queryFactory.update(board)
                .set(board.approval, Approval.MODIFICATION.getApproval())
                .where(board.id.eq(boardId)
                        .and(board.email.eq(email))
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .execute();
    }

    @Override
    public boolean existsBoard(String email, Long boardId) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.email.eq(email)
                        .and(board.id.eq(boardId))
                        .and(board.approval.eq(Approval.AGREE.getApproval()))
                )
                .limit(1L)
                .fetchOne();
        return count == 1L;
    }

    @Override
    public MyDataResponse getMyData(PageRequest pageRequest, String email) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.email.eq(email)
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .fetchOne();

        List<MyDataInfo> myDataInfos = queryFactory
                .select(Projections.fields(
                        MyDataInfo.class,
                        board.createdDate,
                        board.title,
                        board.id.as("boardId"),
                        board.boardCategory.category
                ))
                .from(board)
                .where(board.email.eq(email)
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(board.id.desc())
                .fetch();

        return new MyDataResponse(count, myDataInfos);
    }

    @Override
    public FilePathInfo getFilePath(Long requesterBoardId, String email) {
        FilePathInfo filePathInfo = queryFactory.select(Projections.fields(
                        FilePathInfo.class,
                        board.email,
                        board.filePath
                ))
                .from(exchangeCompletion)
                .innerJoin(board)
                .on(board.id.eq(exchangeCompletion.requesterBoardId))
                .where(exchangeCompletion.requesterBoardId.eq(requesterBoardId)
                        .and(exchangeCompletion.email.eq(email)))
                .fetchOne();
        return filePathInfo;
    }

    @Override
    public String findBoardOwnerEmail(Long boardId, String email) {
        String ownerEmail = queryFactory.select(board.email)
                .from(board)
                .where(board.id.eq(boardId)
                        .and(board.approval.eq(Approval.AGREE.getApproval()))
                        .and(board.email.ne(email)))
                .fetchOne();
        return ownerEmail;
    }

    @Override
    public boolean existsRequesterBoard(Long requesterBoardId, String email) {
        return queryFactory.selectOne()
                .from(board)
                .where(board.id.eq(requesterBoardId)
                        .and(board.email.eq(email))
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .fetchFirst() != null;
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
            booleanBuilder.or(board.boardCategory.gradeType.eq(grade));
        }
        return booleanBuilder;
    }

    private BooleanBuilder departmentEq(List<String> departments) {
        if (departments.size() == 0) return null;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String department : departments) {
            booleanBuilder.or(board.boardCategory
                    .departmentType.eq(DepartmentType.valueOf(department)));
        }
        return booleanBuilder;
    }
}
