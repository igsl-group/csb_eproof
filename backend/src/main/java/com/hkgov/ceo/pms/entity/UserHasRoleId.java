package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserHasRoleId implements Serializable {
    private static final long serialVersionUID = 6729091003980361179L;
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserHasRoleId that = (UserHasRoleId) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
                && getRoleId() != null && Objects.equals(getRoleId(), that.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}