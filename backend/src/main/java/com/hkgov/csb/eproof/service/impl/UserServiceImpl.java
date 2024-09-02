package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.dao.UserHasRoleRepository;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserHasRole;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.UserMapper;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE;
/**
* @author David
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-04-22 16:26:25
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserHasRoleRepository userHasRoleRepository;

    private final AuditLogService auditLogService;


    @Override
    public Boolean createUser(UserDto request) {
        User user = UserMapper.INSTANCE.destinationToSource(request);
        user.setDpDeptId("csb");
        user.setStatus(request.getStatus());
        user =  userRepository.save(user);
        auditLogService.addLog("Create","Create User " +user.getUsername(), request);
        return Objects.nonNull(user);
    }

    @Override
    public Boolean updateUser(Long userId, UserDto request) {
        User user = userRepository.getUserById(userId);
        UserMapper.INSTANCE.updateFromDto(request,user);

        user = userRepository.save(user);
        List<UserHasRole> oldRoles = userHasRoleRepository.roles(request.getId());
        userHasRoleRepository.deleteAllById(oldRoles.stream().map(UserHasRole::getId).collect(Collectors.toList()));
        auditLogService.addLog("Update","Update User " +user.getUsername() + " information", request);
        return Objects.nonNull(user);
    }

    @Override
    public Page<User> getAllUser(Pageable pageable) {
        var user = userRepository.findAll(pageable);
        return user;
    }

    @Override
    public User getUserInfo(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public User removeUser(Long id) {
        User user = getUserInfo(id);
        if (Objects.isNull(user)) {
            throw new GenericException(USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE, USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE);
        }
        userRepository.delete(user);
        auditLogService.addLog("Delete","Delete User " +user.getUsername(), null);
        return user;
    }

    @Override
    @Transactional
    public User getUserByDpUserId(String dpUserId) {
        User user = userRepository.getUserByDpUserId(dpUserId);
        Hibernate.initialize(user.getAuthorities());
        return user;
    }


}




