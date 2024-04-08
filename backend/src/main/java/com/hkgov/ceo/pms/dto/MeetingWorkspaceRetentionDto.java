package com.hkgov.ceo.pms.dto;

public class MeetingWorkspaceRetentionDto extends MeetingWorkspaceDto {
    private Integer remainingDays;

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
}
