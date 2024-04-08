package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.MeetingGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingGroupRepository extends JpaRepository<MeetingGroup, Long> {
    @Query("select g from MeetingGroup g where g.code = :code")
    MeetingGroup findByCode(@Param("code") String code);

    @Query("select m from MeetingGroup m where :keyword is null or (m.code like %:keyword% or m.name like %:keyword%)")
    Page<MeetingGroup> findByCodeOrName(Pageable pageable, @Param("keyword") String keyword);
}