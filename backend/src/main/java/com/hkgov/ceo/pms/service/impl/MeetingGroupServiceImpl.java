package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.dao.MeetingGroupRepository;
import com.hkgov.ceo.pms.dto.MeetingGroupDto;
import com.hkgov.ceo.pms.entity.MeetingGroup;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.service.MeetingGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.GROUP_NOT_FOUND_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.GROUP_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@Transactional
public class MeetingGroupServiceImpl implements MeetingGroupService {
    private final MeetingGroupRepository meetingGroupRepository;

    public MeetingGroupServiceImpl(MeetingGroupRepository meetingGroupRepository) {
        this.meetingGroupRepository = meetingGroupRepository;
    }

    @Override
    public Page<MeetingGroup> getAllMeetingGroup(Pageable pageable) {
        return meetingGroupRepository.findAll(pageable);
    }

    @Override
    public List<MeetingGroup> getAllMeetingGroup() {
        return meetingGroupRepository.findAll();
    }

    @Override
    public MeetingGroup getMeetingGroupByCode(String code) {
        return findMeetingGroupByCode(code);
    }

    @Override
    public MeetingGroup updateMeetingGroup(MeetingGroupDto request) {
        MeetingGroup group = findMeetingGroupByCode(request.getCode());
        if (StringUtils.isNotBlank(request.getName())) {
            group.setName(request.getName());
        }
        meetingGroupRepository.save(group);
        return group;
    }

    @Override
    public MeetingGroup createMeetingGroup(MeetingGroupDto request) {
        MeetingGroup group = new MeetingGroup();
        group.setCode(request.getCode());
        group.setName(request.getName());
        meetingGroupRepository.save(group);
        return group;
    }

    @Override
    public MeetingGroup removeMeetingGroup(String code) {
        MeetingGroup group = findMeetingGroupByCode(code);
        meetingGroupRepository.delete(group);
        return group;
    }

    @Override
    public Page<MeetingGroup> search(Pageable pageable, String keyword) {
        return meetingGroupRepository.findByCodeOrName(pageable, keyword);
    }

    private MeetingGroup findMeetingGroupByCode(String code) {
        return Optional.ofNullable(code)
                .map(meetingGroupRepository::findByCode)
                .orElseThrow(() -> new GenericException(GROUP_NOT_FOUND_EXCEPTION_CODE, GROUP_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
