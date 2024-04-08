package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserHasMeetingWorkspaceId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "meeting_workspace_id")
    private Long meetingWorkspaceId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMeetingWorkspaceId() {
        return meetingWorkspaceId;
    }

    public void setMeetingWorkspaceId(Long meetingWorkspaceId) {
        this.meetingWorkspaceId = meetingWorkspaceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserHasMeetingWorkspaceId that = (UserHasMeetingWorkspaceId) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
                && getMeetingWorkspaceId() != null && Objects.equals(getMeetingWorkspaceId(), that.getMeetingWorkspaceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, meetingWorkspaceId);
    }
}