package com.hkgov.ceo.pms.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.dto.MeetingGroupDto;
import com.hkgov.ceo.pms.entity.MeetingGroup;
import com.hkgov.ceo.pms.entity.Views;
import com.hkgov.ceo.pms.mapper.MeetingGroupMapper;
import com.hkgov.ceo.pms.service.MeetingGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hkgov.ceo.pms.config.AuditTrailConstants.MEETING_GROUP_WORDING;

@RestController
@RequestMapping("/api/v1/meetingGroup")
public class MeetingGroupController {
    private final MeetingGroupService meetingGroupService;

    public MeetingGroupController(MeetingGroupService meetingGroupService) {
        this.meetingGroupService = meetingGroupService;
    }

    @Secured({"GROUP_TABLE_VIEWER"})
    @JsonView(Views.Public.class)
    @GetMapping("/dropdown")
    public List<MeetingGroupDto> getMeetingGroupDropdown() {
        return MeetingGroupMapper.INSTANCE.sourceToDestinationList(meetingGroupService.getAllMeetingGroup());
    }

    @Secured({"GROUP_TABLE_VIEWER"})
    @GetMapping("/get")
    public MeetingGroupDto getMeetingGroup(@RequestParam String code) {
        return MeetingGroupMapper.INSTANCE.sourceToDestination(meetingGroupService.getMeetingGroupByCode(code));
    }

    @Audit(action = "Update", resourceWording = MEETING_GROUP_WORDING, resourceResolverName = "meetingGroupResourceResolver")
    @Secured({"GROUP_TABLE_MAINTENANCE"})
    @PatchMapping("/update")
    public MeetingGroupDto updateMeetingGroup(@RequestBody MeetingGroupDto request) {
        return MeetingGroupMapper.INSTANCE.sourceToDestination(meetingGroupService.updateMeetingGroup(request));
    }

    @Audit(action = "Create", resourceWording = MEETING_GROUP_WORDING, resourceResolverName = "meetingGroupResourceResolver")
    @Secured({"GROUP_TABLE_MAINTENANCE"})
    @PostMapping("/create")
    public MeetingGroupDto createMeetingGroup(@RequestBody MeetingGroupDto request) {
        return MeetingGroupMapper.INSTANCE.sourceToDestination(meetingGroupService.createMeetingGroup(request));
    }

    @Audit(action = "Delete", resourceWording = MEETING_GROUP_WORDING, resourceResolverName = "meetingGroupResourceResolver")
    @Secured({"GROUP_TABLE_MAINTENANCE"})
    @PatchMapping("/remove")
    public MeetingGroupDto removeMeetingGroup(@RequestParam String code) {
        return MeetingGroupMapper.INSTANCE.sourceToDestination(meetingGroupService.removeMeetingGroup(code));
    }

    @Secured({"GROUP_TABLE_VIEWER"})
    @GetMapping("/search")
    public Page<MeetingGroupDto> search(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "code") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<MeetingGroup> meetingGroups = meetingGroupService.search(pageable, keyword);
        return meetingGroups.map(MeetingGroupMapper.INSTANCE::sourceToDestination);
    }
}
