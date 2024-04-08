package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPrivateDocument;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPrivateDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingWorkspaceHasPrivateDocumentRepository extends JpaRepository<MeetingWorkspaceHasPrivateDocument, MeetingWorkspaceHasPrivateDocumentId> {
    @Query("""
            select m.file.fileName from MeetingWorkspaceHasPrivateDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and m.meetingGroup.code = :meetingGroupCode and m.file.path = :path""")
    List<String> findByMeetingWorkspaceAndGroupCodeAndPath(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("meetingGroupCode") String meetingGroupCode, @Param("path") String path);

    @Query("select m from MeetingWorkspaceHasPrivateDocument m where m.file.path = :path")
    MeetingWorkspaceHasPrivateDocument findByFilePath(@Param("path") String path);

    @Query("""
            select m from MeetingWorkspaceHasPrivateDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and m.file.path = :path""")
    MeetingWorkspaceHasPrivateDocument findByMeetingWorkspaceIdAndFilePath(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("path") String path);

    @Query("""
            select m from MeetingWorkspaceHasPrivateDocument m
            where m.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId""")
    List<MeetingWorkspaceHasPrivateDocument> findByMeetingWorkspaceId(@Param("meetingWorkspaceId") Long meetingWorkspaceId);


}
