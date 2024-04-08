package com.hkgov.ceo.pms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role", indexes = {
        @Index(name = "idx_role_code_unq", columnList = "code", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_role_code", columnNames = {"code"})
})
public class Role extends BaseEntity {
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", orphanRemoval = true)
    private List<UserHasRole> userHasRoles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", orphanRemoval = true)
    private List<RoleHasPermission> roleHasPermissions = new ArrayList<>();

    public void addRoleHasPermission(Permission permission) {
        RoleHasPermissionId roleHasPermissionId = new RoleHasPermissionId();
        roleHasPermissionId.setRoleId(this.roleId);
        roleHasPermissionId.setPermissionId(permission.getId());
        RoleHasPermission roleHasPermission = new RoleHasPermission();
        roleHasPermission.setId(roleHasPermissionId);
        roleHasPermission.setRole(this);
        roleHasPermission.setPermission(permission);
        addRoleHasPermission(roleHasPermission);
    }

    public void addUserHasRole(User user) {
        addUserHasRole(createUserHasRole(user));
    }

    public UserHasRole createUserHasRole(User user) {
        UserHasRoleId userHasRoleId = new UserHasRoleId();
        userHasRoleId.setRoleId(this.roleId);
        userHasRoleId.setUserId(user.getUserId());
        UserHasRole userHasRole = new UserHasRole();
        userHasRole.setId(userHasRoleId);
        userHasRole.setUser(user);
        userHasRole.setRole(this);
        return userHasRole;
    }

    public void addUserHasRole(UserHasRole userHasRole) {
        if (!userHasRoles.contains(userHasRole)) {
            userHasRoles.add(userHasRole);
        }
    }

    public void addRoleHasPermission(RoleHasPermission roleHasPermission) {
        if (!roleHasPermissions.contains(roleHasPermission)) {
            roleHasPermissions.add(roleHasPermission);
        }
    }

    public List<RoleHasPermission> getRoleHasPermissions() {
        return roleHasPermissions;
    }

    public void setRoleHasPermissions(List<RoleHasPermission> roleHasPermissions) {
        this.roleHasPermissions = roleHasPermissions;
    }

    public List<UserHasRole> getUserHasRoles() {
        return userHasRoles;
    }

    public void setUserHasRoles(List<UserHasRole> userHasRoles) {
        this.userHasRoles = userHasRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
