package com.ay.exchange.management.facade;

import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.response.BoardInfo;
import com.ay.exchange.management.dto.response.RequestBoardResponse;
import com.ay.exchange.management.service.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementFacade {
    private final ManagementService managementService;

    public RequestBoardResponse findRequestBoard(Integer page) {
        Long totalPages = managementService.findRequestBoardTotal();

        List<BoardInfo> boardInfos = managementService.findRequestBoard(page);

        return new RequestBoardResponse(totalPages, boardInfos);
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptRequestBoard(BoardIdRequest boardIdRequest) {
        managementService.acceptRequestBoard(boardIdRequest);
        //추후 알림까지 추가
    }
}
