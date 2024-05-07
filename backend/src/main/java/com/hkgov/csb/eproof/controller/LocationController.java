package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.LocationDto;
import com.hkgov.csb.eproof.entity.Location;
import com.hkgov.csb.eproof.mapper.LocationMapper;
import com.hkgov.csb.eproof.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/getAll")
    public Page<LocationDto> getAll(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "name") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<Location> locations = locationService.getDropdown(pageable, keyword);
        return locations.map(LocationMapper.INSTANCE::sourceToDestination);
    }
}
