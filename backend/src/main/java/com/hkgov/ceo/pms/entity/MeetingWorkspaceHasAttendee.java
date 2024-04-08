package com.hkgov.ceo.pms.entity;

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
@Table(name = "meeting_workspace_has_attendee")
public class MeetingWorkspaceHasAttendee {
    @EmbeddedId
    private MeetingWorkspaceHasAttendeeId id;

    @ManyToOne
    @MapsId("meetingWorkspaceId")
    @JoinColumn(name = "meeting_workspace_id")
    private MeetingWorkspace meetingWorkspace;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("attendeeId")
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    public MeetingWorkspaceHasAttendeeId getId() {
        return id;
    }

    public void setId(MeetingWorkspaceHasAttendeeId id) {
        this.id = id;
    }

    public MeetingWorkspace getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspace meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MeetingWorkspaceHasAttendee that = (MeetingWorkspaceHasAttendee) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}