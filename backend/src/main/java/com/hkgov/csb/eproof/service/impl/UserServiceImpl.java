package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.dao.RoleRepository;
import com.hkgov.csb.eproof.dao.UserHasRoleRepository;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserHasRole;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.UserMapper;
import com.hkgov.csb.eproof.service.UserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE;
/**
* @author 20768
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-04-22 16:26:25
*/
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserHasRoleRepository userHasRoleRepository;
    @Resource
    private RoleRepository roleRepository;

    @Override
    public Boolean createUser(UserDto request) {
        User user = UserMapper.INSTANCE.destinationToSource(request);
        user.setStatus("Active");
        user.setLastLoginDate(LocalDateTime.now());
        user =  userRepository.save(user);
        Long id = user.getId();
        List<UserHasRole> roles = request.getRoleList().stream().map(x -> new UserHasRole(null,id,x)).collect(Collectors.toList());
        userHasRoleRepository.saveAll(roles);
        return Objects.nonNull(user);
    }

    public Boolean updateUser(UserDto request) {
        User user = userRepository.getUserBydpUserId(request.getId().toString());
        UserMapper.INSTANCE.updateFromDto(request,user);
        user.setLastLoginDate(LocalDateTime.now());
        user = userRepository.save(user);
        Long id = user.getId();
        List<UserHasRole> oldRoles = userHasRoleRepository.roles(request.getId());
        userHasRoleRepository.deleteAllById(oldRoles.stream().map(UserHasRole::getId).collect(Collectors.toList()));
        List<UserHasRole> newqroles = request.getRoleList().stream().map(x -> new UserHasRole(null,id,x)).collect(Collectors.toList());
        userHasRoleRepository.saveAll(newqroles);
        return Objects.nonNull(user);
    }

    @Override
    public Page<User> getAllUser(Pageable pageable) {
        var user = userRepository.findAll(pageable);
        return user;
    }

    @Override
    public User getUserInfo(String id) {
       User user = new User();
       user = userRepository.getUserById(id);
       if(Objects.isNull(user))
           return null;
       return user;
    }

    @Override
    public User removeUser(String id) {
        User user = getUserInfo(id);
        if (Objects.isNull(user)) {
            throw new GenericException(USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE, USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE);
        }
        if (ObjectUtils.isNotEmpty(user)) {
            userRepository.delete(user);
        }
        return user;
    }


}




