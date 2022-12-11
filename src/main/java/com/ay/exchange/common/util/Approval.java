package com.ay.exchange.common.util;

public enum Approval {
    AGREE(1),
    WAITING(2),
    MODIFICATION(3);

    private final Integer approval;

    Approval(Integer approval) {
        this.approval = approval;
    }

    public Integer getApproval() {
        return approval;
    }
}
