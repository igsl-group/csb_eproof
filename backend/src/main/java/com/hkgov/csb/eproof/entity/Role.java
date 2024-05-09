package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * 
 * @TableName role
 */
@Data
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

    @ManyToMany
    @JoinTable(
            name = "role_has_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}