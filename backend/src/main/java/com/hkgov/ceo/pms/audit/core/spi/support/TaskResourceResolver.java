package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import com.hkgov.ceo.pms.dto.MeetingWorkspaceDto;
import com.hkgov.ceo.pms.dto.TaskDto;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.hkgov.ceo.pms.config.Constants.NULL_STRING;

/**
 * Implementation of {@link AuditResourceResolver} that show information in format {message: %s message: %s}. <br>
 * %s display Nil if exception is thrown.
 */
@Component
public class TaskResourceResolver implements AuditResourceResolver {

    @Override
    public String[] resolveFrom(final JoinPoint target, final Object returnValue, final Audit audit) {
        String value = NULL_STRING;
        if (returnValue instanceof TaskDto taskDto) {
            value = String.format(audit.resourceWording(),
                    Optional.ofNullable(taskDto.getMeetingWorkspace())
                            .map(MeetingWorkspaceDto::getTitle)
                            .orElse(NULL_STRING), taskDto.getTitle());
        }
        return new String[]{value};
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Audit audit, final Exception exception) {
        return new String[]{String.format(audit.resourceWording(), NULL_STRING)};
    }
}
