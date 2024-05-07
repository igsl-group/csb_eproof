package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.ConfigurationDto;
import com.hkgov.csb.eproof.entity.Configuration;

import java.util.List;
import java.util.Map;

public interface ConfigurationService {
    List<Configuration> getAll();

    List<Configuration> update(Map<String, ConfigurationDto> configurationDtoMap);

    Configuration getConfigurationByCode(String code);

    int getWorkspaceRetentionDays();

    int getAuditLogMaxNo();
    int getPasswordExpiryReminderDays();

    int getMaxPasswordRecordNo();
    int getPasswordChangeMinDay();
}
