package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.dao.AgendaItemRepository;
import com.hkgov.ceo.pms.dto.AgendaItemDto;
import com.hkgov.ceo.pms.dto.MeetingWorkspaceDto;
import com.hkgov.ceo.pms.entity.AgendaItem;
import com.hkgov.ceo.pms.entity.enums.Status;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.mapper.AgendaItemMapper;
import com.hkgov.ceo.pms.service.AgendaItemService;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import com.hkgov.ceo.pms.service.MeetingWorkspaceService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.AGENDA_ITEM_NOT_FOUND_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.AGENDA_ITEM_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@Transactional
public class AgendaItemServiceImpl implements AgendaItemService {
    private final AgendaItemRepository agendaItemRepository;
    private final MeetingWorkspaceService meetingWorkspaceService;
    private final AuthenticatedInfoService authenticatedInfoService;

    public AgendaItemServiceImpl(AgendaItemRepository agendaItemRepository, MeetingWorkspaceService meetingWorkspaceService, AuthenticatedInfoService authenticatedInfoService) {
        this.agendaItemRepository = agendaItemRepository;
        this.meetingWorkspaceService = meetingWorkspaceService;
        this.authenticatedInfoService = authenticatedInfoService;
    }

    @Override
    public Page<AgendaItem> getAll(Pageable pageable) {
        return agendaItemRepository.findAll(pageable);
    }

    @Override
    public AgendaItem get(Long id) {
        return getAgendaItemById(id);
    }

    @Override
    public AgendaItem create(AgendaItemDto request) {
        AgendaItem agendaItem = AgendaItemMapper.INSTANCE.destinationToSource(request);
        meetingWorkspaceService.freezeValidation(Optional.ofNullable(request)
                .map(AgendaItemDto::getMeetingWorkspace)
                .map(MeetingWorkspaceDto::getMeetingWorkspaceId)
                .orElse(null));
        agendaItem.setMeetingWorkspace(meetingWorkspaceService.getMeetingWorkspaceById(Optional.ofNullable(request)
                .map(AgendaItemDto::getMeetingWorkspace)
                .map(MeetingWorkspaceDto::getMeetingWorkspaceId)
                .orElse(null)));
        agendaItem.setPreparedBy(authenticatedInfoService.getCurrentUser());
        agendaItem.setStatus(Status.PENDING);
        agendaItemRepository.save(agendaItem);
        return agendaItem;
    }

    @Override
    public AgendaItem update(AgendaItemDto request) {
        AgendaItem agendaItem = getAgendaItemById(request.getAgendaItemId());
        meetingWorkspaceService.freezeValidation(agendaItem.getMeetingWorkspace().getMeetingWorkspaceId());
        AgendaItemMapper.INSTANCE.updateFromDto(request, agendaItem);
        return agendaItem;
    }

    @Override
    public AgendaItem approve(Long id) {
        AgendaItem agendaItem = getAgendaItemById(id);
        meetingWorkspaceService.freezeValidation(agendaItem.getMeetingWorkspace().getMeetingWorkspaceId());
        agendaItem.setApprovedBy(authenticatedInfoService.getCurrentUser());
        agendaItem.setStatus(Status.APPROVED);
        return agendaItem;
    }

    @Override
    public AgendaItem reject(Long id) {
        AgendaItem agendaItem = getAgendaItemById(id);
        meetingWorkspaceService.freezeValidation(agendaItem.getMeetingWorkspace().getMeetingWorkspaceId());
        agendaItem.setRejectedBy(authenticatedInfoService.getCurrentUser());
        agendaItem.setStatus(Status.REJECTED);
        return agendaItem;
    }

    @Override
    public AgendaItem remove(Long id) {
        AgendaItem agendaItem = getAgendaItemById(id);
        meetingWorkspaceService.freezeValidation(agendaItem.getMeetingWorkspace().getMeetingWorkspaceId());
        agendaItemRepository.delete(agendaItem);
        return agendaItem;
    }

    @Override
    public AgendaItem getAgendaItemById(Long id) {
        return Optional.ofNullable(agendaItemRepository.findByAgendaItemId(id))
                .orElseThrow(() -> new GenericException(AGENDA_ITEM_NOT_FOUND_EXCEPTION_CODE, AGENDA_ITEM_NOT_FOUND_EXCEPTION_MESSAGE));
    }

}
