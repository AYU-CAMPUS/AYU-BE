package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.request.WriteRequest;
import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.entity.vo.*;
import com.ay.exchange.board.exception.FailDeleteBoardException;
import com.ay.exchange.board.exception.FailWriteBoardException;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ay.exchange.common.util.BoardTypeGenerator.*;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardContentRepository boardContentRepository;
    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final String REGEX = "[0-9]+";
    private final int PAGE_LIMIT_LENGTH = 2;

    @Transactional(rollbackFor = Exception.class)
    public void writeBoard(WriteRequest writeRequest, MultipartFile multipartFile, String token) {
        String userId = jwtTokenProvider.getUserId(token);

        BoardCategory boardCategory = BoardCategory.builder()
                .category(getCategory(writeRequest.getCategoryDto().getCategory()))
                .departmentType(getDepartmentType(writeRequest.getCategoryDto().getDepartmentType()))
                .fileType(getFileType(writeRequest.getCategoryDto().getFileType()))
                .gradeType(getGradeType(writeRequest.getCategoryDto().getGradeType()))
                .subjectName(writeRequest.getCategoryDto().getSubjectName())
                .professorName(writeRequest.getCategoryDto().getProfessorName())
                .build();

        try {
            String filePath = awsS3Service.buildFileName(multipartFile.getOriginalFilename(), userId, 0);

            Board board = Board.builder()
                    .title(writeRequest.getTitle())
                    .numberOfFilePages(writeRequest.getNumberOfFilePages())
                    .exchangeSuccessCount(0)
                    .approval(false)
                    .views(1)
                    .boardCategory(boardCategory)
                    .originalFileName(multipartFile.getOriginalFilename())
                    .userId(userId)
                    .filePath(filePath)
                    .build();
            boardRepository.save(board);

            BoardContent boardContent = BoardContent.builder()
                    .content(writeRequest.getContent())
                    .board(board)
                    .build();
            boardContentRepository.save(boardContent);

            awsS3Service.uploadFile(multipartFile, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FailWriteBoardException();
        }
    }

    public BoardResponse getBoardList(Integer page, Integer category,
                                      String department, String grade, String type
    ) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<BoardInfoDto> pages = boardRepository.findBoards(
                true, //추후 approval true로 변경해야함
                getCategory(category),
                pageRequest,
                getSeparateDepartmentConditions(department),
                getSeparateGradeConditions(grade),
                getSeparateTypeConditions(type));

//        System.out.println(pages.getTotalPages());
//        System.out.println(pages.getTotalElements());
//        System.out.println(pages.getNumber());

        return new BoardResponse(pages.getTotalPages(), pages.getContent());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBoard(String token, DeleteRequest deleteRequest) {
        String userId = jwtTokenProvider.getUserId(token);
        if (boardContentRepository.canDeleted(userId, deleteRequest.getBoardId())) {
            String filePath = boardRepository.findFilePathByBoardId(deleteRequest.getBoardId());
            boardRepository.deleteBoard(userId, deleteRequest.getBoardId());
            awsS3Service.deleteUserFile(filePath);
            return;
        }
        throw new FailDeleteBoardException();
    }

    private List<String> getSeparateTypeConditions(String type) {
        return Arrays.stream(type.split(","))
                .filter(t -> t.matches(REGEX))
                .map(t -> Integer.parseInt(t))
                .filter(t -> (t >= 0 && t <= 3))
                .map(t -> getFileType(t).name())
                .collect(Collectors.toList());
    }

    private List<String> getSeparateGradeConditions(String grade) {
        return Arrays.stream(grade.split(","))
                .filter(g -> g.matches(REGEX))
                .map(g -> Integer.parseInt(g))
                .filter(g -> (g >= 0 && g <= 3))
                .map(g -> getGradeType(g).name())
                .collect(Collectors.toList());
    }

    private List<String> getSeparateDepartmentConditions(String department) {
        return Arrays.stream(department.split(","))
                .filter(d -> d.matches(REGEX))
                .map(d -> Integer.parseInt(d))
                .filter(d -> (d >= 0 && d <= 22)) //[하드코딩 리팩토링] 구현이 바뀔수도 있어서 나중에 할 예정
                .map(d -> getDepartmentType(d).name())
                .collect(Collectors.toList());
    }

}
