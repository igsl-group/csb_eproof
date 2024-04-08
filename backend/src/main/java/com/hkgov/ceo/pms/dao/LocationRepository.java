package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Page<Location> findAll(Pageable pageable);

    @Query("select l from Location l where l.name = :name")
    Location findByName(@Param("name") String name);

    @Query("select l from Location l where l.name like %:keyword%")
    Page<Location> findByName(Pageable pageable, @Param("keyword") String keyword);
}
