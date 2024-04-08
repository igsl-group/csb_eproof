package com.hkgov.ceo.pms.mapper;

import com.hkgov.ceo.pms.dto.UserDto;
import com.hkgov.ceo.pms.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto sourceToDestination(User source);

    @Mapping(target = "userHasMeetingGroups", ignore = true)
    @Mapping(target = "userHasRoles", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(source = "email", target = "email", qualifiedByName = "emptyEmailToNull")
    User destinationToSource(UserDto dto);

    @Mapping(target = "userHasMeetingGroups", ignore = true)
    @Mapping(target = "userHasRoles", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateFromDto(UserDto dto, @MappingTarget User user);

    List<UserDto> toDtoList(List<User> source);

    @Named("emptyEmailToNull")
    static String emptyEmailToNull(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        } else {
            return email;
        }
    }
}
