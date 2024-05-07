package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.AgendaItem;
import com.hkgov.csb.eproof.entity.AgendaItemHasDocument;
import com.hkgov.csb.eproof.entity.AgendaItemHasDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaItemHasDocumentRepository extends JpaRepository<AgendaItemHasDocument, AgendaItemHasDocumentId> {
    @Query("select a.file.fileName from AgendaItemHasDocument a where a.agendaItem = :agendaItem")
    List<String> findFileNameByAgendaItem(@Param("agendaItem") AgendaItem agendaItem);

    @Query("select a from AgendaItemHasDocument a where a.file.path = :path")
    AgendaItemHasDocument findByFilePath(@Param("path") String path);

    @Query("""
            select a from AgendaItemHasDocument a
            where a.agendaItem.agendaItemId = :agendaItemId and a.file.path = :path""")
    AgendaItemHasDocument findByAgendaItemIdAndFilePath(@Param("agendaItemId") Long agendaItemId, @Param("path") String path);

    @Query("""
            select a from AgendaItemHasDocument a
            where a.agendaItem.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId""")
    List<AgendaItemHasDocument> findByMeetingWorkspaceId(@Param("meetingWorkspaceId") Long meetingWorkspaceId);


}
