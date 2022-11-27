package com.ay.exchange.comment.repository.querydsl;

import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ay.exchange.comment.entity.QComment.comment;
import static com.ay.exchange.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class CommentQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CommentInfoDto> getComments(Pageable pageable, Long boardId) {
        return queryFactory.select(Projections.fields(
                        CommentInfoDto.class,
                        comment.id.as("commentId"),
                        user.nickName.as("writer"),
                        comment.content,
                        comment.depth,
                        comment.groupId,
                        comment.createdDate,
                        user.profileImage.coalesce("default.svg").as("profileImage"))
                )
                .from(comment)
                .innerJoin(user)
                .on(comment.userId.eq(user.userId))
                .where(comment.board.id.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
