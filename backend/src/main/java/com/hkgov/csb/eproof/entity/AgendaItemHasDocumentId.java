package com.hkgov.csb.eproof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AgendaItemHasDocumentId implements Serializable {
    @Column(name = "agenda_item_id")
    private Long agendaItemId;

    @Column(name = "file_id")
    private Long fileId;

    public Long getAgendaItemId() {
        return agendaItemId;
    }

    public void setAgendaItemId(Long agendaItemId) {
        this.agendaItemId = agendaItemId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AgendaItemHasDocumentId that = (AgendaItemHasDocumentId) o;
        return getAgendaItemId() != null && Objects.equals(getAgendaItemId(), that.getAgendaItemId())
                && getFileId() != null && Objects.equals(getFileId(), that.getFileId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(agendaItemId, fileId);
    }
}