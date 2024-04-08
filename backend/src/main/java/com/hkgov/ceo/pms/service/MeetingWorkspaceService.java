package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.data.MeetingWorkspaceRetentionData;
import com.hkgov.ceo.pms.data.SearchData;
import com.hkgov.ceo.pms.dto.AttendeeDto;
import com.hkgov.ceo.pms.dto.FolderDto;
import com.hkgov.ceo.pms.dto.MeetingWorkspaceDto;
import com.hkgov.ceo.pms.dto.TaskDto;
import com.hkgov.ceo.pms.entity.AgendaItem;
import com.hkgov.ceo.pms.entity.AgendaItemHasDocument;
import com.hkgov.ceo.pms.entity.MeetingWorkspace;
import com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPostMeetingDocument;
import com.hkgov.ceo.pms.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface MeetingWorkspaceService {
    Page<MeetingWorkspace> getAllMeetingWorkspaceWithoutRetention(Pageable pageable);

    Page<MeetingWorkspace> getMeetingWorkspaceByUser(Pageable pageable);

    MeetingWorkspace createMeetingWorkspace(MeetingWorkspaceDto request);

    MeetingWorkspace editMeetingWorkspace(MeetingWorkspaceDto request);

    void purgeMeetingWorkspaceByRetention(int day);

    MeetingWorkspace addRetention(Long id);

    void retainMeetingWorkspaceByLimit(int limitNo);

    List<AgendaItem> getAgendaItemByMeetingWorkspace(Long id);

    MeetingWorkspace getMeetingWorkspaceById(Long id);

    MeetingWorkspace getMeetingWorkspaceByIdAndUser(Long id);

    MeetingWorkspace addAttendees(Long id, Set<String> attendeeName);

    void removeAllAttendeeByMeetingWorkspace(Long id);

    MeetingWorkspace removeAttendee(Long id, AttendeeDto dto);

    Set<String> getAllAttendee(Long id);

    Task addTask(Long id, TaskDto request);

    Task editTask(TaskDto request);

    Task removeTask(Long taskId);

    List<Task> getTasksByMeetingWorkspace(Long id);

    List<MeetingWorkspace> getCalendar(String year, String month);

    List<AgendaItemHasDocument> getAgendaItemHasDocumentsByMeetingWorkspace(Long id);

    List<MeetingWorkspaceHasPostMeetingDocument> getMeetingWorkspaceHasPostMeetingDocumentByMeetingWorkspace(Long id);

    FolderDto getMeetingWorkspaceHasPrivateMeetingDocumentByMeetingWorkspace(Long id);

    MeetingWorkspace freezeMeetingWorkspace(Long meetingWorkspaceId);

    MeetingWorkspace unfreezeMeetingWorkspace(Long meetingWorkspaceId);

    void freezeValidation(Long meetingWorkspaceId);

    SearchData search(String keyword);

    Page<MeetingWorkspaceRetentionData> getMeetingWorkspaceRetentionList(int workspaceRetentionDays, Pageable pageable);
}
