package com.hkgov.csb.eproof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserHasMeetingGroupId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "meeting_group_id")
    private Long meetingGroupId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMeetingGroupId() {
        return meetingGroupId;
    }

    public void setMeetingGroupId(Long meetingGroupId) {
        this.meetingGroupId = meetingGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserHasMeetingGroupId that = (UserHasMeetingGroupId) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
                && getMeetingGroupId() != null && Objects.equals(getMeetingGroupId(), that.getMeetingGroupId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, meetingGroupId);
    }
}