package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.dto.MeetingGroupDto;
import com.hkgov.ceo.pms.entity.MeetingGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeetingGroupService {
    Page<MeetingGroup> getAllMeetingGroup(Pageable pageable);

    List<MeetingGroup> getAllMeetingGroup();

    MeetingGroup getMeetingGroupByCode(String code);

    MeetingGroup updateMeetingGroup(MeetingGroupDto request);

    MeetingGroup createMeetingGroup(MeetingGroupDto request);

    MeetingGroup removeMeetingGroup(String code);

    Page<MeetingGroup> search(Pageable pageable, String keyword);
}
