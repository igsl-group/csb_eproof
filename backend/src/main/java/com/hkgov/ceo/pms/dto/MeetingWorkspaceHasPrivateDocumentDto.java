package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.entity.Views;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.hkgov.ceo.pms.entity.MeetingWorkspaceHasPrivateDocument} entity
 */

@JsonView(Views.Public.class)
public class MeetingWorkspaceHasPrivateDocumentDto implements Serializable {
    private final FileDto file;
    @JsonView(Views.Internal.class)
    private final DepartmentDto department;

    public MeetingWorkspaceHasPrivateDocumentDto(FileDto file, DepartmentDto department) {
        this.file = file;
        this.department = department;
    }

    public FileDto getFile() {
        return file;
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingWorkspaceHasPrivateDocumentDto entity = (MeetingWorkspaceHasPrivateDocumentDto) o;
        return Objects.equals(this.file, entity.file) &&
                Objects.equals(this.department, entity.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, department);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "file = " + file + ")";
    }
}