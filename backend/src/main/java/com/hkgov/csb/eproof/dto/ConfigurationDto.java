package com.hkgov.csb.eproof.dto;

import com.hkgov.csb.eproof.entity.Configuration;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Configuration} entity
 */
public class ConfigurationDto implements Serializable {
    private final String code;
    private final String label;
    private final String value;

    public ConfigurationDto(String code, String label, String value) {
        this.code = code;
        this.label = label;
        this.value = value;
    }


    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationDto entity = (ConfigurationDto) o;
        return Objects.equals(this.code, entity.code) &&
                Objects.equals(this.label, entity.label) &&
                Objects.equals(this.value, entity.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, label, value);
    }

    @Override
    public String toString() {
        return "ConfigurationDto{" +
                "code='" + code + '\'' +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}