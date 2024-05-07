package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.AgendaItemRepository;
import com.hkgov.csb.eproof.dto.AgendaItemDto;
import com.hkgov.csb.eproof.dto.MeetingWorkspaceDto;
import com.hkgov.csb.eproof.entity.AgendaItem;
import com.hkgov.csb.eproof.entity.enums.Status;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.AgendaItemMapper;
import com.hkgov.csb.eproof.service.AgendaItemService;
import com.hkgov.csb.eproof.service.AuthenticatedInfoService;
import com.hkgov.csb.eproof.service.MeetingWorkspaceService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.AGENDA_ITEM_NOT_FOUND_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.AGENDA_ITEM_NOT_FOUND_EXCEPTION_MESSAGE;

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
