package com.hkgov.csb.eproof.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.data.MeetingWorkspaceRetentionData;
import com.hkgov.csb.eproof.data.SearchData;
import com.hkgov.csb.eproof.dto.AgendaItemDto;
import com.hkgov.csb.eproof.dto.AgendaItemHasDocumentDto;
import com.hkgov.csb.eproof.dto.AttendeeDto;
import com.hkgov.csb.eproof.dto.FolderDto;
import com.hkgov.csb.eproof.dto.MeetingWorkspaceDto;
import com.hkgov.csb.eproof.dto.MeetingWorkspaceHasPostMeetingDocumentDto;
import com.hkgov.csb.eproof.dto.TaskDto;
import com.hkgov.csb.eproof.entity.MeetingWorkspace;
import com.hkgov.csb.eproof.entity.Task;
import com.hkgov.csb.eproof.entity.Views;
import com.hkgov.csb.eproof.mapper.AgendaItemHasDocumentMapper;
import com.hkgov.csb.eproof.mapper.AgendaItemMapper;
import com.hkgov.csb.eproof.mapper.MeetingWorkspaceHasPostMeetingDocumentMapper;
import com.hkgov.csb.eproof.mapper.MeetingWorkspaceMapper;
import com.hkgov.csb.eproof.mapper.MeetingWorkspaceRetentionMapper;
import com.hkgov.csb.eproof.mapper.TaskMapper;
import com.hkgov.csb.eproof.service.ConfigurationService;
import com.hkgov.csb.eproof.service.DocumentService;
import com.hkgov.csb.eproof.service.MeetingWorkspaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static com.hkgov.csb.eproof.config.AuditTrailConstants.MEETING_WORKSPACE_WORDING;
import static com.hkgov.csb.eproof.config.AuditTrailConstants.TASK_WORDING;

@RestController
@RequestMapping("/api/v1/meetingWorkspace")
public class MeetingWorkspaceController {
    private final MeetingWorkspaceService meetingWorkspaceService;
    private final ConfigurationService configurationService;
    private final DocumentService documentService;

    public MeetingWorkspaceController(MeetingWorkspaceService meetingWorkspaceService,
                                      ConfigurationService configurationService, DocumentService documentService) {
        this.meetingWorkspaceService = meetingWorkspaceService;
        this.configurationService = configurationService;
        this.documentService = documentService;
    }

    @GetMapping("/getAllMeetingWorkspace")
    public Page<MeetingWorkspaceDto> getAllMeetingWorkspace(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                                                            @RequestParam(defaultValue = "startTime") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<MeetingWorkspace> meetingWorkspace;
        meetingWorkspace = meetingWorkspaceService.getAllMeetingWorkspaceWithoutRetention(pageable);
        List<MeetingWorkspaceDto> dtoList = meetingWorkspace
                .stream()
                .map(MeetingWorkspaceMapper.INSTANCE::sourceToDestination)
                .toList();
        return new PageImpl<>(dtoList, pageable, meetingWorkspace.getTotalElements());
    }


    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @GetMapping("/meetingWorkspaceRetentionList")
    public Page<MeetingWorkspaceDto> getMeetingWorkspaceRetentionList(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                      @RequestParam(defaultValue = "startTime") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        int workspaceRetentionDays = configurationService.getWorkspaceRetentionDays();
        Page<MeetingWorkspaceRetentionData> retentionPageData = meetingWorkspaceService.getMeetingWorkspaceRetentionList(workspaceRetentionDays, pageable);
        return retentionPageData.map(MeetingWorkspaceRetentionMapper.INSTANCE::toDto);
    }

