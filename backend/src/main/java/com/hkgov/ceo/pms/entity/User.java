package com.hkgov.ceo.pms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user", indexes = {
        @Index(name = "idx_user_login_id_unq", columnList = "login_id", unique = true),
        @Index(name = "idx_user_email_unq", columnList = "email")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_login_id", columnNames = {"login_id"}),
})
public class User extends BaseEntity implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "post")
    private String post;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Password> passwords = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserHasRole> userHasRoles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserHasMeetingWorkspace> userHasMeetingWorkspaces = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserHasMeetingGroup> userHasMeetingGroups = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @Transient
    private List<Permission> permissions;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "login_attempt")
    private Integer loginAttempt;

    @Column(name = "password_remind")
    private Boolean passwordRemind;

    @PostLoad
    private void postLoad() {
        permissions = userHasRoles
                .stream()
                .map(UserHasRole::getRole)
                .map(Role::getRoleHasPermissions)
                .flatMap(List::stream)
                .map(RoleHasPermission::getPermission)
                .distinct()
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions;
    }

    @Override
    public String getPassword() {
        return passwords
                .stream()
                .max(Comparator.comparing(Password::getCreateDate))
                .map(Password::getPasswordHash)
                .orElse(null);
    }

    /**
     * @return the loginId
     */
    @Override
    public String getUsername() {
        return loginId;
    }

    public void addPassword(String password) {
        Password newPassword = new Password();
        newPassword.setPasswordHash(password);
        newPassword.setUser(this);
        passwords.add(newPassword);
    }

    public void clearUserHasRoles() {
        userHasRoles.clear();
    }

    public void clearUserHasGroups() {
        userHasMeetingGroups.clear();
    }

    public void addUserHasRoles(List<UserHasRole> userHasRoles) {
        this.userHasRoles.addAll(userHasRoles);
    }

    public void addUserHasGroups(List<UserHasMeetingGroup> userHasMeetingGroups) {
        this.userHasMeetingGroups.addAll(userHasMeetingGroups);
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

    public List<UserHasRole> getUserHasRoles() {
        return userHasRoles;
    }

    public void setUserHasRoles(List<UserHasRole> userHasRoles) {
        this.userHasRoles = userHasRoles;
    }

    public List<Password> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<Password> passwords) {
        this.passwords = passwords;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public List<UserHasMeetingWorkspace> getUserHasMeetingWorkspaces() {
        return userHasMeetingWorkspaces;
    }

    public void setUserHasMeetingWorkspaces(List<UserHasMeetingWorkspace> userHasMeetingWorkspaces) {
        this.userHasMeetingWorkspaces = userHasMeetingWorkspaces;
    }

    public List<UserHasMeetingGroup> getUserHasMeetingGroups() {
        return userHasMeetingGroups;
    }

    public void setUserHasMeetingGroups(List<UserHasMeetingGroup> userHasMeetingGroups) {
        this.userHasMeetingGroups = userHasMeetingGroups;
    }

    public Integer getLoginAttempt() {
        return loginAttempt;
    }

    public void setLoginAttempt(Integer loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public Boolean getPasswordRemind() {
        return passwordRemind;
    }

    public void setPasswordRemind(Boolean passwordRemind) {
        this.passwordRemind = passwordRemind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getUserId() != null && Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addPasswords(List<Password> passwords) {
        this.passwords.addAll(passwords);
    }

    public void removePasswords(List<Password> passwordsToBeRemove) {
        this.passwords.removeAll(passwordsToBeRemove);
    }

    public List<PasswordResetToken> getPasswordResetTokens() {
        return passwordResetTokens;
    }

    public void setPasswordResetTokens(List<PasswordResetToken> passwordResetTokens) {
        this.passwordResetTokens = passwordResetTokens;
    }
}