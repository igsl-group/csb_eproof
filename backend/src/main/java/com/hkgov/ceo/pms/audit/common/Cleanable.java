package com.hkgov.ceo.pms.audit.common;

/**
 * Describes a resource that supports purging auditing, statistics, or
 * error data that meets arbitrary criteria.
 */
public interface Cleanable {
 
  /**
   * Purges records meeting arbitrary criteria defined by implementers.
   */
  void clean();

}
