package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.AgendaItemHasDocumentRepository;
import com.hkgov.csb.eproof.dao.AgendaItemRepository;
import com.hkgov.csb.eproof.dao.MeetingGroupRepository;
import com.hkgov.csb.eproof.dao.MeetingWorkspaceHasAttendeeRepository;
import com.hkgov.csb.eproof.dao.MeetingWorkspaceHasPostMeetingDocumentRepository;
import com.hkgov.csb.eproof.dao.MeetingWorkspaceHasPrivateDocumentRepository;
import com.hkgov.csb.eproof.dao.MeetingWorkspaceRepository;
import com.hkgov.csb.eproof.dao.MeetingWorkspaceRetentionRepository;
import com.hkgov.csb.eproof.dao.TaskRepository;
import com.hkgov.csb.eproof.data.AgendaSearchData;
import com.hkgov.csb.eproof.data.DocumentSearchData;
import com.hkgov.csb.eproof.data.MeetingSearchData;
import com.hkgov.csb.eproof.data.MeetingWorkspaceRetentionData;
import com.hkgov.csb.eproof.data.SearchData;
import com.hkgov.csb.eproof.data.TaskSearchData;
import com.hkgov.csb.eproof.dto.AttendeeDto;
import com.hkgov.csb.eproof.dto.FolderDto;
import com.hkgov.csb.eproof.dto.MeetingWorkspaceDto;
import com.hkgov.csb.eproof.dto.TaskDto;
import com.hkgov.csb.eproof.entity.AgendaItem;
import com.hkgov.csb.eproof.entity.AgendaItemHasDocument;
import com.hkgov.csb.eproof.entity.Attendee;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.MeetingGroup;
import com.hkgov.csb.eproof.entity.MeetingWorkspace;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceHasAttendee;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceHasPostMeetingDocument;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceHasPrivateDocument;
import com.hkgov.csb.eproof.entity.MeetingWorkspaceRetention;
import com.hkgov.csb.eproof.entity.Task;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.enums.Status;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.MeetingWorkspaceHasPrivateDocumentMapper;
import com.hkgov.csb.eproof.mapper.MeetingWorkspaceMapper;
import com.hkgov.csb.eproof.mapper.TaskMapper;
import com.hkgov.csb.eproof.service.AuthenticatedInfoService;
import com.hkgov.csb.eproof.service.LocationService;
import com.hkgov.csb.eproof.service.MeetingWorkspaceService;
import com.hkgov.csb.eproof.service.UserService;
import com.hkgov.csb.eproof.exception.ExceptionConstants;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.hkgov.csb.eproof.config.Constants.SLASH;

@Service
public class MeetingWorkspaceServiceImpl implements MeetingWorkspaceService {
    private final MeetingWorkspaceRepository meetingWorkspaceRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final UserService userService;
    private final AuthenticatedInfoService authenticatedInfoService;
    private final TaskRepository taskRepository;
    private final AgendaItemHasDocumentRepository agendaItemHasDocumentRepository;
    private final MeetingWorkspaceHasPostMeetingDocumentRepository meetingWorkspaceHasPostMeetingDocumentRepository;
    private final MeetingWorkspaceHasPrivateDocumentRepository meetingWorkspaceHasPrivateDocumentRepository;
    private final MeetingWorkspaceRetentionRepository meetingWorkspaceRetentionRepository;
    private final MeetingGroupRepository meetingGroupRepository;
    private final LocationService locationService;
    private final MeetingWorkspaceHasAttendeeRepository meetingWorkspaceHasAttendeeRepository;

    @Value("${search.meeting.url}")
    private String meetingUrl;

