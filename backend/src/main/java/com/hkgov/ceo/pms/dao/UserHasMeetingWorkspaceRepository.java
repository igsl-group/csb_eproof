package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserHasMeetingWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHasMeetingWorkspaceRepository extends JpaRepository<UserHasMeetingWorkspace, Long> {
    @Query("select u from UserHasMeetingWorkspace u where u.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and u.user.loginId = :loginId")
    UserHasMeetingWorkspace findByMeetingWorkspaceAndUser(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("loginId") String loginId);

    @Query("select u from UserHasMeetingWorkspace u where u.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and u.user.email = :email")
    UserHasMeetingWorkspace findByMeetingWorkspaceAndUserEmail(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("email") String email);

    @Query("select u.user from UserHasMeetingWorkspace u where u.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId")
    List<User> findByMeetingWorkspace(@Param("meetingWorkspaceId") Long meetingWorkspaceId);


}
