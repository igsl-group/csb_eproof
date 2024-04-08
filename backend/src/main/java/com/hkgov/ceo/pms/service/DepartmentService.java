package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.dto.DepartmentDto;
import com.hkgov.ceo.pms.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    Page<Department> getAllDepartment(Pageable pageable);

    Department getDepartmentByCode(String code);

    Department createDepartment(DepartmentDto request);

    Department updateDepartment(DepartmentDto request);

    Department removeDepartment(String code);

    List<Department> getAllDepartment();

    Page<Department> search(Pageable pageable, String keyword);
}
