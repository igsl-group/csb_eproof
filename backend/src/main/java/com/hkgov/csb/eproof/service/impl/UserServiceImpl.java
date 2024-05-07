package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.PasswordResetTokenRepository;
import com.hkgov.csb.eproof.dao.UserHasGroupRepository;
import com.hkgov.csb.eproof.dao.UserHasRoleRepository;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.dto.PasswordDto;
import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.dto.UserHasMeetingGroupDto;
import com.hkgov.csb.eproof.dto.UserHasRoleDto;
import com.hkgov.csb.eproof.entity.Department;
import com.hkgov.csb.eproof.entity.EmailContext;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.entity.MeetingGroup;
import com.hkgov.csb.eproof.entity.Password;
import com.hkgov.csb.eproof.entity.PasswordResetToken;
import com.hkgov.csb.eproof.entity.Role;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserHasMeetingGroup;
import com.hkgov.csb.eproof.entity.UserHasRole;
import com.hkgov.csb.eproof.event.EmailEventPublisher;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.UserMapper;
import com.hkgov.csb.eproof.service.AuthenticatedInfoService;
import com.hkgov.csb.eproof.service.ConfigurationService;
import com.hkgov.csb.eproof.service.DepartmentService;
import com.hkgov.csb.eproof.service.EmailService;
import com.hkgov.csb.eproof.service.MeetingGroupService;
import com.hkgov.csb.eproof.service.RoleService;
import com.hkgov.csb.eproof.service.UserService;
import com.hkgov.csb.eproof.exception.ExceptionConstants;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.hkgov.csb.eproof.config.Constants.ACTIVE;
import static com.hkgov.csb.eproof.config.Constants.BLOCKED;
import static com.hkgov.csb.eproof.config.Constants.DATE_PATTERN;
import static java.util.Objects.isNull;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentService departmentService;
    private final UserHasRoleRepository userHasRoleRepository;
    private final EmailService emailService;
    private final EmailEventPublisher emailEventPublisher;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticatedInfoService authenticatedInfoService;
    private final MeetingGroupService meetingGroupService;
    private final UserHasGroupRepository userHasGroupRepository;
    private final ConfigurationService configurationService;

    @Value("${password.tokenExpirationAfterDays}")
    private long passwordTokenExpiryDate;

    @Value("${server.url}")
    private String serverUrl;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, DepartmentService departmentService, UserHasRoleRepository userHasRoleRepository, EmailService emailService, EmailEventPublisher emailEventPublisher, PasswordResetTokenRepository passwordResetTokenRepository, AuthenticatedInfoService authenticatedInfoService, MeetingGroupService meetingGroupService, UserHasGroupRepository userHasGroupRepository, ConfigurationService configurationService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.departmentService = departmentService;
        this.userHasRoleRepository = userHasRoleRepository;
        this.emailService = emailService;
        this.emailEventPublisher = emailEventPublisher;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.authenticatedInfoService = authenticatedInfoService;
        this.meetingGroupService = meetingGroupService;
        this.userHasGroupRepository = userHasGroupRepository;
        this.configurationService = configurationService;
    }

    @Override
    public Page<User> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> GenericException.Builder
                        .create()
                        .setCode(ExceptionConstants.USER_ID_NOT_FOUND_EXCEPTION_CODE)
                        .setMessage(ExceptionConstants.USER_ID_NOT_FOUND_EXCEPTION_MESSAGE)
                        .setValue(loginId)
                        .build());
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> GenericException.Builder
                        .create()
                        .setCode(ExceptionConstants.USER_EMAIL_NOT_FOUND_EXCEPTION_CODE)
                        .setMessage(ExceptionConstants.USER_EMAIL_NOT_FOUND_EXCEPTION_MESSAGE)
                        .setValue(email)
                        .build());
    }

    @Override
    public User createUser(UserDto request) {
        validateLoginIdDuplication(request);
        validateEmailDuplication(request);
        User user = UserMapper.INSTANCE.destinationToSource(request);
        setDepartment(request, user);
        if (request.getStatus() == null) {
            user.setStatus(ACTIVE);
        }
        userRepository.save(user);
        saveUserHasRole(request.getUserHasRoles(), user);
        saveUserHasGroup(request.getUserHasMeetingGroups(), user);
        if (StringUtils.isNotBlank(request.getNewPassword())) {
            savePassword(request.getNewPassword(), user);
        }
        if (BooleanUtils.isTrue(request.isResetPassword())) {
            resetPasswordByEmail(user);
        }
        return user;
    }

    private void validateEmailDuplication(UserDto request) {
        if (StringUtils.isNotEmpty(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new GenericException(ExceptionConstants.DUPLICATE_EMAIL_EXCEPTION_CODE, ExceptionConstants.DUPLICATE_EMAIL_EXCEPTION_MESSAGE);
        }
    }

    private void validateEmailDuplication(UserDto request, Long userId) {
        if (StringUtils.isNotEmpty(request.getEmail()) && userRepository.existsByEmailAndUserId(request.getEmail(), userId)) {
            throw new GenericException(ExceptionConstants.DUPLICATE_EMAIL_EXCEPTION_CODE, ExceptionConstants.DUPLICATE_EMAIL_EXCEPTION_MESSAGE);
        }
    }

    private void validateLoginIdDuplication(UserDto request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new GenericException(ExceptionConstants.DUPLICATE_LOGIN_ID_EXCEPTION_CODE, ExceptionConstants.DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<User> createUsers(List<UserDto> requests) {
        return requests
                .stream()
                .map(this::createUser)
                .toList();
    }

    @Override
    public User updateUser(UserDto request) {
        User user = getUserByLoginId(request.getLoginId());
        validateEmailDuplication(request, user.getUserId());
        if (isCurrentUser(user) && !ACTIVE.equals(request.getStatus())) {
            throw new GenericException(ExceptionConstants.USER_CANNOT_EDIT_SELF_STATUS_EXCEPTION_CODE, ExceptionConstants.USER_CANNOT_EDIT_SELF_STATUS_EXCEPTION_MESSAGE);
        }
        if (BLOCKED.equals(user.getStatus()) && ACTIVE.equals(request.getStatus())) {
            user.setLoginAttempt(0);
        }
        setDepartment(request, user);
        UserMapper.INSTANCE.updateFromDto(request, user);
        userRepository.save(user);
        updateUserHasRole(request.getUserHasRoles(), user);
        updateUserHasGroup(request.getUserHasMeetingGroups(), user);
        if (StringUtils.isNotBlank(request.getNewPassword())) {
            savePassword(request.getNewPassword(), user);
        }
        if (BooleanUtils.isTrue(request.isResetPassword())) {
            resetPasswordByEmail(user);
        }
        return user;
    }

    @Override
    public User removeUser(String loginId) {
        User user = getUserByLoginId(loginId);
        if (Objects.equals(authenticatedInfoService.getCurrentUser(), user)) {
            throw new GenericException(ExceptionConstants.USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE, ExceptionConstants.USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE);
        }
        if (ObjectUtils.isNotEmpty(user)) {
            userRepository.delete(user);
        }
        return user;
    }

    @Override
    public void savePassword(String passwordPlainText, User user) {
        user.addPassword(passwordEncoder.encode(passwordPlainText));
        user.setPasswordRemind(false);
        userRepository.save(user);
        passwordRetention(user);
    }

    private void passwordRetention(User user) {
        int maxPasswordRecordNo = configurationService.getMaxPasswordRecordNo();
        if (user.getPasswords().size() >= maxPasswordRecordNo) {
            List<Password> passwordsToBeRemove = user.getPasswords()
                    .stream()
                    .sorted(Comparator.comparing(Password::getCreateDate).reversed())
                    .skip(maxPasswordRecordNo - 1)
                    .toList();
            user.removePasswords(passwordsToBeRemove);
            userRepository.save(user);
        }
    }

    private void validateOldPassword(String oldPassword, User user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new GenericException(ExceptionConstants.OLD_PASSWORD_NOT_VALID_EXCEPTION_CODE, ExceptionConstants.OLD_PASSWORD_NOT_VALID_EXCEPTION_MESSAGE);
        }
    }

    private void saveUserHasRole(List<UserHasRoleDto> request, User user) {
        if (ObjectUtils.isNotEmpty(request)) {
            request
                    .stream()
                    .map(UserHasRoleDto::getRole)
                    .forEach(roleDto -> {
                        Role role = roleService.getRoleByCode(roleDto.getCode());
                        role.addUserHasRole(user);
                    });
        }
    }

    private void saveUserHasGroup(List<UserHasMeetingGroupDto> request, User user) {
        if (ObjectUtils.isNotEmpty(request)) {
            request
                    .stream()
                    .map(UserHasMeetingGroupDto::getMeetingGroup)
                    .forEach(groupDto -> {
                        MeetingGroup group = meetingGroupService.getMeetingGroupByCode(groupDto.getCode());
                        group.addUserHasGroup(user);
                    });
        }
    }

    @Override
    public void updateUserHasRole(List<UserHasRoleDto> request, User user) {
        if (isCurrentUser(user)) {
            return;
        }
        if (ObjectUtils.isNotEmpty(request)) {
            user.clearUserHasRoles();
            user.addUserHasRoles(request
                    .stream()
                    .map(UserHasRoleDto::getRole)
                    .map(roleDto -> {
                        Role role = roleService.getRoleByCode(roleDto.getCode());
                        UserHasRole userHasRole = userHasRoleRepository.findByUserAndRole(user, role);
                        if (ObjectUtils.isEmpty(userHasRole)) {
                            return role.createUserHasRole(user);
                        } else {
                            return userHasRole;
                        }
                    }).toList());
        } else {
            user.clearUserHasRoles();
        }
    }

    private boolean isCurrentUser(User user) {
        return Objects.equals(authenticatedInfoService.getCurrentUser(), user);
    }

    @Override
    public void updateUserHasGroup(List<UserHasMeetingGroupDto> request, User user) {
        if (ObjectUtils.isNotEmpty(request)) {
            user.clearUserHasGroups();
            user.addUserHasGroups(request
                    .stream()
                    .map(UserHasMeetingGroupDto::getMeetingGroup)
                    .map(groupDto -> {
                        MeetingGroup group = meetingGroupService.getMeetingGroupByCode(groupDto.getCode());
                        UserHasMeetingGroup userHasMeetingGroup = userHasGroupRepository.findByUserAndGroup(user, group);
                        if (ObjectUtils.isEmpty(userHasMeetingGroup)) {
                            return group.createUserHasGroup(user);
                        } else {
                            return userHasMeetingGroup;
                        }
                    }).toList());
        } else {
            user.clearUserHasGroups();
        }
    }

    @Override
    public User updateSelfProfile(UserDto request) {
        User user = getUserByLoginId(authenticatedInfoService.getCurrentUser().getLoginId());
        UserMapper.INSTANCE.updateFromDto(request, user);
        if (StringUtils.isNotBlank(request.getNewPassword())) {
            validateOldPassword(request.getOldPassword(), user);
            validatePasswordHistory(user, request.getNewPassword());
            savePassword(request.getNewPassword(), user);
        }
        return userRepository.save(user);
    }

    @Override
    public User forgotPassword(String email) {
        User user = getUserByEmail(email);
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        sendPasswordResetEmail(token, user);
        return user;
    }

    //User Profile Reset Password By Email
    @Override
    public void resetPasswordByEmail(User user) {
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        sendPasswordResetEmail(token, user);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDate.now().plusDays(passwordTokenExpiryDate));
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public void sendPasswordResetEmail(String token, User user) {
        EmailContext emailContext = new EmailContext();
        emailContext.setServerUrl(serverUrl);
        emailContext.setToken(token);
        String contextString = emailService.convertContextToJson(emailContext);
        EmailEvent emailEvent = emailService.createEmailEvent(user.getEmail(), null, "PasswordReset", contextString);
        emailEventPublisher.publicEmailEvent(emailEvent);
    }

    @Override
    public void sendPasswordExpireEmail() {
        int passwordExpiryReminderDays = configurationService.getPasswordExpiryReminderDays();
        if (passwordExpiryReminderDays > 0) {
            List<User> passwordExpireUser = userRepository.findPasswordExpireUser(passwordExpiryReminderDays);
            passwordExpireUser.forEach(user -> {
                EmailContext emailContext = new EmailContext();
                emailContext.setPasswordExpirationDays(LocalDate.now().plusDays(passwordExpiryReminderDays).format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                String contextString = emailService.convertContextToJson(emailContext);
                EmailEvent emailEvent = emailService.createEmailEvent(user.getEmail(), null, "PasswordExpiryReminder", contextString);
                emailEventPublisher.publicEmailEvent(emailEvent);
                user.setPasswordRemind(true);
                userRepository.save(user);
            });
        }
    }

    @Override
    public void validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (isNull(passToken) || passToken.isUsed() || isTokenExpired(passToken)) {
            throw new GenericException(ExceptionConstants.RESET_PASSWORD_TOKEN_INVALID_EXCEPTION_CODE, ExceptionConstants.RESET_PASSWORD_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public User resetPassword(String token, String newPassword) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        User user = passToken.getUser();
        changePassword(user, newPassword);
        passToken.setUsed(true);
        passwordResetTokenRepository.save(passToken);
        return user;
    }

    @Override
    public void changePassword(User user, String newPassword) {
        validatePasswordHistory(user, newPassword);
        savePassword(newPassword, user);
    }

    @Override
    public User changePasswordByLoginId(String loginId, PasswordDto request) {
        User user = getUserByLoginId(loginId);
        validateOldPassword(request.getOldPassword(), user);
        changePassword(user, request.getNewPassword());
        return user;
    }

    @Override
    public Department getCurrentUserDepartment() {
        User user = authenticatedInfoService.getCurrentUser();
        return user.getDepartment();
    }

    @Override
    public List<MeetingGroup> getCurrentUserMeetingGroup() {
        User user = authenticatedInfoService.getCurrentUser();
        return getUserMeetingGroup(user);
    }

    @Override
    public List<MeetingGroup> getUserMeetingGroup(User user) {
        return userHasGroupRepository.findByUser(user);
    }

    @Override
    public boolean isMeetingWorkspaceMaintenance() {
        User currentUser = authenticatedInfoService.getCurrentUser();
        return currentUser.getPermissions()
                .stream()
                .anyMatch(p -> p.getCode().equals("MEETING_WORKSPACE_MAINTENANCE"));
    }

    @Override
    public Page<User> search(Pageable pageable, String keyword) {
        return userRepository.findByEmailOrName(pageable, keyword);
    }

    @Override
    public void increaseLoginAttempt(String id) {
        userRepository.increaseLoginAttempt(id);
    }

    @Override
    public void blockUser(String id) {
        userRepository.blockUser(id);
    }

    @Override
    public boolean isUserPasswordExpire(User user) {
        return (daysAfterChangePassword(user) + configurationService.getPasswordChangeMinDay()) < 0;
    }

    private void setDepartment(UserDto request, User user) {
        if (ObjectUtils.isNotEmpty(request.getDepartment())) {
            user.setDepartment(departmentService.getDepartmentByCode(request.getDepartment().getDepartmentCode()));
        }
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDate.now());
    }

    private void validatePasswordHistory(User user, String encodedPassword) {
        user.getPasswords()
                .stream()
                .map(Password::getPasswordHash)
                .filter(passwordHash -> passwordEncoder.matches(encodedPassword, passwordHash))
                .findAny()
                .ifPresent(p -> {
                    throw new GenericException(ExceptionConstants.PASSWORD_USED_BEFORE_EXCEPTION_CODE, ExceptionConstants.PASSWORD_USED_BEFORE_EXCEPTION_MESSAGE);
                });
    }

    private int daysAfterChangePassword(User user) {
        return userRepository.findDayAfterChangePassword(user.getUserId());
    }
}
