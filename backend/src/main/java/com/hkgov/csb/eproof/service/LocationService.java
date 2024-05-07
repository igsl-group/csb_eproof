package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationService {

    Page<Location> getDropdown(Pageable pageable, String keyword);

    void add(String name);
}
