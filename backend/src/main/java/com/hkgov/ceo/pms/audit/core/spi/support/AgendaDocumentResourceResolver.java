package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import com.hkgov.ceo.pms.dto.AgendaItemDto;
import com.hkgov.ceo.pms.dto.MeetingWorkspaceDto;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.hkgov.ceo.pms.config.Constants.NULL_STRING;

/**
 * Implementation of {@link AuditResourceResolver} that show information in format {message: %s message: %s message %s}. <br>
 * %s display Nil if exception is thrown.
 */
@Component
public class AgendaDocumentResourceResolver implements AuditResourceResolver {
    @Override
    public String[] resolveFrom(final JoinPoint target, final Object returnValue, final Audit audit) {
        String value = NULL_STRING;
        if (returnValue instanceof AgendaItemDto dto) {
            Object lastElement = target.getArgs()[target.getArgs().length - 1];
            if (lastElement instanceof MultipartFile multipart) {
                value = String.format(audit.resourceWording(), getMeetingTitle(dto), dto.getTitle(), multipart.getOriginalFilename());
            } else if (lastElement instanceof String path) {
                String[] parts = path.split("/");
                String fileName = parts[parts.length - 1];
                value = String.format(audit.resourceWording(), getMeetingTitle(dto), dto.getTitle(), fileName);
            }
        }
        return new String[]{value};
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Audit audit, final Exception exception) {
        return new String[]{String.format(audit.resourceWording(), NULL_STRING, NULL_STRING, NULL_STRING)};
    }

    private static String getMeetingTitle(AgendaItemDto agendaItemDto) {
        return Optional.ofNullable(agendaItemDto.getMeetingWorkspace())
                .map(MeetingWorkspaceDto::getTitle)
                .orElse(NULL_STRING);
    }
}
