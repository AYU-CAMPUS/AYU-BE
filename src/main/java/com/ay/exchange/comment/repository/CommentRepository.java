package com.ay.exchange.comment.repository;

import com.ay.exchange.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Transactional(rollbackOn = Exception.class)
    void deleteAllByGroupId(Long groupId);
}