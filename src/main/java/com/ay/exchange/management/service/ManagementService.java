package com.ay.exchange.management.service;

import com.ay.exchange.management.dto.query.UserInfo;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.request.SuspensionRequest;
import com.ay.exchange.management.dto.response.BoardInfo;
import com.ay.exchange.management.exception.FailAcceptRequestBoard;
import com.ay.exchange.management.exception.FailUpdatedSuspension;
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
    private final int PAGE_LIMIT_LENGTH = 2;

    public List<BoardInfo> findRequestBoard(Integer page) {
        List<BoardInfo> boardInfos = managementRepository.findRequestBoards(getPageRequest(page));
        return boardInfos;
    }

    public void acceptRequestBoard(BoardIdRequest boardIdRequest) {
        long successUpdatedApprovalCount = managementRepository.updateBoardApproval(boardIdRequest);
        if (successUpdatedApprovalCount != 1L) {
            throw new FailAcceptRequestBoard();
        }
    }

    public void rejectRequestBoard(BoardIdRequest boardIdRequest) {
        long successDeletedBoardCount = managementRepository.deleteBoard(boardIdRequest);
        if(successDeletedBoardCount != 1L){
            throw new FailAcceptRequestBoard();
        }
    }

    public void updateSuspension(SuspensionRequest suspensionRequest) {
        long successUpdatedUserSuspendedCount = managementRepository.updateUserSuspensionByEmail(suspensionRequest);
        if (successUpdatedUserSuspendedCount != 1L) {
            throw new FailUpdatedSuspension();
        }
    }

    public List<UserInfo> getUserInfos(Integer page) {
        List<UserInfo> userInfos = managementRepository.findUserInfos(getPageRequest(page));
        return userInfos;
    }

    private PageRequest getPageRequest(Integer page) {
        return PageRequest.of(page > 0 ? (page - 1) : 0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));
    }

    public Long findRequestBoardTotal() {
        return managementRepository.findRequestBoardTotal();
    }

    public Long findUserTotal() {
        return managementRepository.findUserTotal();
    }
}