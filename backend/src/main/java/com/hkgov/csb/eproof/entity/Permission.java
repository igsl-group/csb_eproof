package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * 
 * @TableName permission
 */
@Entity
@Table(name="permission")
@Getter
@Setter
public class Permission implements Serializable, GrantedAuthority {
    /**
     * 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 
     */
    @Column(name = "permission_code")
    private String code;

    /**
     * 
     */
    @Column(name = "permission_desc")
    private String description;

    private static final long serialVersionUID = 1L;

    @Override
    public String getAuthority() {
        return this.code;
    }
}