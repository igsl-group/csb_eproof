package com.hkgov.ceo.pms.dto;

import java.util.ArrayList;
import java.util.List;

public class FolderDto {
    private String name;
    private String code;
    private List<FolderDto> subFolders;
    private List<MeetingWorkspaceHasPrivateDocumentDto> documents = new ArrayList<>();

    public FolderDto() {
        subFolders = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FolderDto> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(List<FolderDto> subFolders) {
        this.subFolders = subFolders;
    }

    public List<MeetingWorkspaceHasPrivateDocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MeetingWorkspaceHasPrivateDocumentDto> documents) {
        this.documents = documents;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
