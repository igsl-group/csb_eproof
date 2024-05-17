package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @TableName user
 */
@Entity
@Table(name = "user")
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {
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

    // 用户拥有的角色
    @ManyToMany
    @JoinTable(
            name = "user_has_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return dpUserId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}