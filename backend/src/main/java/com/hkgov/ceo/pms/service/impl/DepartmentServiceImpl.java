package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.dao.DepartmentRepository;
import com.hkgov.ceo.pms.dto.DepartmentDto;
import com.hkgov.ceo.pms.entity.Department;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.service.DepartmentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.DEPARTMENT_HAS_PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.DEPARTMENT_NOT_FOUND_EXCEPTION_CODE;

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
                .orElseThrow(() -> new GenericException(DEPARTMENT_NOT_FOUND_EXCEPTION_CODE, DEPARTMENT_HAS_PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
