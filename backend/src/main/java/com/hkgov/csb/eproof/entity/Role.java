package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
 * @TableName role
 */
@Getter
@Setter
@Entity
@Table(name="role")
public class Role extends BaseEntity {
    /**
     * 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 
     */
    @Column(name = "name")
    private String name;

    /**
     * 
     */
    @Column(name = "description")
    private String description;

    @Transient
    private List<Permission> permissions;
}