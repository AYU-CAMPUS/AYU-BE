package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.repository.querydsl.ModificationBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ay.exchange.common.util.BoardTypeGenerator.*;

@Service
@RequiredArgsConstructor
public class ModificationBoardService {
    private final ModificationBoardRepository modificationBoardRepository;

    public void save(ModificationRequest modificationRequest, String email, String originalFilename, String filePath) {
        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(Integer.parseInt(modificationRequest.getCategory())))
                .departmentType(getDepartmentType(Integer.parseInt(modificationRequest.getDepartmentType())))
                .fileType(getFileType(Integer.parseInt(modificationRequest.getFileType())))
                .gradeType(modificationRequest.getGradeType())
                .subjectName(modificationRequest.getSubjectName())
                .professorName(modificationRequest.getProfessorName())
                .build();
        Long cnt = modificationBoardRepository.save(modificationRequest, email, originalFilename, filePath, boardCategory);
        if (cnt != 1L) {
            throw new FailModifyBoardException();
        }
    }
}
