package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @TableName user
 */
@Entity
@Table(name = "user")
@Getter
@Setter
public class User extends BaseEntity {
    /**
     * 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 
     */
    @Column(name = "dp_user_id")
    private String dpUserId;

    /**
     * 
     */
    @Column(name = "dp_dept_id")
    private String dpDeptId;

    /**
     * 
     */
    @Column(name = "name")
    private String name;

    /**
     * 
     */
    @Column(name = "post")
    private String post;

    /**
     * 
     */
    @Column(name = "email")
    private String email;

    /**
     * 
     */
    @Column(name = "status")
    private String status;

    /**
     * 
     */
    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Transient
    private List<Role> roles;
}