package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MeetingWorkspaceHasPrivateDocumentId implements Serializable {
    @Column(name = "meeting_workspace_id")
    private Long meetingWorkspaceId;

    @Column(name = "file_id")
    private Long fileId;

    public Long getMeetingWorkspaceId() {
        return meetingWorkspaceId;
    }

    public void setMeetingWorkspaceId(Long meetingWorkspaceId) {
        this.meetingWorkspaceId = meetingWorkspaceId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MeetingWorkspaceHasPrivateDocumentId that = (MeetingWorkspaceHasPrivateDocumentId) o;
        return getMeetingWorkspaceId() != null && Objects.equals(getMeetingWorkspaceId(), that.getMeetingWorkspaceId())
                && getFileId() != null && Objects.equals(getFileId(), that.getFileId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingWorkspaceId, fileId);
    }
}