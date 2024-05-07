package com.hkgov.csb.eproof.audit.spring.support;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Produces a where clause to select audit records older than a given duration.
 */
public class DurationWhereClauseMatchCriteria extends AbstractWhereClauseMatchCriteria {

    private static final String DATE_COLUMN = "AUD_DATE";

    protected String duration;

    public DurationWhereClauseMatchCriteria(final String duration) {
        this.duration = duration;
        addCriteria(DATE_COLUMN, "<");
    }


    @Override
    public List<?> getParameterValues() {
        final Date currentTime = new Date();
        final long newTime = currentTime.getTime() + Duration.parse(duration).toMillis();
        return Collections.singletonList(newTime);
    }
}
