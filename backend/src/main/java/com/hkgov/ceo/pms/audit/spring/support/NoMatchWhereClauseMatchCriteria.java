package com.hkgov.ceo.pms.audit.spring.support;

import java.util.Collections;
import java.util.List;

/**
 * Constructs a where clause that matches no records.
 */
public class NoMatchWhereClauseMatchCriteria extends AbstractWhereClauseMatchCriteria {
  
  public NoMatchWhereClauseMatchCriteria() {
    sbClause.append("WHERE 0=1");
  }

  /** {@inheritDoc} */
  @Override
  public List<?> getParameterValues() {
    return Collections.emptyList();
  }

}
