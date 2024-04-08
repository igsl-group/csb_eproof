package com.hkgov.ceo.pms.entity;

import com.hkgov.ceo.pms.entity.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meeting_workspace")
public class MeetingWorkspace extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_workspace_id")
    private Long meetingWorkspaceId;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_freeze")
    private Boolean isFreeze;

    @Transient
    private Boolean isDelete;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace")
    private List<UserHasMeetingWorkspace> userHasMeetingWorkspaces = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace")
    private List<AgendaItem> agendaItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace")
    private List<MeetingWorkspaceHasPostMeetingDocument> meetingWorkspaceHasPostMeetingDocuments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace")
    private List<MeetingWorkspaceHasPrivateDocument> meetingWorkspaceHasPrivateDocuments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace")
    private List<Task> tasks = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace")
    private MeetingWorkspaceRetention meetingWorkspaceRetention;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingWorkspace", orphanRemoval = true)
    private List<MeetingWorkspaceHasAttendee> meetingWorkspaceHasAttendees = new ArrayList<>();

    public void addMeetingWorkspaceHasPostMeetingDocuments(File file, User preparedBy) {
        MeetingWorkspaceHasPostMeetingDocumentId meetingWorkspaceHasPostMeetingDocumentId = new MeetingWorkspaceHasPostMeetingDocumentId();
        meetingWorkspaceHasPostMeetingDocumentId.setMeetingWorkspaceId(this.meetingWorkspaceId);
        meetingWorkspaceHasPostMeetingDocumentId.setFileId(file.getFileId());
        MeetingWorkspaceHasPostMeetingDocument meetingWorkspaceHasPostMeetingDocument = new MeetingWorkspaceHasPostMeetingDocument();
        meetingWorkspaceHasPostMeetingDocument.setId(meetingWorkspaceHasPostMeetingDocumentId);
        meetingWorkspaceHasPostMeetingDocument.setMeetingWorkspace(this);
        meetingWorkspaceHasPostMeetingDocument.setFile(file);
        meetingWorkspaceHasPostMeetingDocument.setPreparedBy(preparedBy);
        meetingWorkspaceHasPostMeetingDocument.setStatus(Status.PENDING);
        addMeetingWorkspaceHasPostMeetingDocument(meetingWorkspaceHasPostMeetingDocument);
    }

    public void addMeetingWorkspaceHasPostMeetingDocument(MeetingWorkspaceHasPostMeetingDocument meetingWorkspaceHasPostMeetingDocument) {
        if (!meetingWorkspaceHasPostMeetingDocuments.contains(meetingWorkspaceHasPostMeetingDocument)) {
            meetingWorkspaceHasPostMeetingDocuments.add(meetingWorkspaceHasPostMeetingDocument);
        }
    }

    public void addMeetingWorkspaceHasPrivateDocument(File file, MeetingGroup meetingGroup) {
        MeetingWorkspaceHasPrivateDocumentId meetingWorkspaceHasPrivateDocumentId = new MeetingWorkspaceHasPrivateDocumentId();
        meetingWorkspaceHasPrivateDocumentId.setMeetingWorkspaceId(this.meetingWorkspaceId);
        meetingWorkspaceHasPrivateDocumentId.setFileId(file.getFileId());
        MeetingWorkspaceHasPrivateDocument meetingWorkspaceHasPrivateDocument = new MeetingWorkspaceHasPrivateDocument();
        meetingWorkspaceHasPrivateDocument.setId(meetingWorkspaceHasPrivateDocumentId);
        meetingWorkspaceHasPrivateDocument.setMeetingWorkspace(this);
        meetingWorkspaceHasPrivateDocument.setFile(file);
        meetingWorkspaceHasPrivateDocument.setMeetingGroup(meetingGroup);
        addMeetingWorkspaceHasPrivateDocument(meetingWorkspaceHasPrivateDocument);
    }

    public void addMeetingWorkspaceHasPrivateDocument(MeetingWorkspaceHasPrivateDocument meetingWorkspaceHasPrivateDocument) {
        if (!meetingWorkspaceHasPrivateDocuments.contains(meetingWorkspaceHasPrivateDocument)) {
            meetingWorkspaceHasPrivateDocuments.add(meetingWorkspaceHasPrivateDocument);
        }
    }

    public List<MeetingWorkspaceHasPostMeetingDocument> getMeetingWorkspaceHasPostMeetingDocuments() {
        return meetingWorkspaceHasPostMeetingDocuments;
    }

    public void setMeetingWorkspaceHasPostMeetingDocuments(List<MeetingWorkspaceHasPostMeetingDocument> meetingWorkspaceHasPostMeetingDocuments) {
        this.meetingWorkspaceHasPostMeetingDocuments = meetingWorkspaceHasPostMeetingDocuments;
    }

    public List<AgendaItem> getAgendaItems() {
        return agendaItems;
    }

    public void setAgendaItems(List<AgendaItem> agendaItems) {
        this.agendaItems = agendaItems;
    }

    public void addUserHasMeetingWorkspace(UserHasMeetingWorkspace userHasMeetingWorkspace) {
        if (!userHasMeetingWorkspaces.contains(userHasMeetingWorkspace)) {
            userHasMeetingWorkspaces.add(userHasMeetingWorkspace);
        }
    }

    public UserHasMeetingWorkspace createUserHasMeetingWorkspace(User user) {
        UserHasMeetingWorkspaceId userHasMeetingWorkspaceId = new UserHasMeetingWorkspaceId();
        userHasMeetingWorkspaceId.setUserId(user.getUserId());
        userHasMeetingWorkspaceId.setMeetingWorkspaceId(this.meetingWorkspaceId);
        UserHasMeetingWorkspace userHasMeetingWorkspace = new UserHasMeetingWorkspace();
        userHasMeetingWorkspace.setId(userHasMeetingWorkspaceId);
        userHasMeetingWorkspace.setMeetingWorkspace(this);
        userHasMeetingWorkspace.setUser(user);
        return userHasMeetingWorkspace;
    }

    public void addMeetingWorkspaceHasAttendee(Attendee attendee) {
        MeetingWorkspaceHasAttendeeId meetingWorkspaceHasAttendeeId = new MeetingWorkspaceHasAttendeeId();
        meetingWorkspaceHasAttendeeId.setAttendeeId(attendee.getAttendeeId());
        meetingWorkspaceHasAttendeeId.setMeetingWorkspaceId(this.meetingWorkspaceId);
        MeetingWorkspaceHasAttendee meetingWorkspaceHasAttendee = new MeetingWorkspaceHasAttendee();
        meetingWorkspaceHasAttendee.setId(meetingWorkspaceHasAttendeeId);
        meetingWorkspaceHasAttendee.setMeetingWorkspace(this);
        meetingWorkspaceHasAttendee.setAttendee(attendee);
        addMeetingWorkspaceHasAttendee(meetingWorkspaceHasAttendee);
    }

    public void addMeetingWorkspaceHasAttendee(MeetingWorkspaceHasAttendee meetingWorkspaceHasAttendee) {
        if (!meetingWorkspaceHasAttendees.contains(meetingWorkspaceHasAttendee)) {
            meetingWorkspaceHasAttendees.add(meetingWorkspaceHasAttendee);
        }
    }

    public Long getMeetingWorkspaceId() {
        return meetingWorkspaceId;
    }

    public void setMeetingWorkspaceId(Long meetingWorkspaceId) {
        this.meetingWorkspaceId = meetingWorkspaceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getFreeze() {
        return isFreeze;
    }

    public void setFreeze(Boolean freeze) {
        isFreeze = freeze;
    }

    public List<UserHasMeetingWorkspace> getUserHasMeetingWorkspaces() {
        return userHasMeetingWorkspaces;
    }

    public void setUserHasMeetingWorkspaces(List<UserHasMeetingWorkspace> userHasMeetingWorkspaces) {
        this.userHasMeetingWorkspaces = userHasMeetingWorkspaces;
    }

    public MeetingWorkspaceRetention getMeetingWorkspaceRetention() {
        return meetingWorkspaceRetention;
    }

    public void setMeetingWorkspaceRetention(MeetingWorkspaceRetention meetingWorkspaceRetention) {
        this.meetingWorkspaceRetention = meetingWorkspaceRetention;
    }

    public List<MeetingWorkspaceHasPrivateDocument> getMeetingWorkspaceHasPrivateDocuments() {
        return meetingWorkspaceHasPrivateDocuments;
    }

    public void setMeetingWorkspaceHasPrivateDocuments(List<MeetingWorkspaceHasPrivateDocument> meetingWorkspaceHasPrivateDocuments) {
        this.meetingWorkspaceHasPrivateDocuments = meetingWorkspaceHasPrivateDocuments;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<MeetingWorkspaceHasAttendee> getMeetingWorkspaceHasAttendees() {
        return meetingWorkspaceHasAttendees;
    }

    public void setMeetingWorkspaceHasAttendees(List<MeetingWorkspaceHasAttendee> meetingWorkspaceHasAttendees) {
        this.meetingWorkspaceHasAttendees = meetingWorkspaceHasAttendees;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}