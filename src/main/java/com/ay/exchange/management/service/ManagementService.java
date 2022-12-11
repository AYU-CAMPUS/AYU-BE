package com.ay.exchange.management.service;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.management.dto.response.BoardInfo;
import com.ay.exchange.management.dto.response.RequestBoardResponse;
import com.ay.exchange.management.repository.ManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}