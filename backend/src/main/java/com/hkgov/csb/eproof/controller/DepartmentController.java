package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.dto.DepartmentDto;
import com.hkgov.csb.eproof.entity.Department;
import com.hkgov.csb.eproof.mapper.DepartmentMapper;
import com.hkgov.csb.eproof.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hkgov.csb.eproof.config.AuditTrailConstants.DEPARTMENT_WORDING;

@RestController
@RequestMapping("/api/v1/department")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Secured({"BD_TABLE_VIEWER"})
    @GetMapping("/dropdown")
    public List<DepartmentDto> getDepartmentDropdown() {
        return DepartmentMapper.INSTANCE.sourceToDestinationList(departmentService.getAllDepartment());
    }

    @Secured({"BD_TABLE_VIEWER"})
    @GetMapping("/get")
    public DepartmentDto getDepartment(String code) {
        return DepartmentMapper.INSTANCE.sourceToDestination(departmentService.getDepartmentByCode(code));
    }

    @Audit(action = "Create", resourceWording = DEPARTMENT_WORDING, resourceResolverName = "departmentResourceResolver")
    @Secured({"BD_TABLE_MAINTENANCE"})
    @PostMapping("/create")
    public DepartmentDto createDepartment(@RequestBody DepartmentDto requestDto) {
        return DepartmentMapper.INSTANCE.sourceToDestination(departmentService.createDepartment(requestDto));
    }

    @Audit(action = "Update", resourceWording = DEPARTMENT_WORDING, resourceResolverName = "departmentResourceResolver")
    @Secured({"BD_TABLE_MAINTENANCE"})
    @PatchMapping("/update")
    public DepartmentDto updateDepartment(@RequestBody DepartmentDto requestDto) {
        return DepartmentMapper.INSTANCE.sourceToDestination(departmentService.updateDepartment(requestDto));
    }

    @Audit(action = "Delete", resourceWording = DEPARTMENT_WORDING, resourceResolverName = "departmentResourceResolver")
    @Secured({"BD_TABLE_MAINTENANCE"})
    @DeleteMapping("/remove")
    public DepartmentDto removeDepartment(String departmentCode) {
        return DepartmentMapper.INSTANCE.sourceToDestination(departmentService.removeDepartment(departmentCode));
    }

    @Secured({"BD_TABLE_VIEWER"})
    @GetMapping("/search")
    public Page<DepartmentDto> search(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "departmentCode") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<Department> departments = departmentService.search(pageable, keyword);
        return departments.map(DepartmentMapper.INSTANCE::sourceToDestination);
    }
}
