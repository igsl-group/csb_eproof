package com.hkgov.ceo.pms.dto;

public class TaskDto extends BaseEntityDto {
    private Long taskId;
    private String assignedTo;

    private String title;

    private String note;

    private MeetingWorkspaceDto meetingWorkspace;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public MeetingWorkspaceDto getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspaceDto meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
