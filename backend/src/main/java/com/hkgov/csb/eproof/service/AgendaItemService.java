package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.AgendaItemDto;
import com.hkgov.csb.eproof.entity.AgendaItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgendaItemService {
    Page<AgendaItem> getAll(Pageable pageable);

    AgendaItem get(Long id);

    AgendaItem create(AgendaItemDto request);

    AgendaItem update(AgendaItemDto request);

    AgendaItem approve(Long id);

    AgendaItem reject(Long id);

    AgendaItem remove(Long id);

    AgendaItem getAgendaItemById(Long id);
}
