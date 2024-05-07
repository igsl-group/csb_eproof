package com.hkgov.csb.eproof.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    @CreationTimestamp
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "auditor_details_modified_by")
    private AuditorDetails modifiedBy;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "auditor_details_created_by")
    private AuditorDetails createdBy;

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public AuditorDetails getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(AuditorDetails modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public AuditorDetails getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AuditorDetails createdBy) {
        this.createdBy = createdBy;
    }
}
