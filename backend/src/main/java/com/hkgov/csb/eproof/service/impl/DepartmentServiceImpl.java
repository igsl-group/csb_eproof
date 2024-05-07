package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.DepartmentRepository;
import com.hkgov.csb.eproof.dto.DepartmentDto;
import com.hkgov.csb.eproof.entity.Department;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.DepartmentService;
import com.hkgov.csb.eproof.exception.ExceptionConstants;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Page<Department> getAllDepartment(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    @Override
    public Department getDepartmentByCode(String code) {
        return findDepartmentByCode(code);
    }

    @Override
    public Department createDepartment(DepartmentDto request) {
        Department department = new Department();
        department.setDepartmentCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(DepartmentDto request) {
        Department department = findDepartmentByCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        return departmentRepository.save(department);
    }

    @Override
    public Department removeDepartment(String code) {
        Department department = findDepartmentByCode(code);
        departmentRepository.delete(department);
        return department;
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

    @Override
    public Page<Department> search(Pageable pageable, String keyword) {
        return departmentRepository.findByCodeOrName(pageable, keyword);
    }

    private Department findDepartmentByCode(String code) {
        return Optional.ofNullable(departmentRepository.findByDepartmentCode(code))
                .orElseThrow(() -> new GenericException(ExceptionConstants.DEPARTMENT_NOT_FOUND_EXCEPTION_CODE, ExceptionConstants.DEPARTMENT_HAS_PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
