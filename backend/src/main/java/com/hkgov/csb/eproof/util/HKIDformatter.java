package com.hkgov.csb.eproof.util;

import org.springframework.stereotype.Component;

@Component
public class HKIDformatter {

  public static String formatHkid(String hkid) {
    if (hkid != null && hkid.length() == 8) {
      return hkid.substring(0, 1) + " " +hkid.substring(1, 7) + "(" + hkid.charAt(7) + ")";
    }
    return hkid;
  }
}
