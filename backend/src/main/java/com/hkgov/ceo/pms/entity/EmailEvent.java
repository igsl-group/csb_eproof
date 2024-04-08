package com.hkgov.ceo.pms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_event")
public class EmailEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_event_id")
    private Long emailEventId;

    @Column(name = "status")
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_message")
    private EmailMessage emailMessage;

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEmailEventId() {
        return emailEventId;
    }

    public void setEmailEventId(Long emailEventId) {
        this.emailEventId = emailEventId;
    }

}