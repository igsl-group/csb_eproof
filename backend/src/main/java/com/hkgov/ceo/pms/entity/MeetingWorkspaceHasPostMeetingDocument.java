package com.hkgov.ceo.pms.entity;

import com.hkgov.ceo.pms.entity.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "meeting_workspace_has_post_meeting_document")
public class MeetingWorkspaceHasPostMeetingDocument extends BaseEntity {
    @EmbeddedId
    private MeetingWorkspaceHasPostMeetingDocumentId id;

    @ManyToOne
    @MapsId("meetingWorkspaceId")
    @JoinColumn(name = "meeting_workspace_id")
    private MeetingWorkspace meetingWorkspace;

    @ManyToOne
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne
    @JoinColumn(name = "prepared_by", referencedColumnName = "user_id")
    private User preparedBy;

    @ManyToOne
    @JoinColumn(name = "approved_by", referencedColumnName = "user_id")
    private User approvedBy;

    @ManyToOne
    @JoinColumn(name = "rejected_by", referencedColumnName = "user_id")
    private User rejectedBy;

    @Column(name = "status")
    private Status status;

    public MeetingWorkspaceHasPostMeetingDocumentId getId() {
        return id;
    }

    public void setId(MeetingWorkspaceHasPostMeetingDocumentId id) {
        this.id = id;
    }

    public MeetingWorkspace getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspace meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public User getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(User preparedBy) {
        this.preparedBy = preparedBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(User rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MeetingWorkspaceHasPostMeetingDocument that = (MeetingWorkspaceHasPostMeetingDocument) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}