package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static com.hkgov.csb.eproof.config.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class UserDto {
    private Long Id;

    private String dpUserId;

    private String dpDeptId;

    private String name;

    private String post;

    private String email;

    private String status;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime lastLoginDate;

    private List<RoleDto> roles;

    private List<Long> roleList;
}
