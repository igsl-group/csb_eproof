package com.hkgov.csb.eproof.audit.spring.support;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Produces a where clause to select audit records older than some arbitrary
 * cutoff age in days.
 */
public class MaxAgeWhereClauseMatchCriteria extends AbstractWhereClauseMatchCriteria {
 
  /** Name of creation date column name in audit record table */
  private static final String DATE_COLUMN = "AUD_DATE";

  /** Maximum age of records */
  protected int maxAge;


  /**
   * Creates a new instance that selects audit records older than the given
   * number of days as measured from the present time.
   *
   * @param maxAgeDays Cutoff age of records in days.
   */
  public MaxAgeWhereClauseMatchCriteria(final int maxAgeDays) {
    this.maxAge = maxAgeDays;
    addCriteria(DATE_COLUMN, "<");
  }
  
 
  /** {@inheritDoc} */
  @Override
  public List<?> getParameterValues() {
    final var cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -this.maxAge);
    return Collections.singletonList(cal.getTime());
  }
}
