package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.MeetingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("select f from File f where f.path = :path")
    File findByPath(@Param("path") String path);

    @Query("""
            select f
            from File f
                     left join MeetingWorkspaceHasPostMeetingDocument mwhpmd on f = mwhpmd.file
                     left join MeetingWorkspaceHasPrivateDocument mwhpd on f = mwhpd.file
                     left join AgendaItemHasDocument aihd on f = aihd.file
                     left join MeetingGroup mg on mwhpd.meetingGroup = mg
            where f.path = :path
              AND (mwhpmd.status = 'APPROVED' OR mwhpd.meetingGroup in :meetingGroups OR aihd.status = 'APPROVED')
            """)
    File findApprovedOrCorrectMeetingGroupDocument(@Param("path") String path, @Param("meetingGroups") List<MeetingGroup> meetingGroups);
}
