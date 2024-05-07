package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.MeetingWorkspaceHasAttendee;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceHasAttendeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MeetingWorkspaceHasAttendeeRepository extends JpaRepository<MeetingWorkspaceHasAttendee, MeetingWorkspaceHasAttendeeId> {
    @Query("select m.attendee.name from MeetingWorkspaceHasAttendee m where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId order by m.attendee.sequence")
    Set<String> findAttendeeOrderBySequence(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query("select m from MeetingWorkspaceHasAttendee m where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and m.attendee.name = :name")
    MeetingWorkspaceHasAttendee findAttendeeByMeetingWorkspaceAndName(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("name") String name);

    @Query("select m from MeetingWorkspaceHasAttendee m where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId")
    List<MeetingWorkspaceHasAttendee> findAttendeeByMeetingWorkspace(@Param("meetingWorkspaceId") Long meetingWorkspaceId);
}
