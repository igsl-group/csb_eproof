package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @TableName role_has_permission
 */
@Entity
@Table(name="role_has_permission")
@Getter
@Setter
@NoArgsConstructor
public class RoleHasPermission implements Serializable {
    /**
     * 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 
     */

    @Column(name = "role_id")
    private Long roleId;


    /**
     * 
     */
    @Column(name = "permission_id")
    private Long permissionId;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private Permission permission;

    public RoleHasPermission(Long id, Long roleId, Long permissionId) {
        this.id = id;
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}