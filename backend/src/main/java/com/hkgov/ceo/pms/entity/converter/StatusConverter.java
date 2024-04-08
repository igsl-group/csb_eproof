package com.hkgov.ceo.pms.entity.converter;

import com.hkgov.ceo.pms.entity.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status type) {
        if (type == null) {
            return null;
        }
        return type.getCode();
    }

    @Override
    public Status convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Status.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}