package com.ay.exchange.board.service;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.board.dto.request.ModificationRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.ay.exchange.common.util.BoardTypeGenerator.*;
import static com.ay.exchange.common.util.BoardTypeGenerator.getGradeType;

@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AwsS3Service awsS3Service;
    private final int UPLOAD_FILE = 0;
    private final int PAGE_LIMIT_LENGTH = 2;

    public BoardContentResponse getBoardContent(Long boardId, String token) {
        PageRequest pageRequest = PageRequest.of(0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));

        return boardContentRepository.findBoardContent(boardId, pageRequest, jwtTokenProvider.getUserEmail(token));
    }

    public ModifiableBoardResponse findModifiableBoard(String token, Long boardId) {
        return boardContentRepository.findModifiableBoard(jwtTokenProvider.getUserEmail(token), boardId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void requestModificationBoard(ModificationRequest modificationRequest, MultipartFile multipartFile, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(modificationRequest.getCategoryDto().getCategory()))
                .departmentType(getDepartmentType(modificationRequest.getCategoryDto().getDepartmentType()))
                .fileType(getFileType(modificationRequest.getCategoryDto().getFileType()))
                .gradeType(getGradeType(modificationRequest.getCategoryDto().getGradeType()))
                .subjectName(modificationRequest.getCategoryDto().getSubjectName())
                .professorName(modificationRequest.getCategoryDto().getProfessorName())
                .build();

        try {
            String filePath = awsS3Service.buildFileName(multipartFile.getOriginalFilename(), email, UPLOAD_FILE);
            boardContentRepository.requestModificationBoard(modificationRequest, email, multipartFile.getOriginalFilename(), filePath, boardCategory);
            awsS3Service.uploadFile(multipartFile, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FailModifyBoardException();
        }

    }
}