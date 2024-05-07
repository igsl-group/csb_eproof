package com.hkgov.csb.eproof.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.config.Constants.DATE_TIME_PATTERN;

public class MeetingSearchData {
    private String title;
    private String url;
    private String location;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime startTime;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
