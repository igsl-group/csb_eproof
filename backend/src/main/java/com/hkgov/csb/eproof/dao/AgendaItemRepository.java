package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {
    @Query("select a from AgendaItem a where a.agendaItemId = :agendaItemId")
    AgendaItem findByAgendaItemId(@Param("agendaItemId") Long agendaItemId);

    @Query("select a from AgendaItem a where a.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId")
    List<AgendaItem> findByMeetingWorkspace(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query("select a from AgendaItem a where a.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId and a.status = 'APPROVED'")
    List<AgendaItem> findApprovedByMeetingWorkspace(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query(value = """
              select a.*
                        from agenda_item a
                        where a.title like concat('%', :keyword, '%')
                           or a.presenter like concat('%', :keyword, '%')
                           or a.note like concat('%', :keyword, '%')
                           or a.status like concat('%', :keyword, '%')
            """, nativeQuery = true)
    List<AgendaItem> findByTitleOrPresenterOrNoteOrStatus(@Param("keyword") String keyword);

    @Query(value = """
            select a.*
            from agenda_item a
                     left join meeting_workspace mw on a.meeting_workspace_id = mw.meeting_workspace_id
            where 
                a.status = 'APPROVED'
                and (a.title like concat('%', :keyword, '%')
               or a.presenter like concat('%', :keyword, '%')
               or a.note like concat('%', :keyword, '%')
               or a.status like concat('%', :keyword, '%'))
                        """, nativeQuery = true)
    List<AgendaItem> findApprovedByTitleOrPresenterOrNoteOrStatus(@Param("keyword") String keyword);
}
