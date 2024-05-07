package com.hkgov.csb.eproof.dto;

import com.hkgov.csb.eproof.entity.enums.Status;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.hkgov.csb.eproof.entity.MeetingWorkspaceHasPostMeetingDocument} entity
 */
public class MeetingWorkspaceHasPostMeetingDocumentDto implements Serializable {
    private final FileDto file;
    private final UserDto preparedBy;
    private final UserDto approvedBy;
    private final UserDto rejectedBy;
    private final Status status;

    public MeetingWorkspaceHasPostMeetingDocumentDto(FileDto file, UserDto preparedBy, UserDto approvedBy, UserDto rejectedBy, Status status) {
        this.file = file;
        this.preparedBy = preparedBy;
        this.approvedBy = approvedBy;
        this.rejectedBy = rejectedBy;
        this.status = status;
    }

    public FileDto getFile() {
        return file;
    }

    public UserDto getPreparedBy() {
        return preparedBy;
    }

    public UserDto getApprovedBy() {
        return approvedBy;
    }

    public UserDto getRejectedBy() {
        return rejectedBy;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingWorkspaceHasPostMeetingDocumentDto entity = (MeetingWorkspaceHasPostMeetingDocumentDto) o;
        return Objects.equals(this.file, entity.file) &&
                Objects.equals(this.preparedBy, entity.preparedBy) &&
                Objects.equals(this.approvedBy, entity.approvedBy) &&
                Objects.equals(this.rejectedBy, entity.rejectedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, preparedBy, approvedBy, rejectedBy);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "file = " + file + ", " +
                "preparedBy = " + preparedBy + ", " +
                "approvedBy = " + approvedBy + ", " +
                "rejectedBy = " + rejectedBy + ")";
    }


}