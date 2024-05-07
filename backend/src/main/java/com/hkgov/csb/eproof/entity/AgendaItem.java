package com.hkgov.csb.eproof.entity;

import com.hkgov.csb.eproof.entity.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agenda_item")
public class AgendaItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agenda_item_id", nullable = false)
    private Long agendaItemId;

    @Column(name = "title")
    private String title;

    @Column(name = "presenter")
    private String presenter;

    @Column(name = "note")
    private String note;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "prepared_by", referencedColumnName = "user_id")
    private User preparedBy;

    @ManyToOne
    @JoinColumn(name = "approved_by", referencedColumnName = "user_id")
    private User approvedBy;

    @ManyToOne
    @JoinColumn(name = "rejected_by", referencedColumnName = "user_id")
    private User rejectedBy;

    @ManyToOne
    @JoinColumn(name = "meeting_workspace_id", referencedColumnName = "meeting_workspace_id")
    private MeetingWorkspace meetingWorkspace;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "agendaItem")
    private List<AgendaItemHasDocument> agendaItemHasDocuments = new ArrayList<>();

    public void addAgendaItemHasDocuments(File file, User preparedBy) {
        AgendaItemHasDocumentId agendaItemHasDocumentId = new AgendaItemHasDocumentId();
        agendaItemHasDocumentId.setAgendaItemId(this.agendaItemId);
        agendaItemHasDocumentId.setFileId(file.getFileId());
        AgendaItemHasDocument agendaItemHasDocument = new AgendaItemHasDocument();
        agendaItemHasDocument.setId(agendaItemHasDocumentId);
        agendaItemHasDocument.setAgendaItem(this);
        agendaItemHasDocument.setFile(file);
        agendaItemHasDocument.setPreparedBy(preparedBy);
        agendaItemHasDocument.setStatus(Status.PENDING);
        addAgendaItemHasDocument(agendaItemHasDocument);
    }

    public void addAgendaItemHasDocument(AgendaItemHasDocument agendaItemHasDocument) {
        if (!agendaItemHasDocuments.contains(agendaItemHasDocument)) {
            agendaItemHasDocuments.add(agendaItemHasDocument);
        }
    }

    public List<AgendaItemHasDocument> getAgendaItemHasDocuments() {
        return agendaItemHasDocuments;
    }

    public void setAgendaItemHasDocuments(List<AgendaItemHasDocument> agendaItemHasDocuments) {
        this.agendaItemHasDocuments = agendaItemHasDocuments;
    }

    public MeetingWorkspace getMeetingWorkspace() {
        return meetingWorkspace;
    }

    public void setMeetingWorkspace(MeetingWorkspace meetingWorkspace) {
        this.meetingWorkspace = meetingWorkspace;
    }

    public Long getAgendaItemId() {
        return agendaItemId;
    }

    public void setAgendaItemId(Long agendaItemId) {
        this.agendaItemId = agendaItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public User getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(User preparedBy) {
        this.preparedBy = preparedBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(User rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}