package com.hkgov.ceo.pms.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "user_has_meeting_workspace")
public class UserHasMeetingWorkspace extends BaseEntity {
    @EmbeddedId
    private UserHasMeetingWorkspaceId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("meetingWorkspaceId")
    @JoinColumn(name = "meeting_workspace_id")
    private MeetingWorkspace meetingWorkspace;

    public UserHasMeetingWorkspaceId getId() {
        return id;
    }

    public void setId(UserHasMeetingWorkspaceId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MeetingWorkspace getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspace meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserHasMeetingWorkspace that = (UserHasMeetingWorkspace) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}