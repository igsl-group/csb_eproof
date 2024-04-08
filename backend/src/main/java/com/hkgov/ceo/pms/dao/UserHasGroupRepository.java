package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.MeetingGroup;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserHasMeetingGroup;
import com.hkgov.ceo.pms.entity.UserHasMeetingGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHasGroupRepository extends JpaRepository<UserHasMeetingGroup, UserHasMeetingGroupId> {
    @Query("select u from UserHasMeetingGroup u where u.user = :user and u.meetingGroup = :group")
    UserHasMeetingGroup findByUserAndGroup(@Param("user") User user, @Param("group") MeetingGroup group);

    @Query("select u.meetingGroup from UserHasMeetingGroup u where u.user = :user")
    List<MeetingGroup> findByUser(@Param("user") User user);


}