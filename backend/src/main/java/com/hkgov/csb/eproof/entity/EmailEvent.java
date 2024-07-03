package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_event")
@Getter
@Setter
public class EmailEvent extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_message_id")
    private Long emailMessageId;

    @Column(name = "status")
    private String status;

    @Column(name = "schedule_datetime")
    private LocalDateTime scheduleDatetime;


    // Mapped tables
    @OneToOne
    @JoinColumn(name = "email_message_id", insertable = false, updatable = false)
    private EmailMessage emailMessage;

}
