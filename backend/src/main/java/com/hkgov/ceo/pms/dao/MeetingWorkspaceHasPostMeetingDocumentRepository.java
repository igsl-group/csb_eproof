package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.MeetingWorkspace;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPostMeetingDocument;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPostMeetingDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingWorkspaceHasPostMeetingDocumentRepository extends JpaRepository<MeetingWorkspaceHasPostMeetingDocument, MeetingWorkspaceHasPostMeetingDocumentId> {
    @Query("""
            select m.file.fileName from MeetingWorkspaceHasPostMeetingDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId""")
    List<String> findFileNameByMeetingWorkspace(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query("""
            select m from MeetingWorkspaceHasPostMeetingDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and m.file.path = :path""")
    MeetingWorkspaceHasPostMeetingDocument findByMeetingWorkspaceIdAndFilePath(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("path") String path);

    @Query("""
            select m from MeetingWorkspaceHasPostMeetingDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId""")
    List<MeetingWorkspaceHasPostMeetingDocument> findByMeetingWorkspaceId(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query("""
            select m from MeetingWorkspaceHasPostMeetingDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and m.status = 'APPROVED'""")
    List<MeetingWorkspaceHasPostMeetingDocument> findApprovedByMeetingWorkspaceId(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query("""
            select m from MeetingWorkspaceHasPostMeetingDocument m
            left join MeetingWorkspace mw on m.meetingWorkspace = mw
            left join File f on m.file = f
            where mw = :meetingWorkspace and f.path = :path""")
    MeetingWorkspaceHasPostMeetingDocument findByFilePath(@Param("meetingWorkspace")MeetingWorkspace meetingWorkspace, @Param("path") String path);
}