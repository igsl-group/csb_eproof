package com.hkgov.csb.eproof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MeetingWorkspaceHasAttendeeId implements Serializable {
    @Column(name = "meeting_workspace_id")
    private Long meetingWorkspaceId;

    @Column(name = "attendee_id")
    private Long attendeeId;

    public Long getMeetingWorkspaceId() {
        return meetingWorkspaceId;
    }

    public void setMeetingWorkspaceId(Long meetingWorkspaceId) {
        this.meetingWorkspaceId = meetingWorkspaceId;
    }

    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MeetingWorkspaceHasAttendeeId that = (MeetingWorkspaceHasAttendeeId) o;
        return getAttendeeId() != null && Objects.equals(getAttendeeId(), that.getAttendeeId())
                && getMeetingWorkspaceId() != null && Objects.equals(getMeetingWorkspaceId(), that.getMeetingWorkspaceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendeeId, meetingWorkspaceId);
    }
}