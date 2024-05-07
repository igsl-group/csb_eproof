package com.hkgov.csb.eproof.dto;

import com.hkgov.csb.eproof.entity.enums.Status;

import java.util.List;

public class AgendaItemDto extends BaseEntityDto {
    private Long agendaItemId;
    private String title;
    private String presenter;
    private String note;
    private Integer sequence;
    private Status status;
    private UserDto preparedBy;
    private UserDto approvedBy;
    private UserDto rejectedBy;
    private MeetingWorkspaceDto meetingWorkspace;
    private List<AgendaItemHasDocumentDto> agendaItemHasDocuments;

    public Long getAgendaItemId() {
        return agendaItemId;
    }

    public void setAgendaItemId(Long agendaItemId) {
        this.agendaItemId = agendaItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public UserDto getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(UserDto preparedBy) {
        this.preparedBy = preparedBy;
    }

    public UserDto getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UserDto approvedBy) {
        this.approvedBy = approvedBy;
    }

    public UserDto getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(UserDto rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public MeetingWorkspaceDto getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspaceDto meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<AgendaItemHasDocumentDto> getAgendaItemHasDocuments() {
        return agendaItemHasDocuments;
    }

    public void setAgendaItemHasDocuments(List<AgendaItemHasDocumentDto> agendaItemHasDocuments) {
        this.agendaItemHasDocuments = agendaItemHasDocuments;
    }
}
