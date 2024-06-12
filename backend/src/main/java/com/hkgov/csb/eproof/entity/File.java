package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file")
@Getter
@Setter
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "path")
    private String path;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

}