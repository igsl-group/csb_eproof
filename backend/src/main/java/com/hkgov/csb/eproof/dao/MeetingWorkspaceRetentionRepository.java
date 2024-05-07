package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.MeetingWorkspace;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceRetention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingWorkspaceRetentionRepository extends JpaRepository<MeetingWorkspaceRetention, Long> {
    @Query(value = """
            select * from meeting_workspace_retention as m where create_date < DATE_SUB(NOW(), INTERVAL :retentionDay DAY)
                        """, nativeQuery = true)
    List<MeetingWorkspaceRetention> findByRetentionDay(@Param("retentionDay") int retentionDay);

    @Query("select m from MeetingWorkspaceRetention m where m.meetingWorkspace = :meetingWorkspace")
    List<MeetingWorkspaceRetention> findByMeetingWorkspace(@Param("meetingWorkspace") MeetingWorkspace meetingWorkspace);
}