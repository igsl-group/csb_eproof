package com.hkgov.ceo.pms.data;

import java.time.LocalDateTime;

public class MeetingWorkspaceRetentionData {

    private Long meetingWorkspaceId;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer remainingDays;

    public MeetingWorkspaceRetentionData(Long meetingWorkspaceId, String title, String location, LocalDateTime startTime, LocalDateTime endTime, Integer remainingDays) {
        this.meetingWorkspaceId = meetingWorkspaceId;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remainingDays = remainingDays;
    }

    public Long getMeetingWorkspaceId() {
        return meetingWorkspaceId;
    }

    public void setMeetingWorkspaceId(Long meetingWorkspaceId) {
        this.meetingWorkspaceId = meetingWorkspaceId;
    }

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

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
}
