package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.dao.ConfigurationRepository;
import com.hkgov.ceo.pms.dto.ConfigurationDto;
import com.hkgov.ceo.pms.entity.Configuration;
import com.hkgov.ceo.pms.mapper.ConfigurationMapper;
import com.hkgov.ceo.pms.service.ConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.hkgov.ceo.pms.config.ConfigurationConstants.AUDIT_RECORD_MAX_NO;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.AUDIT_RECORD_MAX_NO_DEFAULT_VALUE;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.PASSWORD_CHANGE_MIN_DAY;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.PASSWORD_CHANGE_MIN_DAY_DEFAULT_VALUE;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.PASSWORD_EXPIRY_REMINDER_DAY;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.PASSWORD_EXPIRY_REMINDER_DAY_DEFAULT_VALUE;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.PASSWORD_RECORD_MAX_NO;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.PASSWORD_RECORD_MAX_NO_DEFAULT_VALUE;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.WORKSPACE_DAYS_REMAINING_NO;
import static com.hkgov.ceo.pms.config.ConfigurationConstants.WORKSPACE_DAYS_REMAINING_NO_DEFAULT_VALUE;


@Service
@Transactional
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public ConfigurationServiceImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public List<Configuration> getAll() {
        return configurationRepository.findAll();
    }

    @Override
    public List<Configuration> update(Map<String, ConfigurationDto> configurationDtoMap) {
        List<Configuration> configurationList = configurationRepository.findAll();
        return configurationList
                .stream()
                .map(configuration -> {
                    ConfigurationDto configurationDto = configurationDtoMap.get(configuration.getCode());
                    Configuration configurationToBeUpdated = ConfigurationMapper.INSTANCE.partialUpdate(configurationDto, configuration);
                    return configurationRepository.save(configurationToBeUpdated);
                })
                .toList();
    }

    @Override
    public Configuration getConfigurationByCode(String code) {
        return configurationRepository.findByCode(code);
    }

    @Override
    public int getWorkspaceRetentionDays() {
        return Optional.ofNullable(getConfigurationByCode(WORKSPACE_DAYS_REMAINING_NO))
                .map(Configuration::getValue)
                .map(Integer::parseInt)
                .orElse(WORKSPACE_DAYS_REMAINING_NO_DEFAULT_VALUE);
    }

    @Override
    public int getAuditLogMaxNo() {
        return Optional.ofNullable(getConfigurationByCode(AUDIT_RECORD_MAX_NO))
                .map(Configuration::getValue)
                .map(Integer::parseInt)
                .orElse(AUDIT_RECORD_MAX_NO_DEFAULT_VALUE);
    }

    @Override
    public int getPasswordExpiryReminderDays() {
        return Optional.ofNullable(configurationRepository.findByCode(PASSWORD_EXPIRY_REMINDER_DAY))
                .map(Configuration::getValue)
                .map(Integer::parseInt)
                .orElse(PASSWORD_EXPIRY_REMINDER_DAY_DEFAULT_VALUE);
    }

    @Override
    public int getMaxPasswordRecordNo() {
        return Optional.ofNullable(configurationRepository.findByCode(PASSWORD_RECORD_MAX_NO))
                .map(Configuration::getValue)
                .map(Integer::parseInt)
                .orElse(PASSWORD_RECORD_MAX_NO_DEFAULT_VALUE);
    }

    @Override
    public int getPasswordChangeMinDay() {
        return Optional.ofNullable(configurationRepository.findByCode(PASSWORD_CHANGE_MIN_DAY))
                .map(Configuration::getValue)
                .map(Integer::parseInt)
                .orElse(PASSWORD_CHANGE_MIN_DAY_DEFAULT_VALUE);
    }
}
