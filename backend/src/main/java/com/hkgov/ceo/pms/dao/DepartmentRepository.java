package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("select d from Department d where d.departmentCode = :departmentCode")
    Department findByDepartmentCode(@Param("departmentCode") String departmentCode);

    @Query("select d from Department d where :keyword is null or (d.departmentCode like %:keyword% or d.departmentName like %:keyword%)")
    Page<Department> findByCodeOrName(Pageable pageable, @Param("keyword") String keyword);
}