    @GetMapping("/{meetingWorkspaceId}")
    public MeetingWorkspaceDto getMeetingWorkspaceById(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspaceService.getMeetingWorkspaceById(meetingWorkspaceId));
    }

    @Audit(action = "Create", resourceWording = MEETING_WORKSPACE_WORDING, resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/create")
    public MeetingWorkspaceDto createMeeting(@RequestBody MeetingWorkspaceDto requestDto) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.createMeetingWorkspace(requestDto);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Audit(action = "Update", resourceWording = MEETING_WORKSPACE_WORDING, resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/edit")
    public MeetingWorkspaceDto editMeeting(@RequestBody MeetingWorkspaceDto requestDto) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.editMeetingWorkspace(requestDto);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Audit(action = "Delete", resourceWording = MEETING_WORKSPACE_WORDING, resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @DeleteMapping("/remove")
    public MeetingWorkspaceDto removeMeeting(Long meetingWorkspaceId) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.addRetention(meetingWorkspaceId);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @GetMapping("/{meetingWorkspaceId}/agendaItem")
    public List<AgendaItemDto> getAgendaItemByMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return AgendaItemMapper.INSTANCE.sourceToDestinationList(meetingWorkspaceService.getAgendaItemByMeetingWorkspace(meetingWorkspaceId));
    }

    @GetMapping("/{meetingWorkspaceId}/meetingDocuments")
    public List<AgendaItemHasDocumentDto> getAgendaItemHasDocumentByMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return AgendaItemHasDocumentMapper.INSTANCE.toDtoList(meetingWorkspaceService.getAgendaItemHasDocumentsByMeetingWorkspace(meetingWorkspaceId));
    }

    @GetMapping("/{meetingWorkspaceId}/postMeetingDocuments")
    public List<MeetingWorkspaceHasPostMeetingDocumentDto> meetingWorkspaceHasPostMeetingDocumentByMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return MeetingWorkspaceHasPostMeetingDocumentMapper.INSTANCE.toDtoList(meetingWorkspaceService.getMeetingWorkspaceHasPostMeetingDocumentByMeetingWorkspace(meetingWorkspaceId));
    }

    @JsonView(Views.Public.class)
    @GetMapping("/{meetingWorkspaceId}/privateMeetingDocuments")
    public FolderDto meetingWorkspaceHasPrivateMeetingDocumentByMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return meetingWorkspaceService.getMeetingWorkspaceHasPrivateMeetingDocumentByMeetingWorkspace(meetingWorkspaceId);
    }

    @Audit(action = "Add", resourceWording = "[List of Attendees for Meeting Workspace]: %s", resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/{meetingWorkspaceId}/attendee/batch/add")
    public MeetingWorkspaceDto addAttendees(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId, @RequestParam MultipartFile multipart) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        Set<String> attendeeName = documentService.getAttendeeEmailFromTxt(multipart);
        meetingWorkspaceService.removeAllAttendeeByMeetingWorkspace(meetingWorkspaceId);
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.addAttendees(meetingWorkspaceId, attendeeName);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Audit(action = "Delete", resourceWording = "[Attendee for Meeting Workspace]: %s", resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @DeleteMapping("/{meetingWorkspaceId}/attendee/remove")
    public MeetingWorkspaceDto removeAttendee(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId, @RequestBody AttendeeDto dto) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.removeAttendee(meetingWorkspaceId, dto);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @GetMapping("/{meetingWorkspaceId}/attendee/getAll")
    public Set<String> getAllAttendee(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return meetingWorkspaceService.getAllAttendee(meetingWorkspaceId);
    }

    @Audit(action = "Add", resourceWording = TASK_WORDING, resourceResolverName = "taskResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/{meetingWorkspaceId}/task/add")
    public TaskDto addTask(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId, @RequestBody TaskDto request) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        Task task = meetingWorkspaceService.addTask(meetingWorkspaceId, request);
        return TaskMapper.INSTANCE.sourceToDestination(task);
    }

    @Audit(action = "Update", resourceWording = TASK_WORDING, resourceResolverName = "taskResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/{meetingWorkspaceId}/task/edit")
    public TaskDto editTask(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId, @RequestBody TaskDto request) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        Task task = meetingWorkspaceService.editTask(request);
        return TaskMapper.INSTANCE.sourceToDestination(task);
    }

    @Audit(action = "Delete", resourceWording = TASK_WORDING, resourceResolverName = "taskResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @DeleteMapping("/{meetingWorkspaceId}/task/remove")
    public TaskDto removeTask(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId, @RequestParam Long taskId) {
        meetingWorkspaceService.freezeValidation(meetingWorkspaceId);
        Task task = meetingWorkspaceService.removeTask(taskId);
        return TaskMapper.INSTANCE.sourceToDestination(task);
    }

    @GetMapping("/{meetingWorkspaceId}/task")
    public List<TaskDto> getTasksByMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        return meetingWorkspaceService.getTasksByMeetingWorkspace(meetingWorkspaceId)
                .stream()
                .map(TaskMapper.INSTANCE::sourceToDestination)
                .toList();
    }

    @GetMapping("/calendar")
    public List<MeetingWorkspaceDto> getCalendar(@RequestParam String year, @RequestParam(required = false) String month) {
        return meetingWorkspaceService.getCalendar(year, month)
                .stream()
                .map(MeetingWorkspaceMapper.INSTANCE::sourceToDestination)
                .toList();
    }


    @Audit(action = "Freeze", resourceWording = MEETING_WORKSPACE_WORDING, resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/{meetingWorkspaceId}/freeze")
    public MeetingWorkspaceDto freezeMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.freezeMeetingWorkspace(meetingWorkspaceId);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @Audit(action = "Unfreeze", resourceWording = MEETING_WORKSPACE_WORDING, resourceResolverName = "meetingWorkspaceResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/{meetingWorkspaceId}/unfreeze")
    public MeetingWorkspaceDto unfreezeMeetingWorkspace(@PathVariable(value = "meetingWorkspaceId") Long meetingWorkspaceId) {
        MeetingWorkspace meetingWorkspace = meetingWorkspaceService.unfreezeMeetingWorkspace(meetingWorkspaceId);
        return MeetingWorkspaceMapper.INSTANCE.sourceToDestination(meetingWorkspace);
    }

    @GetMapping("/search")
    public SearchData search(@RequestParam String keyword) {
        return meetingWorkspaceService.search(keyword);
    }
}