    public MeetingWorkspaceServiceImpl(MeetingWorkspaceRepository meetingWorkspaceRepository, AgendaItemRepository agendaItemRepository, UserService userService, AuthenticatedInfoService authenticatedInfoService, TaskRepository taskRepository, AgendaItemHasDocumentRepository agendaItemHasDocumentRepository, MeetingWorkspaceHasPostMeetingDocumentRepository meetingWorkspaceHasPostMeetingDocumentRepository, MeetingWorkspaceHasPrivateDocumentRepository meetingWorkspaceHasPrivateDocumentRepository, MeetingWorkspaceRetentionRepository meetingWorkspaceRetentionRepository, MeetingGroupRepository meetingGroupRepository, LocationService locationService, MeetingWorkspaceHasAttendeeRepository meetingWorkspaceHasAttendeeRepository) {
        this.meetingWorkspaceRepository = meetingWorkspaceRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.userService = userService;
        this.authenticatedInfoService = authenticatedInfoService;
        this.taskRepository = taskRepository;
        this.agendaItemHasDocumentRepository = agendaItemHasDocumentRepository;
        this.meetingWorkspaceHasPostMeetingDocumentRepository = meetingWorkspaceHasPostMeetingDocumentRepository;
        this.meetingWorkspaceHasPrivateDocumentRepository = meetingWorkspaceHasPrivateDocumentRepository;
        this.meetingWorkspaceRetentionRepository = meetingWorkspaceRetentionRepository;
        this.meetingGroupRepository = meetingGroupRepository;
        this.locationService = locationService;
        this.meetingWorkspaceHasAttendeeRepository = meetingWorkspaceHasAttendeeRepository;
    }

    @Override
    public Page<MeetingWorkspace> getAllMeetingWorkspaceWithoutRetention(Pageable pageable) {
        return meetingWorkspaceRepository.findAllWithoutRetention(pageable);
    }

    @Override
    public Page<MeetingWorkspace> getMeetingWorkspaceByUser(Pageable pageable) {
        User currentUser = authenticatedInfoService.getCurrentUser();
        return meetingWorkspaceRepository.findWithoutRetentionByUser(currentUser, pageable);
    }

    @Override
    @Transactional
    public MeetingWorkspace createMeetingWorkspace(MeetingWorkspaceDto request) {
        MeetingWorkspace meetingWorkspace = MeetingWorkspaceMapper.INSTANCE.destinationToSource(request);
        validateDuplicateMeetingTitle(meetingWorkspace);
        validateDuplicateTimeSlot(meetingWorkspace);
        meetingWorkspaceRepository.save(meetingWorkspace);
        locationService.add(request.getLocation());
        return meetingWorkspaceRepository.save(meetingWorkspace);
    }

