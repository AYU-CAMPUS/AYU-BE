package com.ay.exchange.board.repository.querydsl;

import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.common.util.DateUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ModificationBoardRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public long save(ModificationRequest modificationRequest, String email, String originalFilename, String filePath, BoardCategory boardCategory) {
        String sql = "INSERT INTO modification_board(title, category, department_type, file_type, grade_type, professor_name, subject_name, number_of_file_pages, original_file_name, file_path, board_id, content, date) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        return em.createNativeQuery(sql)
                .setParameter(1, modificationRequest.getTitle())
                .setParameter(2, String.valueOf(boardCategory.getCategory()))
                .setParameter(3, String.valueOf(boardCategory.getDepartmentType()))
                .setParameter(4, String.valueOf(boardCategory.getFileType()))
                .setParameter(5, boardCategory.getGradeType())
                .setParameter(6, boardCategory.getProfessorName())
                .setParameter(7, boardCategory.getSubjectName())
                .setParameter(8, modificationRequest.getNumberOfFilePages())
                .setParameter(9, originalFilename)
                .setParameter(10, filePath)
                .setParameter(11, modificationRequest.getBoardId())
                .setParameter(12, modificationRequest.getContent())
                .setParameter(13, DateUtil.getCurrentDate())
                .executeUpdate();
    }
}
