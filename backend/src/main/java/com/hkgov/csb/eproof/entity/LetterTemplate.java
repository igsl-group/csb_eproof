package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "letter_template")
@Getter
@Setter
public class LetterTemplate extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "file_id")
    private Long fileId;

    // Mapped tables
    @OneToOne
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    private File file;

}