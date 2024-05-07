package com.hkgov.csb.eproof.dto;

import com.hkgov.csb.eproof.entity.enums.Status;

public class AgendaItemHasDocumentDto {
    private Long agendaItemId;
    private FileDto file;
    private UserDto preparedBy;
    private UserDto approvedBy;
    private UserDto rejectedBy;
    private Status status;

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
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

    public Long getAgendaItemId() {
        return agendaItemId;
    }

    public void setAgendaItemId(Long agendaItemId) {
        this.agendaItemId = agendaItemId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
