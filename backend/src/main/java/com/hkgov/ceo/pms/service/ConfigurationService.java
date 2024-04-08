package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.dto.ConfigurationDto;
import com.hkgov.ceo.pms.entity.Configuration;

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
