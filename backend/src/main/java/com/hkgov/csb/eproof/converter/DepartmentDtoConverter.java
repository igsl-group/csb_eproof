package com.hkgov.csb.eproof.converter;

import com.hkgov.csb.eproof.dto.DepartmentDto;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class DepartmentDtoConverter extends AbstractBeanField<String, DepartmentDto> {
    @Override
    protected DepartmentDto convert(String code) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentCode(code);
        return departmentDto;
    }
}
