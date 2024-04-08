package com.hkgov.ceo.pms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "meeting_group", indexes = {
        @Index(name = "idx_meetinggroup_code_unq", columnList = "code", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_meeting_group_code", columnNames = {"code"})
})
public class MeetingGroup extends BaseEntity {
    @Id
    @Column(name = "meeting_group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingGroupId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingGroup", orphanRemoval = true)
    private List<UserHasMeetingGroup> userHasMeetingGroups = new ArrayList<>();

    public UserHasMeetingGroup createUserHasGroup(User user) {
        UserHasMeetingGroupId userHasMeetingGroupId = new UserHasMeetingGroupId();
        userHasMeetingGroupId.setMeetingGroupId(this.meetingGroupId);
        userHasMeetingGroupId.setUserId(user.getUserId());
        UserHasMeetingGroup userHasMeetingGroup = new UserHasMeetingGroup();
        userHasMeetingGroup.setId(userHasMeetingGroupId);
        userHasMeetingGroup.setUser(user);
        userHasMeetingGroup.setMeetingGroup(this);
        return userHasMeetingGroup;
    }

    public void addUserHasGroup(User user) {
        addUserHasGroup(createUserHasGroup(user));
    }

    public void addUserHasGroup(UserHasMeetingGroup userHasMeetingGroup) {
        if (!userHasMeetingGroups.contains(userHasMeetingGroup)) {
            userHasMeetingGroups.add(userHasMeetingGroup);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        MeetingGroup that = (MeetingGroup) o;
        return getMeetingGroupId() != null && Objects.equals(getMeetingGroupId(), that.getMeetingGroupId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}