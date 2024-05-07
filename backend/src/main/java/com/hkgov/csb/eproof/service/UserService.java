package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.PasswordDto;
import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.dto.UserHasMeetingGroupDto;
import com.hkgov.csb.eproof.dto.UserHasRoleDto;
import com.hkgov.csb.eproof.entity.Department;
import com.hkgov.csb.eproof.entity.MeetingGroup;
import com.hkgov.csb.eproof.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> getAllUser(Pageable pageable);

    List<User> getAllUser();

    User getUserByLoginId(String loginId);

    User getUserByEmail(String email);

    User createUser(UserDto request);

    List<User> createUsers(List<UserDto> requests);

    User updateUser(UserDto request);

    User removeUser(String loginId);

    void savePassword(String password, User user);

    void updateUserHasRole(List<UserHasRoleDto> request, User user);

    void updateUserHasGroup(List<UserHasMeetingGroupDto> request, User user);

    User updateSelfProfile(UserDto request);

    User forgotPassword(String email);

    //User Profile Reset Password By Email
    void resetPasswordByEmail(User user);

    void createPasswordResetTokenForUser(User user, String token);

    void sendPasswordResetEmail(String token, User user);

    void sendPasswordExpireEmail();

    void validatePasswordResetToken(String token);

    User resetPassword(String token, String newPassword);

    void changePassword(User user, String newPassword);

    User changePasswordByLoginId(String loginId, PasswordDto request);

    Department getCurrentUserDepartment();

    List<MeetingGroup> getCurrentUserMeetingGroup();

    List<MeetingGroup> getUserMeetingGroup(User user);

    boolean isMeetingWorkspaceMaintenance();

    Page<User> search(Pageable pageable, String keyword);

    void increaseLoginAttempt(String id);

    void blockUser(String id);

    boolean isUserPasswordExpire(User user);
}
