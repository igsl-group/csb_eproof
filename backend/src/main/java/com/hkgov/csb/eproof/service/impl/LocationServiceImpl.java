package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.LocationRepository;
import com.hkgov.csb.eproof.entity.Location;
import com.hkgov.csb.eproof.service.LocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Page<Location> getDropdown(Pageable pageable, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return locationRepository.findAll(pageable);
        } else {
            return locationRepository.findByName(pageable, keyword);
        }
    }

    @Override
    public void add(String name) {
        if (!checkIfExist(name)) {
            Location location = new Location();
            location.setName(name);
            locationRepository.save(location);
        }
    }

    private boolean checkIfExist(String name) {
        return locationRepository.findByName(name) != null;
    }
}
