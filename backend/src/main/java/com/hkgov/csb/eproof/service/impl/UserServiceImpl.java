package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.dao.RoleRepository;
import com.hkgov.csb.eproof.dao.UserHasRoleRepository;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserHasRole;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.UserMapper;
import com.hkgov.csb.eproof.service.AuthenticationService;
import com.hkgov.csb.eproof.service.UserService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
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
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserHasRoleRepository userHasRoleRepository;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private AuthenticationService authenticationService;

    @Override
    public Boolean createUser(UserDto request) {
        User user = UserMapper.INSTANCE.destinationToSource(request);
        user.setDpUserId("csb");
        user.setStatus(request.getStatus());
        user =  userRepository.save(user);
//        Long id = user.getId();
//        List<UserHasRole> roles = request.getRoles().stream().map(RoleDto::getId).map(x -> new UserHasRole(null,id,x)).collect(Collectors.toList());
//        userHasRoleRepository.saveAll(roles);
        return Objects.nonNull(user);
    }

    public Boolean updateUser(Long userId, UserDto request) {
//        User user = userRepository.getUserByDpUserIdAndDpDeptId(request.getDpUserId(),"CSB");
        User user = userRepository.getUserById(userId);
        UserMapper.INSTANCE.updateFromDto(request,user);

        user = userRepository.save(user);
        List<UserHasRole> oldRoles = userHasRoleRepository.roles(request.getId());
        userHasRoleRepository.deleteAllById(oldRoles.stream().map(UserHasRole::getId).collect(Collectors.toList()));
//        Long id = user.getId();
//        List<UserHasRole> newqroles = request.getRoles().stream().map(RoleDto::getId).map(x -> new UserHasRole(null,id,x)).collect(Collectors.toList());
//        userHasRoleRepository.saveAll(newqroles);
        return Objects.nonNull(user);
    }

    @Override
    public Page<User> getAllUser(Pageable pageable) {
        var user = userRepository.findAll(pageable);
        return user;
    }

    @Override
    public User getUserInfo(Long id) {
       /* Remove redundant codes
       User user = new User();
       user = userRepository.getUserById(id);
       if(Objects.isNull(user))
           return null;
       return user;*/
        return userRepository.getUserById(id);
    }

    @Override
    public User removeUser(Long id) {
        User user = getUserInfo(id);
        if (Objects.isNull(user)) {
            throw new GenericException(USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE, USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE);
        }
        if (ObjectUtils.isNotEmpty(user)) {
            userRepository.delete(user);
        }
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




