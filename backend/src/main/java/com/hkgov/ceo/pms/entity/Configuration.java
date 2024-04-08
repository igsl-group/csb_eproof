package com.hkgov.ceo.pms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Configuration", indexes = {
        @Index(name = "idx_configuration_code", columnList = "code")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_configuration_code", columnNames = {"code"})
})
public class Configuration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuration_id")
    private Long configurationId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "label")
    private String label;

    @Column(name = "value")
    private String value;

    public Long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}