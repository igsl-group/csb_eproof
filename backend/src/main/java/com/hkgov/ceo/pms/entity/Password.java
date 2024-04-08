package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "password")
public class Password extends BaseEntity {
    @Id
    @Column(name = "password_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passwordId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_Id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "password_hash")
    private String passwordHash;

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getPasswordId() {
        return passwordId;
    }
}
