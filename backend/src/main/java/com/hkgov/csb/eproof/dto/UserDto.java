package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.csb.eproof.converter.DepartmentDtoConverter;
import com.hkgov.csb.eproof.entity.Views;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import java.time.LocalDateTime;
import java.util.List;

import static com.hkgov.csb.eproof.config.Constants.DATE_TIME_PATTERN;

@JsonView(Views.Public.class)
public class UserDto {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CsvBindByName(column = "Email")
    private String email;

    @CsvBindByName(column = "User Name", required = true)
    private String loginId;

    @CsvBindByName(column = "Full Name", required = true)
    private String name;

    @CsvBindByName(column = "Phone No.")
    private String phoneNumber;

    private String status;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime lastLoginDate;

    @CsvBindByName(column = "Post Title")
    private String post;

    private List<UserHasRoleDto> userHasRoles;

    private List<UserHasMeetingGroupDto> userHasMeetingGroups;
    private String oldPassword;

    @JsonView(Views.Internal.class)
    @CsvBindByName(column = "Password")
    private String newPassword;

    @CsvCustomBindByName(column = "Office", converter = DepartmentDtoConverter.class)
    private DepartmentDto department;
    private Boolean resetPassword;
    private Boolean passwordRemind;


    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

    private List<PermissionDto> permissions;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<UserHasRoleDto> getUserHasRoles() {
        return userHasRoles;
    }

    public void setUserHasRoles(List<UserHasRoleDto> userHasRoles) {
        this.userHasRoles = userHasRoles;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public List<UserHasMeetingGroupDto> getUserHasMeetingGroups() {
        return userHasMeetingGroups;
    }

    public void setUserHasMeetingGroups(List<UserHasMeetingGroupDto> userHasMeetingGroups) {
        this.userHasMeetingGroups = userHasMeetingGroups;
    }

    public Boolean isResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(Boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public Boolean getPasswordRemind() {
        return passwordRemind;
    }

    public void setPasswordRemind(Boolean passwordRemind) {
        this.passwordRemind = passwordRemind;
    }

    public Boolean getResetPassword() {
        return resetPassword;
    }
}