    private void validateDuplicateTimeSlot(MeetingWorkspace meetingWorkspace) {
        if (meetingWorkspaceRepository.idDuplicateTimeslot(meetingWorkspace.getStartTime(), meetingWorkspace.getEndTime())) {
            throw new GenericException(ExceptionConstants.DUPLICATE_MEETING_TIMESLOT_EXCEPTION_CODE, ExceptionConstants.DUPLICATE_MEETING_TIMESLOT_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicateMeetingTitle(MeetingWorkspace meetingWorkspace) {
        if (meetingWorkspaceRepository.existsByTitle(meetingWorkspace.getTitle())) {
            throw new GenericException(ExceptionConstants.DUPLICATE_MEETING_TITLE_EXCEPTION_CODE, ExceptionConstants.DUPLICATE_MEETING_TITLE_EXCEPTION_MESSAGE);
        }
    }

    @Override
    @Transactional
    public MeetingWorkspace editMeetingWorkspace(MeetingWorkspaceDto request) {
        MeetingWorkspace meetingWorkspace = getMeetingWorkspaceById(request.getMeetingWorkspaceId());
        MeetingWorkspaceMapper.INSTANCE.updateFromDto(request, meetingWorkspace);
        meetingWorkspaceRepository.save(meetingWorkspace);
        return meetingWorkspace;
    }

    @Override
    @Transactional
    public void purgeMeetingWorkspaceByRetention(int day) {
        List<MeetingWorkspaceRetention> meetingWorkspaceRetentions = meetingWorkspaceRetentionRepository.findByRetentionDay(day);
        meetingWorkspaceRetentionRepository.deleteAll(meetingWorkspaceRetentions);
    }

    @Override
    @Transactional
    public MeetingWorkspace addRetention(Long id) {
        MeetingWorkspace meetingWorkspace = getMeetingWorkspaceById(id);
        if (CollectionUtils.isEmpty(meetingWorkspaceRetentionRepository.findByMeetingWorkspace(meetingWorkspace))) {
            MeetingWorkspaceRetention meetingWorkspaceRetention = new MeetingWorkspaceRetention();
            meetingWorkspaceRetention.setMeetingWorkspace(meetingWorkspace);
            meetingWorkspaceRetentionRepository.save(meetingWorkspaceRetention);
        }
        return meetingWorkspace;
    }

    @Override
    @Transactional
    public void retainMeetingWorkspaceByLimit(int limitNo) {
        List<MeetingWorkspace> meetingWorkspaceList = meetingWorkspaceRepository.findMeetingWorkspaceByOffset(limitNo);
        meetingWorkspaceList.forEach(meetingWorkspace -> {
            MeetingWorkspaceRetention meetingWorkspaceRetention = new MeetingWorkspaceRetention();
            meetingWorkspaceRetention.setMeetingWorkspace(meetingWorkspace);
            meetingWorkspaceRetentionRepository.save(meetingWorkspaceRetention);
        });

    }

    @Override
    public List<AgendaItem> getAgendaItemByMeetingWorkspace(Long id) {
        List<AgendaItem> agendaItemList = userService.isMeetingWorkspaceMaintenance() ? agendaItemRepository.findByMeetingWorkspace(id) : agendaItemRepository.findApprovedByMeetingWorkspace(id);
        return userService.isMeetingWorkspaceMaintenance() ? agendaItemRepository.findByMeetingWorkspace(id) : agendaItemList.stream().map(agendaItem -> {
            agendaItem.setAgendaItemHasDocuments(agendaItem.getAgendaItemHasDocuments().stream().filter(document ->
                    document.getStatus().equals(Status.APPROVED)
            ).toList());
            return agendaItem;
        }).toList();
    }

    @Override
    public MeetingWorkspace getMeetingWorkspaceById(Long id) {
        MeetingWorkspace meetingWorkspace = Optional.ofNullable(meetingWorkspaceRepository.findByMeetingWorkspaceId(id))
                .orElseThrow(() -> new GenericException(ExceptionConstants.MEETING_WORKSPACE_NOT_FOUND_EXCEPTION_CODE, ExceptionConstants.MEETING_WORKSPACE_NOT_FOUND_EXCEPTION_MESSAGE));
        meetingWorkspace.setDelete(isMeetingWorkspaceDelete(meetingWorkspace));
        return meetingWorkspace;
    }

    @Override
    public MeetingWorkspace getMeetingWorkspaceByIdAndUser(Long id) {
        User currentUser = authenticatedInfoService.getCurrentUser();
        return Optional.ofNullable(meetingWorkspaceRepository.findByMeetingWorkspaceIdAndUser(id, currentUser))
                .orElseThrow(() -> new GenericException(ExceptionConstants.MEETING_WORKSPACE_NOT_FOUND_EXCEPTION_CODE, ExceptionConstants.MEETING_WORKSPACE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    @Transactional
    public MeetingWorkspace addAttendees(Long id, Set<String> attendeeName) {
        MeetingWorkspace meetingWorkspace = getMeetingWorkspaceById(id);
        int sequence = 1;
        for (String name : attendeeName) {
            Attendee attendee = new Attendee();
            attendee.setName(name);
            attendee.setSequence(sequence);
            meetingWorkspace.addMeetingWorkspaceHasAttendee(attendee);
            sequence++;
        }
        return meetingWorkspace;
    }

    @Override
    @Transactional
    public void removeAllAttendeeByMeetingWorkspace(Long id) {
        getMeetingWorkspaceById(id).getMeetingWorkspaceHasAttendees().clear();
    }

    @Override
    @Transactional
    public MeetingWorkspace removeAttendee(Long id, AttendeeDto dto) {
        MeetingWorkspaceHasAttendee meetingWorkspaceHasAttendee = meetingWorkspaceHasAttendeeRepository.findAttendeeByMeetingWorkspaceAndName(id, dto.getName());
        meetingWorkspaceHasAttendeeRepository.delete(meetingWorkspaceHasAttendee);
        return meetingWorkspaceHasAttendee.getMeetingWorkspace();
    }

    @Override
    public Set<String> getAllAttendee(Long id) {
        return meetingWorkspaceHasAttendeeRepository.findAttendeeOrderBySequence(id);
    }

    @Override
    @Transactional
    public Task addTask(Long id, TaskDto request) {
        Task task = TaskMapper.INSTANCE.destinationToSource(request);
        task.setUser(request.getAssignedTo());
        task.setMeetingWorkspace(getMeetingWorkspaceById(id));
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task editTask(TaskDto request) {
        Task task = taskRepository.findByTaskId(request.getTaskId());
        TaskMapper.INSTANCE.updateFromDto(request, task);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task removeTask(Long taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        taskRepository.delete(task);
        return task;
    }

    @Override
    public List<Task> getTasksByMeetingWorkspace(Long id) {
        return taskRepository.findByMeetingWorkspaceId(id);
    }

    @Override
    public List<MeetingWorkspace> getCalendar(String year, String month) {
        return userService.isMeetingWorkspaceMaintenance() ? meetingWorkspaceRepository.findByYearAndMonth(year, month) : meetingWorkspaceRepository.findByLoginAndYearAndMonth(authenticatedInfoService.getCurrentUser().getLoginId(), year, month);
    }

    @Override
    public List<AgendaItemHasDocument> getAgendaItemHasDocumentsByMeetingWorkspace(Long id) {
        return agendaItemHasDocumentRepository.findByMeetingWorkspaceId(id);
    }

    @Override
    public List<MeetingWorkspaceHasPostMeetingDocument> getMeetingWorkspaceHasPostMeetingDocumentByMeetingWorkspace(Long id) {
        return userService.isMeetingWorkspaceMaintenance() ? meetingWorkspaceHasPostMeetingDocumentRepository.findByMeetingWorkspaceId(id) : meetingWorkspaceHasPostMeetingDocumentRepository.findApprovedByMeetingWorkspaceId(id);
    }

    @Override
    public FolderDto getMeetingWorkspaceHasPrivateMeetingDocumentByMeetingWorkspace(Long id) {
        FolderDto result = new FolderDto();
        List<MeetingWorkspaceHasPrivateDocument> meetingWorkspaceHasPrivateDocuments = meetingWorkspaceHasPrivateDocumentRepository.findByMeetingWorkspaceId(id);
        Map<String, MeetingWorkspaceHasPrivateDocument> meetingWorkspaceHasPrivateDocumentMap = new HashMap<>();
        meetingWorkspaceHasPrivateDocuments.forEach(meetingWorkspaceHasPrivateDocument ->
                meetingWorkspaceHasPrivateDocumentMap.put(meetingWorkspaceHasPrivateDocument.getFile().getPath(), meetingWorkspaceHasPrivateDocument)
        );
        if (userService.isMeetingWorkspaceMaintenance()) {
            List<MeetingGroup> meetingGroupNames = meetingGroupRepository.findAll();
            addMeetingGroupToMap(id, meetingWorkspaceHasPrivateDocumentMap, meetingGroupNames);
            meetingWorkspaceHasPrivateDocumentMap
                    .forEach((key, value) -> {
                        String[] folderNames = splitPath(id, key);
                        ListIterator<String> iterator = Arrays.stream(folderNames).toList().listIterator();
                        result.setName(iterator.next());
                        prepareFolderDto(iterator, result, value, true);
                    });
        } else {
            List<MeetingGroup> meetingGroupNames = meetingWorkspaceRepository.findMeetingGroupNameByMeetingWorkspaceAndUser(authenticatedInfoService.getCurrentUser().getLoginId());
            addMeetingGroupToMap(id, meetingWorkspaceHasPrivateDocumentMap, meetingGroupNames);
            meetingWorkspaceHasPrivateDocumentMap
                    .entrySet()
                    .stream()
                    .filter(entry -> {
                        String[] folderNames = splitPath(id, entry.getKey());
                        return userService.getCurrentUserMeetingGroup()
                                .stream()
                                .map(MeetingGroup::getName)
                                .toList()
                                .contains(folderNames[1]);
                    })
                    .forEach(entry -> {
                        String[] folderNames = splitPath(id, entry.getKey());
                        ListIterator<String> iterator = Arrays.stream(folderNames).toList().listIterator();
                        result.setName(iterator.next());
                        prepareFolderDto(iterator, result, entry.getValue(), true);
                    });
        }
        return result;
    }

    private static void addMeetingGroupToMap(Long id, Map<String, MeetingWorkspaceHasPrivateDocument> meetingWorkspaceHasPrivateDocumentMap, List<MeetingGroup> meetingGroupNames) {
        meetingGroupNames.forEach(group -> {
            MeetingWorkspaceHasPrivateDocument document = new MeetingWorkspaceHasPrivateDocument();
            document.setMeetingGroup(group);
            meetingWorkspaceHasPrivateDocumentMap.put("/Meeting/" + id + "/Private Documents/" + group.getName() + SLASH, document);
        });
    }

    @Override
    @Transactional
    public MeetingWorkspace freezeMeetingWorkspace(Long meetingWorkspaceId) {
        MeetingWorkspace meetingWorkspace = getMeetingWorkspaceById(meetingWorkspaceId);
        meetingWorkspace.setFreeze(true);
        meetingWorkspaceRepository.save(meetingWorkspace);
        return meetingWorkspace;
    }

    @Override
    @Transactional
    public MeetingWorkspace unfreezeMeetingWorkspace(Long meetingWorkspaceId) {
        MeetingWorkspace meetingWorkspace = getMeetingWorkspaceById(meetingWorkspaceId);
        meetingWorkspace.setFreeze(false);
        meetingWorkspaceRepository.save(meetingWorkspace);
        return meetingWorkspace;
    }

    @Override
    public void freezeValidation(Long meetingWorkspaceId) {
        MeetingWorkspace meetingWorkspace = getMeetingWorkspaceById(meetingWorkspaceId);
        if (Boolean.TRUE.equals(meetingWorkspace.getFreeze()))
            throw new GenericException(ExceptionConstants.MEETING_WORKSPACE_FREEZE_EXCEPTION_CODE, ExceptionConstants.MEETING_WORKSPACE_FREEZE_EXCEPTION_MESSAGE);
    }

    @Override
    public SearchData search(String keyword) {
        SearchData searchData = new SearchData();
        searchData.setMeetings(getMeetingSearchData(keyword));
        searchData.setAgendas(getAgendaSearchData(keyword));
        searchData.setTasks(getTaskSearchData(keyword));
        searchData.setDocuments(getDocumentSearchData(keyword));
        return searchData;
    }

    @Override
    public Page<MeetingWorkspaceRetentionData> getMeetingWorkspaceRetentionList(int workspaceRetentionDays, Pageable pageable) {
        return meetingWorkspaceRepository.findAllMeetingWorkspaceRetention(workspaceRetentionDays, pageable);
    }

    private List<MeetingSearchData> getMeetingSearchData(String keyword) {
        List<MeetingWorkspace> meetingWorkspaces = meetingWorkspaceRepository.findByTitleOrLocation(keyword);
        return meetingWorkspaces.stream().map(meetingWorkspace -> {
            MeetingSearchData meetingSearchData = new MeetingSearchData();
            meetingSearchData.setTitle(meetingWorkspace.getTitle());
            meetingSearchData.setLocation(meetingWorkspace.getLocation());
            meetingSearchData.setStartTime(meetingWorkspace.getStartTime());
            meetingSearchData.setEndTime(meetingWorkspace.getEndTime());
            meetingSearchData.setUrl(meetingUrl + meetingWorkspace.getMeetingWorkspaceId());
            return meetingSearchData;
        }).toList();
    }

    private List<AgendaSearchData> getAgendaSearchData(String keyword) {
        List<AgendaItem> agendaItems = userService.isMeetingWorkspaceMaintenance() ? agendaItemRepository.findByTitleOrPresenterOrNoteOrStatus(keyword) : agendaItemRepository.findApprovedByTitleOrPresenterOrNoteOrStatus(keyword);
        return agendaItems.stream().map(agendaItem -> {
            AgendaSearchData agendaSearchData = new AgendaSearchData();
            agendaSearchData.setTitle(agendaItem.getTitle());
            agendaSearchData.setPresenter(agendaItem.getPresenter());
            agendaSearchData.setNote(agendaItem.getNote());
            agendaSearchData.setStatus(agendaItem.getStatus().getLabel());
            agendaSearchData.setUrl(meetingUrl + agendaItem.getMeetingWorkspace().getMeetingWorkspaceId());
            return agendaSearchData;
        }).toList();
    }

    private List<TaskSearchData> getTaskSearchData(String keyword) {
        List<Task> tasks = userService.isMeetingWorkspaceMaintenance() ? taskRepository.findByTitleOrNoteOrAssignee(keyword) : taskRepository.findApprovedByTitleOrNoteOrAssignee(keyword);
        return tasks.stream().map(task -> {
            TaskSearchData taskSearchData = new TaskSearchData();
            taskSearchData.setTitle(task.getTitle());
            taskSearchData.setNote(task.getNote());
            taskSearchData.setAssignee(task.getUser());
            taskSearchData.setUrl(meetingUrl + task.getMeetingWorkspace().getMeetingWorkspaceId());
            return taskSearchData;
        }).toList();
    }

    private List<DocumentSearchData> getDocumentSearchData(String keyword) {
        List<File> fileList = new ArrayList<>();
        fileList.addAll(getAgendaItemFileForSearch(keyword));
        fileList.addAll(getPostMeetingDocumentFileForSearch(keyword));
        fileList.addAll(getPrivateMeetingDocumentFileForSearch(keyword));
        return fileList.stream().map(file -> {
            DocumentSearchData documentSearchData = new DocumentSearchData();
            documentSearchData.setFileName(file.getFileName());
            documentSearchData.setUrl(file.getPath());
            return documentSearchData;
        }).toList();
    }

    private List<File> getAgendaItemFileForSearch(String keyword) {
        return userService.isMeetingWorkspaceMaintenance() ? meetingWorkspaceRepository.findAgendaItemFileByFileName(keyword) : meetingWorkspaceRepository.findApprovedAgendaItemFileByFileName(keyword);
    }

    private List<File> getPostMeetingDocumentFileForSearch(String keyword) {
        return userService.isMeetingWorkspaceMaintenance() ? meetingWorkspaceRepository.findPostMeetingDocumentFileByFileName(keyword) : meetingWorkspaceRepository.findApprovedPostMeetingDocumentFileByFileName(keyword);
    }

    private List<File> getPrivateMeetingDocumentFileForSearch(String keyword) {
        return userService.isMeetingWorkspaceMaintenance() ? meetingWorkspaceRepository.findPrivateMeetingDocumentFileByFileName(keyword) : meetingWorkspaceRepository.findPrivateMeetingDocumentFileByFileNameAndUser(keyword, authenticatedInfoService.getCurrentUser());
    }

    private void prepareFolderDto(ListIterator<String> iterator, FolderDto folder, MeetingWorkspaceHasPrivateDocument document, boolean showDocument) {
        String current = iterator.next();
        if (iterator.hasNext()) {
            prepareFolderDto(iterator, getOrCreateSubFolder(folder, current, document.getMeetingGroup().getCode()), document, showDocument);
        } else {
            if (isFolder(document)) {
                getOrCreateSubFolder(folder, current, document.getMeetingGroup().getCode());
            } else if (showDocument && !isFolder(document)) {
                folder.getDocuments().add(MeetingWorkspaceHasPrivateDocumentMapper.INSTANCE.toDto(document));
            }
        }
    }

    private static boolean isFolder(MeetingWorkspaceHasPrivateDocument document) {
        return ObjectUtils.isEmpty(document.getFile());
    }

    private FolderDto getOrCreateSubFolder(FolderDto folder, String name, String code) {
        return folder.getSubFolders()
                .stream()
                .filter(sub -> sub.getName().equals(name))
                .findFirst()
                .orElseGet(() -> createSubFolder(folder, name, code));
    }

    private boolean isMeetingWorkspaceDelete(MeetingWorkspace meetingWorkspace) {
        return meetingWorkspaceRepository.isMeetingWorkspaceDelete(meetingWorkspace);
    }

    @NotNull
    private static FolderDto createSubFolder(FolderDto folder, String name, String code) {
        FolderDto subFolder = new FolderDto();
        subFolder.setName(name);
        subFolder.setCode(code);
        folder.getSubFolders().add(subFolder);
        return subFolder;
    }

    @NotNull
    private static String[] splitPath(Long id, String path) {
        String prefix = "/Meeting/" + id.toString() + "/";
        return path.substring(prefix.length()).split("/");
    }

}
