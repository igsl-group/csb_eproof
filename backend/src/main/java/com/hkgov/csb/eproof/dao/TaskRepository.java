package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.taskId = :taskId")
    Task findByTaskId(@Param("taskId") Long taskId);

    @Query("select t from Task t where t.meetingWorkspace.meetingWorkspaceId = :meetingWorkspaceId")
    List<Task> findByMeetingWorkspaceId(@Param("meetingWorkspaceId") Long meetingWorkspaceId);

    @Query(value = """
              select t.*
                        from task t
                        where t.title like concat('%', :keyword, '%')
                           or t.note like concat('%', :keyword, '%')
                           or t.user like concat('%', :keyword, '%')
            """, nativeQuery = true)
    List<Task> findByTitleOrNoteOrAssignee(@Param("keyword") String keyword);

    @Query(value = """
            select t.*
            from task t
            left join meeting_workspace mw on t.meeting_workspace_id = mw.meeting_workspace_id
            where
                (t.title like concat('%', :keyword, '%')
               or t.note like concat('%', :keyword, '%')
               or t.user like concat('%', :keyword, '%'))
                        """, nativeQuery = true)
    List<Task> findApprovedByTitleOrNoteOrAssignee(@Param("keyword") String keyword);
}
