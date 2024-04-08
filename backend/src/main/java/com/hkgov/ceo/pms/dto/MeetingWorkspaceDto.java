package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.hkgov.ceo.pms.config.Constants.DATE_TIME_PATTERN_NO_SEC;

public class MeetingWorkspaceDto extends BaseEntityDto {
    private Long meetingWorkspaceId;

    private String title;

    private String location;

    @JsonFormat(pattern = DATE_TIME_PATTERN_NO_SEC)
    private LocalDateTime startTime;

    @JsonFormat(pattern = DATE_TIME_PATTERN_NO_SEC)
    private LocalDateTime endTime;

    private Boolean isFreeze;

    private Boolean isDelete;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean isFreeze() {
        return isFreeze;
    }

    public void setFreeze(Boolean freeze) {
        isFreeze = freeze;
    }

    public Long getMeetingWorkspaceId() {
        return meetingWorkspaceId;
    }

    public void setMeetingWorkspaceId(Long meetingWorkspaceId) {
        this.meetingWorkspaceId = meetingWorkspaceId;
    }

    public Boolean getFreeze() {
        return isFreeze;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}
