package com.hkgov.csb.eproof.mapper;


import com.hkgov.csb.eproof.dto.UserDto;
import com.hkgov.csb.eproof.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


/**
* @author David
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-04-22 16:26:25
*/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto sourceToDestination(User source);

    User destinationToSource(UserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserDto dto, @MappingTarget User user);
}




