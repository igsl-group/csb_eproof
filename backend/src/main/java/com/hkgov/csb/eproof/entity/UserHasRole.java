package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @TableName user_has_role
 */
@Entity
@Table(name="user_has_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHasRole implements Serializable {
    /**
     * 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     *
     */
    @Column(name = "role_id")
    private Long roleId;

    private static final long serialVersionUID = 1L;
}