package com.hkgov.csb.eproof.mapper;

import com.hkgov.csb.eproof.dto.LocationDto;
import com.hkgov.csb.eproof.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationDto sourceToDestination(Location source);

    Location destinationToSource(LocationDto dto);

    List<LocationDto> sourceToDestinationList(List<Location> locations);

    List<Location> destinationToSourceList(List<LocationDto> locations);
}
