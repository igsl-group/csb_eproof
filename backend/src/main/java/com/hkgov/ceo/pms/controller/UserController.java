package com.hkgov.ceo.pms.controller;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.dto.PasswordDto;
import com.hkgov.ceo.pms.dto.UserDto;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.mapper.UserDtoMappingStrategy;
import com.hkgov.ceo.pms.mapper.UserMapper;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import com.hkgov.ceo.pms.service.CsvService;
import com.hkgov.ceo.pms.service.MeetingGroupService;
import com.hkgov.ceo.pms.service.UserService;
import com.hkgov.ceo.pms.validator.CustomValidator;
import com.opencsv.bean.MappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.hkgov.ceo.pms.config.AuditTrailConstants.PASSWORD_WORDING;
import static com.hkgov.ceo.pms.config.AuditTrailConstants.PROFILE_WORDING;
import static com.hkgov.ceo.pms.config.AuditTrailConstants.USERS_WORDING;
import static com.hkgov.ceo.pms.config.AuditTrailConstants.USER_WORDING;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private AuthenticatedInfoService authenticatedInfoService;
    private final UserService userService;
    private final CsvService csvService;
    private final CustomValidator validator;
    private final MeetingGroupService meetingGroupService;

    public UserController(UserService userService, CsvService csvService, CustomValidator validator, MeetingGroupService meetingGroupService) {
        this.userService = userService;
        this.csvService = csvService;
        this.validator = validator;
        this.meetingGroupService = meetingGroupService;
    }

    @Secured({"USER_VIEWER"})
    @GetMapping("/dropdown")
    public List<UserDto> getAllUserDropdown() {
        return userService.getAllUser()
                .stream()
                .map(UserMapper.INSTANCE::sourceToDestination)
                .toList();
    }

    @GetMapping("/getCurrentUser")
    public UserDto getCurrentUser() {
        User currentUser = authenticatedInfoService.getCurrentUser();
        return UserMapper.INSTANCE.sourceToDestination(userService.getUserByLoginId(currentUser.getLoginId()));
    }

    @Secured({"USER_VIEWER"})
    @GetMapping("/get")
    public UserDto getUser(String loginId) {
        return UserMapper.INSTANCE.sourceToDestination(userService.getUserByLoginId(loginId));
    }

    @Audit(action = "Create", resourceWording = USER_WORDING, resourceResolverName = "userResourceResolver")
    @Secured({"USER_MAINTENANCE"})
    @PostMapping("/create")
    public UserDto createUser(@RequestBody UserDto requestDto) {
        return UserMapper.INSTANCE.sourceToDestination(userService.createUser(requestDto));
    }

    @Audit(action = "Create", resourceWording = USERS_WORDING, resourceResolverName = "resourceWordingAsAuditResourceResolver")
    @Secured({"USER_MAINTENANCE"})
    @PostMapping("/batch/create")
    public List<UserDto> createUsers(@RequestParam MultipartFile multipart) {
        MappingStrategy<UserDto> strategy = new UserDtoMappingStrategy(meetingGroupService);
        List<UserDto> userDtos = csvService.convertToObject(multipart, UserDto.class, strategy);
        return UserMapper.INSTANCE.toDtoList(userService.createUsers(userDtos));
    }

    @Audit(action = "Update", resourceWording = USER_WORDING, resourceResolverName = "userResourceResolver")
    @Secured({"USER_MAINTENANCE"})
    @PatchMapping("/update")
    public UserDto updateUser(@RequestBody UserDto requestDto) {
        return UserMapper.INSTANCE.sourceToDestination(userService.updateUser(requestDto));
    }

    @Audit(action = "Update", resourceWording = PROFILE_WORDING, resourceResolverName = "resourceWordingAsAuditResourceResolver")
    @PatchMapping("/profile")
    public UserDto updateSelfProfile(@RequestBody UserDto requestDto) {
        return UserMapper.INSTANCE.sourceToDestination(userService.updateSelfProfile(requestDto));
    }

    @Audit(action = "Delete", resourceWording = USER_WORDING, resourceResolverName = "userResourceResolver")
    @Secured({"USER_MAINTENANCE"})
    @DeleteMapping("/remove")
    public UserDto removeUser(String loginId) {
        return UserMapper.INSTANCE.sourceToDestination(userService.removeUser(loginId));
    }

    @Audit(action = "Forgot", resourceWording = PASSWORD_WORDING, resourceResolverName = "resourceWordingAsAuditResourceResolver")
    @PostMapping("/forgotPassword")
    public UserDto forgotPassword(@RequestParam String email) {
        return UserMapper.INSTANCE.sourceToDestination(userService.forgotPassword(email));
    }

    @Audit(action = "Reset", resourceWording = PASSWORD_WORDING, resourceResolverName = "resourceWordingAsAuditResourceResolver")
    @Transactional
    @PostMapping("/resetPassword")
    public UserDto resetPassword(@RequestParam String token, @RequestBody PasswordDto request) {
        userService.validatePasswordResetToken(token);
        validator.validate(request);
        return UserMapper.INSTANCE.sourceToDestination(userService.resetPassword(token, request.getNewPassword()));
    }

    @Audit(action = "Change", resourceWording = PASSWORD_WORDING, resourceResolverName = "resourceWordingAsAuditResourceResolver")
    @PostMapping("/changePassword")
    public UserDto forgotPassword(@RequestParam String loginId, @RequestBody PasswordDto request) {
        return UserMapper.INSTANCE.sourceToDestination(userService.changePasswordByLoginId(loginId, request));
    }

    @Secured({"USER_VIEWER"})
    @GetMapping("/search")
    public Page<UserDto> search(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "department.departmentName") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<User> users = userService.search(pageable, keyword);
        return users.map(UserMapper.INSTANCE::sourceToDestination);
    }
}
