package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "attendee")
public class Attendee extends BaseEntity {
    @Id
    @Column(name = "attendee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendeeId;

    @Column(name = "name")
    private String name;

    @Column(name = "sequence")
    private Integer sequence;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attendee that = (Attendee) o;
        return getAttendeeId() != null && Objects.equals(getAttendeeId(), that.getAttendeeId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}