package com.ay.exchange.management.service;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.response.BoardInfo;
import com.ay.exchange.management.dto.response.RequestBoardResponse;
import com.ay.exchange.management.exception.FailAcceptRequestBoard;
import com.ay.exchange.management.repository.ManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final ManagementRepository managementRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final int PAGE_LIMIT_LENGTH = 2;

    public RequestBoardResponse findRequestBoard(Integer page) {
        Long totalPages = managementRepository.findRequestBoardTotal();

        List<BoardInfo> boardInfos = managementRepository.findRequestBoards(getPageRequest(page));

        return new RequestBoardResponse(totalPages, boardInfos);
    }

    private PageRequest getPageRequest(Integer page) {
        return PageRequest.of(page > 0 ? (page - 1) : 0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptRequestBoard(BoardIdRequest boardIdRequest) {
        if (!managementRepository.updateBoardApproval(boardIdRequest)) {
            throw new FailAcceptRequestBoard();
        }

        //추후 알림까지 추가
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectRequestBoard(BoardIdRequest boardIdRequest) {
        try{
            if (managementRepository.deleteBoard(boardIdRequest)) {
                //추후 알림까지 추가
                return;
            }
        } catch (Exception e){
            throw new FailAcceptRequestBoard();
        }
        throw new FailAcceptRequestBoard();
    }
}