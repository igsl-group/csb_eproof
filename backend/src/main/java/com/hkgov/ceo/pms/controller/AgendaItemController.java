package com.hkgov.ceo.pms.controller;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.dto.AgendaItemDto;
import com.hkgov.ceo.pms.entity.AgendaItem;
import com.hkgov.ceo.pms.mapper.AgendaItemMapper;
import com.hkgov.ceo.pms.service.AgendaItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agendaItem")
public class AgendaItemController {
    private final AgendaItemService agendaItemService;

    public AgendaItemController(AgendaItemService agendaItemService) {
        this.agendaItemService = agendaItemService;
    }

    @GetMapping("/getAllAgendaItem")
    public Page<AgendaItemDto> getAllAgendaItem(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                @RequestParam(defaultValue = "agendaItemId") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<AgendaItem> departments = agendaItemService.getAll(pageable);
        List<AgendaItemDto> dtoList = departments
                .stream()
                .map(AgendaItemMapper.INSTANCE::sourceToDestination)
                .toList();
        return new PageImpl<>(dtoList, pageable, departments.getTotalElements());
    }

    @Audit(action = "Create", resourceWording = "[Meeting Workspace]: %s [Agenda Item]: %s", resourceResolverName = "agendaItemResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PostMapping("/create")
    public AgendaItemDto create(@RequestBody AgendaItemDto requestDto) {
        AgendaItem agendaItem = agendaItemService.create(requestDto);
        return AgendaItemMapper.INSTANCE.sourceToDestination(agendaItem);
    }

    @Audit(action = "Update", resourceWording = "[Meeting Workspace]: %s [Agenda Item]: %s", resourceResolverName = "agendaItemResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @PatchMapping("/update")
    public AgendaItemDto update(@RequestBody AgendaItemDto requestDto) {
        return AgendaItemMapper.INSTANCE.sourceToDestination(agendaItemService.update(requestDto));
    }

    @Audit(action = "Approve", resourceWording = "[Meeting Workspace]: %s [Agenda Item]: %s", resourceResolverName = "agendaItemResourceResolver")
    @Secured({"APPROVE"})
    @PatchMapping("/approve")
    public AgendaItemDto approve(@RequestParam Long agendaItemId) {
        return AgendaItemMapper.INSTANCE.sourceToDestination(agendaItemService.approve(agendaItemId));
    }

    @Audit(action = "Reject", resourceWording = "[Meeting Workspace]: %s [Agenda Item]: %s", resourceResolverName = "agendaItemResourceResolver")
    @Secured({"APPROVE"})
    @PatchMapping("/reject")
    public AgendaItemDto reject(@RequestParam Long agendaItemId) {
        return AgendaItemMapper.INSTANCE.sourceToDestination(agendaItemService.reject(agendaItemId));
    }

    @Audit(action = "Delete", resourceWording = "[Meeting Workspace]: %s [Agenda Item]: %s", resourceResolverName = "agendaItemResourceResolver")
    @Secured({"MEETING_WORKSPACE_MAINTENANCE"})
    @DeleteMapping("/remove")
    public AgendaItemDto remove(Long agendaItemId) {
        return AgendaItemMapper.INSTANCE.sourceToDestination(agendaItemService.remove(agendaItemId));
    }
}
