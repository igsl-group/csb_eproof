package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.AuditTrailManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns the parameters as an array of strings.
 */
@Component
public class ParametersAsStringResourceResolver extends AbstractAuditResourceResolver {

    @Override
    protected String[] createResource(final Object[] args) {
        final List<String> stringArgs = new ArrayList<String>();

        for (final var arg : args) {
            stringArgs.add(toResourceString(arg));
        }

        return stringArgs.toArray(new String[stringArgs.size()]);
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return AuditTrailManager.toJson(arg);
        }
        return arg.toString();
    }
}
