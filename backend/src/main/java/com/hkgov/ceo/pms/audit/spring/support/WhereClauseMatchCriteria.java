package com.hkgov.ceo.pms.audit.spring.support;

import java.util.List;

/**
 * Interface describing match criteria in terms of a SQL select clause.
 */
public interface WhereClauseMatchCriteria {

  /**
   * @return Immutable list of parameter values for a parameterized query or
   * an empty list if the where clause is not parameterized.
   */
  List<?> getParameterValues();
  
  /**
   * @return The where clause text beginning with the string " WHERE" such that
   * the return value can be directly appended to a SQL statement with no
   * where clause.
   */
  @Override
  String toString();

}
