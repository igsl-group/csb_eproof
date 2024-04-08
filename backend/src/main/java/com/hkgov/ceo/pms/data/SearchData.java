package com.hkgov.ceo.pms.data;

import java.util.List;

public class SearchData {
    private List<MeetingSearchData> meetings;
    private List<AgendaSearchData> agendas;
    private List<TaskSearchData> tasks;
    private List<DocumentSearchData> documents;

    public List<MeetingSearchData> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<MeetingSearchData> meetings) {
        this.meetings = meetings;
    }

    public List<AgendaSearchData> getAgendas() {
        return agendas;
    }

    public void setAgendas(List<AgendaSearchData> agendas) {
        this.agendas = agendas;
    }

    public List<TaskSearchData> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskSearchData> tasks) {
        this.tasks = tasks;
    }

    public List<DocumentSearchData> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentSearchData> documents) {
        this.documents = documents;
    }
}
