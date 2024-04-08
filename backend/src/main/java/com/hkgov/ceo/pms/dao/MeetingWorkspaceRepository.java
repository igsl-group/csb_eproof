package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.data.MeetingWorkspaceRetentionData;
import com.hkgov.ceo.pms.entity.File;
import com.hkgov.ceo.pms.entity.MeetingGroup;
import com.hkgov.ceo.pms.entity.MeetingWorkspace;
import com.hkgov.ceo.pms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingWorkspaceRepository extends JpaRepository<MeetingWorkspace, Long> {
    @Query("select m from MeetingWorkspace m where m.meetingWorkspaceId = :meetingWorkspaceId")
    MeetingWorkspace findByMeetingWorkspaceId(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query("select m from MeetingWorkspace m left join UserHasMeetingWorkspace u on u.meetingWorkspace = m where m.meetingWorkspaceId = :meetingWorkspaceId and u.user = :user")
    MeetingWorkspace findByMeetingWorkspaceIdAndUser(@Param("meetingWorkspaceId") Long meetingWorkspaceId, @Param("user") User user);

    @Query(value = """
              select mw.*
                        from meeting_workspace mw
                                 left join user_has_meeting_workspace uhmw on mw.meeting_workspace_id = uhmw.meeting_workspace_id
                                 left join user u on uhmw.user_id = u.user_id
                                 left join meeting_workspace_retention on mw.meeting_workspace_id = meeting_workspace_retention.meeting_workspace_id
                        where u.login_id = :loginId
                          and year(mw.start_time) = :year
                          and (:month is null or month(mw.start_time) = :month)
                          and meeting_workspace_retention.meeting_workspace_id is null
            """, nativeQuery = true)
    List<MeetingWorkspace> findByLoginAndYearAndMonth(@Param("loginId") String loginId, @Param("year") String year, @Param("month") String month);

    @Query(value = """
              select mw.*
                        from meeting_workspace mw
                                 left join meeting_workspace_retention on mw.meeting_workspace_id = meeting_workspace_retention.meeting_workspace_id
                        where year(mw.start_time) = :year
                          and (:month is null or month(mw.start_time) = :month)
                          and meeting_workspace_retention.meeting_workspace_id is null
            """, nativeQuery = true)
    List<MeetingWorkspace> findByYearAndMonth(@Param("year") String year, @Param("month") String month);

    @Query("""
            select m from MeetingWorkspace m 
            inner join m.userHasMeetingWorkspaces userHasMeetingWorkspaces
            left join MeetingWorkspaceRetention mr on mr.meetingWorkspace = m
            where userHasMeetingWorkspaces.user = :user
            and mr.meetingWorkspace is null
            """)
    Page<MeetingWorkspace> findWithoutRetentionByUser(@Param("user") User user, Pageable pageable);

    @Query(value = """
             select mw.* from meeting_workspace as mw
                 left join meeting_workspace_retention mwr on mw.meeting_workspace_id = mwr.meeting_workspace_id
             where mwr.meeting_workspace_id is null
                 and mw.end_time < now()
             order by mw.end_time desc
             limit :offset, 18446744073709551615;
            """, nativeQuery = true)
    List<MeetingWorkspace> findMeetingWorkspaceByOffset(@Param("offset") int offset);

    @Query(value = """
                select mg from MeetingWorkspace m 
                    left join UserHasMeetingWorkspace uhmw on m = uhmw.meetingWorkspace
                    left join User u on u = uhmw.user
                    left join UserHasMeetingGroup uhmg on uhmg.user = u
                    left join MeetingGroup mg on uhmg.meetingGroup = mg
                where m.meetingWorkspaceId = :meetingWorkspaceId
                and mg.meetingGroupId is not null
            """)
    List<MeetingGroup> findMeetingGroupNameByMeetingWorkspace(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query(value = """
                select mg from UserHasMeetingGroup uhmg
                left join User u on uhmg.user = u
                left join MeetingGroup mg on uhmg.meetingGroup = mg
                where u.loginId = :loginId
            """)
    List<MeetingGroup> findMeetingGroupNameByMeetingWorkspaceAndUser(@Param("loginId") String loginId);

    @Query(value = """
            select m from MeetingWorkspace m
            left join MeetingWorkspaceRetention mr on mr.meetingWorkspace = m
            where mr.meetingWorkspace is null
            """)
    Page<MeetingWorkspace> findAllWithoutRetention(Pageable pageable);

    @Query("select m from MeetingWorkspace m where m.title like %:keyword% or m.location like %:keyword%")
    List<MeetingWorkspace> findByTitleOrLocation(@Param("keyword") String keyword);

    @Query("select (count(m) > 0) from MeetingWorkspace m where m.title = :title")
    boolean existsByTitle(@Param("title") @NonNull String title);

    @Query("select (count(m) > 0) from MeetingWorkspace m where (:startTime >= m.startTime and :startTime < m.endTime) or (:endTime > m.startTime and :endTime <= m.endTime)")
    boolean idDuplicateTimeslot(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query(value = """
            select f from MeetingWorkspace m 
                left join AgendaItem a on m = a.meetingWorkspace 
                left join AgendaItemHasDocument aihd on aihd.agendaItem = a 
                left join File f on f = aihd.file
            where f.fileName like %:keyword% and aihd.status = 'APPROVED' and a.status = 'APPROVED'
            """)
    List<File> findApprovedAgendaItemFileByFileName(@Param("keyword") String keyword);

    @Query(value = """
            select f from MeetingWorkspace m 
                left join AgendaItem a on m = a.meetingWorkspace 
                left join AgendaItemHasDocument aihd on aihd.agendaItem = a 
                left join File f on f = aihd.file
            where f.fileName like %:keyword%
            """)
    List<File> findAgendaItemFileByFileName(@Param("keyword") String keyword);

    @Query(value = """
            select f from MeetingWorkspace m 
                left join MeetingWorkspaceHasPostMeetingDocument mp on m = mp.meetingWorkspace 
                left join File f on f = mp.file
            where f.fileName like %:keyword% and mp.status = 'APPROVED'
            """)
    List<File> findApprovedPostMeetingDocumentFileByFileName(@Param("keyword") String keyword);

    @Query(value = """
            select f from MeetingWorkspace m 
                left join MeetingWorkspaceHasPostMeetingDocument mp on m = mp.meetingWorkspace 
                left join File f on f = mp.file
            where f.fileName like %:keyword%
            """)
    List<File> findPostMeetingDocumentFileByFileName(@Param("keyword") String keyword);

    @Query(value = """
            select f from MeetingWorkspace m 
                left join MeetingWorkspaceHasPrivateDocument mp on m = mp.meetingWorkspace 
                left join File f on f = mp.file
                left join MeetingGroup mg on mg = mp.meetingGroup
                left join UserHasMeetingGroup uhmg on uhmg.meetingGroup = mg
            where f.fileName like %:keyword% and uhmg.user = :user
            """)
    List<File> findPrivateMeetingDocumentFileByFileNameAndUser(@Param("keyword") String keyword, @Param("user") User user);

    @Query(value = """
            select f from MeetingWorkspace m 
                left join MeetingWorkspaceHasPrivateDocument mp on m = mp.meetingWorkspace 
                left join File f on f = mp.file
            where f.fileName like %:keyword%
            """)
    List<File> findPrivateMeetingDocumentFileByFileName(@Param("keyword") String keyword);

    @Query(value = """
            select new com.hkgov.ceo.pms.data.MeetingWorkspaceRetentionData(
                m.meetingWorkspaceId,
                m.title,
                m.location,
                m.startTime,
                m.endTime,
                CASE
                    WHEN DATEDIFF(ADDDATE(DATE(mr.createDate), :workspaceRetentionDays), CURRENT_TIMESTAMP) < 0 THEN 0
                    ELSE DATEDIFF(ADDDATE(DATE(mr.createDate), :workspaceRetentionDays), CURRENT_TIMESTAMP)
                END
            ) from MeetingWorkspace m
            inner join MeetingWorkspaceRetention mr on mr.meetingWorkspace = m
            """)
    Page<MeetingWorkspaceRetentionData> findAllMeetingWorkspaceRetention(@Param("workspaceRetentionDays") int workspaceRetentionDays, Pageable pageable);

    @Query("select (count(m) > 0) from MeetingWorkspace m right join MeetingWorkspaceRetention mr on m = mr.meetingWorkspace where m = :meetingWorkspace")
    boolean isMeetingWorkspaceDelete(@Param("meetingWorkspace") MeetingWorkspace meetingWorkspace);


}
