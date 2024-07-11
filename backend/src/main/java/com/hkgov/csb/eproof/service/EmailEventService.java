package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EmailEventService {
    void updateEmailEventStatus(List<EmailEvent> emailEventList, String status);
}
