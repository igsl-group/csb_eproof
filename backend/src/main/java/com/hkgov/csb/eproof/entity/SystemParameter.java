package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "system_parameter")
@Getter
@Setter
public class SystemParameter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "name",columnDefinition = "varchar(4000)")
    private String name;

    @Column(name = "value",columnDefinition = "varchar(4000)")
    private String value;

}