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
@AllArgsConstructor
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

    private static final long serialVersionUID = 1L;

}