package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.UserDto;
import com.hkgov.ceo.pms.dto.UserHasMeetingGroupDto;
import com.hkgov.ceo.pms.service.MeetingGroupService;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import io.micrometer.common.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class UserDtoMappingStrategy extends HeaderColumnNameMappingStrategy<UserDto> {
    private final MeetingGroupService meetingGroupService;

    public UserDtoMappingStrategy(MeetingGroupService meetingGroupService) {
        this.meetingGroupService = meetingGroupService;
        setType(UserDto.class);
    }

    @Override
    public UserDto populateNewBean(String[] columns) throws CsvChainedException, CsvFieldAssignmentException {
        UserDto userDto = super.populateNewBean(columns);
        String[] meetingGroupsCode = columns[5].split(",");
        List<UserHasMeetingGroupDto> userHasMeetingGroupDtos = Arrays.stream(meetingGroupsCode)
                .filter(StringUtils::isNotEmpty)
                .map(String::trim)
                .map(meetingGroupService::getMeetingGroupByCode)
                .map(MeetingGroupMapper.INSTANCE::sourceToDestination)
                .map(meetingGroupDto -> {
                    UserHasMeetingGroupDto userHasMeetingGroupDto = new UserHasMeetingGroupDto();
                    userHasMeetingGroupDto.setMeetingGroup(meetingGroupDto);
                    return userHasMeetingGroupDto;
                })
                .toList();
        userDto.setUserHasMeetingGroups(userHasMeetingGroupDtos);
        return userDto;
    }
}
