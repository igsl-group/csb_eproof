package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.mapper.UserMapper;
import com.hkgov.csb.eproof.service.UserService;
import com.hkgov.csb.eproof.util.Result;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Transactional(rollbackFor = Exception.class)
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/create")
    public Result<Boolean> createUser(@RequestBody UserDto requestDto) {
        return Result.success(userService.createUser(requestDto));
    }

    @PatchMapping("/update/{userId}")
    public Result<Boolean> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto requestDto
    ) {
        return Result.success(userService.updateUser(userId,requestDto));
    }
    @GetMapping("/list")
    public Result<Page<UserDto>> getUserList(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                             @RequestParam(defaultValue = "id") String... properties){
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        return Result.success(userService.getAllUser(pageable).map(UserMapper.INSTANCE::sourceToDestination));
    }
    @GetMapping("/{userId}")
    public Result<UserDto> getUserInfo(@PathVariable Long userId){
        return Result.success(UserMapper.INSTANCE.sourceToDestination(userService.getUserInfo(userId)));
    }

    @DeleteMapping("/delete/{userId}")
    public UserDto removeUser(@PathVariable Long userId) {
        return UserMapper.INSTANCE.sourceToDestination(userService.removeUser(userId));
    }

}
