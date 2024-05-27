package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
* @author David
* @description 针对表【user】的数据库操作Service
* @createDate 2024-04-22 16:26:25
*/
public interface UserService{

    Boolean createUser(UserDto request);

    Boolean updateUser(Long userId, UserDto request);

    Page<User> getAllUser(Pageable pageable);

    User getUserInfo(Long userId);

    User removeUser(Long id);

}
