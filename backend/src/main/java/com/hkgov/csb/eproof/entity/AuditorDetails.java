package com.hkgov.csb.eproof.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.io.Serializable;

@Entity
@Table(name = "auditor_details", indexes = {
        @Index(name = "idx_auditordetails_unq", columnList = "userId, userName, post, hostname", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_auditordetails", columnNames = {"userId", "userName", "post", "hostname"})
})
public class AuditorDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditorDetailsId;
    private String userId;
    private String userName;
    private String post;
    private String hostname;

    public Long getAuditorDetailsId() {
        return auditorDetailsId;
    }

    public void setAuditorDetailsId(Long auditorDetails) {
        this.auditorDetailsId = auditorDetails;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}