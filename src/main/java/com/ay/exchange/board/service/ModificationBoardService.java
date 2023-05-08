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
        Integer categoryIndex = Integer.parseInt(modificationRequest.getCategory());

        Integer departmentIndex = modificationRequest.getDepartmentType() == null
                ? null
                : Integer.parseInt(modificationRequest.getDepartmentType());

        Integer fileTypeIndex = modificationRequest.getFileType() == null
                ? null
                : Integer.parseInt(modificationRequest.getFileType());

        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(categoryIndex))
                .departmentType(getDepartmentType(departmentIndex))
                .fileType(getFileType(fileTypeIndex))
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
