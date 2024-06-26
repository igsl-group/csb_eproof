package com.hkgov.ceo.pms.controller;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.dto.ConfigurationDto;
import com.hkgov.ceo.pms.entity.Configuration;
import com.hkgov.ceo.pms.mapper.ConfigurationMapper;
import com.hkgov.ceo.pms.service.ConfigurationService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hkgov.ceo.pms.config.AuditTrailConstants.CONFIGURATIONS_WORDING;

@RestController
@RequestMapping("/api/v1/configuration")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Secured({"CONFIGURATION_MAINTENANCE"})
    @GetMapping("/all")
    public Map<String, ConfigurationDto> getAll() {
        List<Configuration> configurationList = configurationService.getAll();
        return configurationList
                .stream()
                .map(ConfigurationMapper.INSTANCE::toDto)
                .collect(Collectors.toMap(ConfigurationDto::getCode, Function.identity()));
    }

    @Audit(action = "Update", resourceWording = CONFIGURATIONS_WORDING, resourceResolverName = "resourceWordingAsAuditResourceResolver")
    @Secured({"CONFIGURATION_MAINTENANCE"})
    @PatchMapping("/")
    public Map<String, ConfigurationDto> update(@RequestBody Map<String, ConfigurationDto> configurationDtoMap) {
        List<Configuration> configurationList = configurationService.update(configurationDtoMap);
        return configurationList
                .stream()
                .map(ConfigurationMapper.INSTANCE::toDto)
                .collect(Collectors.toMap(ConfigurationDto::getCode, Function.identity()));
    }
}
