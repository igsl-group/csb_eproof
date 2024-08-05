package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @TableName user_session
 */
@Entity
@Table(name="user_session")
@Getter
@Setter
public class UserSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id")
    private Long userId;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",insertable = false, updatable = false)
    private User user;

    @Column(name = "jwt")
    private String jwt;

    @Column(name = "client_ip_address")
    private String clientIpAddress;

    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    private static final long serialVersionUID = 1L;

}