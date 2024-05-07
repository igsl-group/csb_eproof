package com.hkgov.csb.eproof.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "meeting_workspace_retention")
public class MeetingWorkspaceRetention extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_workspace_retention_id", nullable = false)
    private Long meetingWorkspaceRetentionId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "meeting_workspace_id", nullable = false)
    private MeetingWorkspace meetingWorkspace;

    public Long getMeetingWorkspaceRetentionId() {
        return meetingWorkspaceRetentionId;
    }

    public void setMeetingWorkspaceRetentionId(Long meetingWorkspaceRetentionId) {
        this.meetingWorkspaceRetentionId = meetingWorkspaceRetentionId;
    }

    public MeetingWorkspace getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspace meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }
}