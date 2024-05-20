package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @TableName permission
 */
@Entity
@Table(name="permission")
@Getter
@Setter
public class Permission implements Serializable {
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
    @Column(name = "permission_name")
    private String name;

    /**
     * 
     */
    @Column(name = "permission_key")
    private String key;

    private static final long serialVersionUID = 1L;

}