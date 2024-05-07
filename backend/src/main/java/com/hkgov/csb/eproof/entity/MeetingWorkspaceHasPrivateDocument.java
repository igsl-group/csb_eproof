package com.hkgov.csb.eproof.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "meeting_workspace_has_private_document")
public class MeetingWorkspaceHasPrivateDocument extends BaseEntity {
    @EmbeddedId
    private MeetingWorkspaceHasPrivateDocumentId id;

    @ManyToOne
    @MapsId("meetingWorkspaceId")
    @JoinColumn(name = "meeting_workspace_id")
    private MeetingWorkspace meetingWorkspace;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne
    @JoinColumn(name = "meeting_group_id")
    private MeetingGroup meetingGroup;

    public MeetingWorkspaceHasPrivateDocumentId getId() {
        return id;
    }

    public void setId(MeetingWorkspaceHasPrivateDocumentId id) {
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

    public MeetingGroup getMeetingGroup() {
        return meetingGroup;
    }

    public void setMeetingGroup(MeetingGroup meetingGroup) {
        this.meetingGroup = meetingGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MeetingWorkspaceHasPrivateDocument that = (MeetingWorkspaceHasPrivateDocument) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}