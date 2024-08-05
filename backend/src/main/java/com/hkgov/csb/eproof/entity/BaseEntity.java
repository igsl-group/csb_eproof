package com.hkgov.csb.eproof.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_PATTERN;
import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime modifiedDate;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
}
