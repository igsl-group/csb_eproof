import React, { useMemo } from 'react';

export default function usePermission() {

  const userHasPermissions = useMemo(() => {
    
    try {
      return JSON.parse(sessionStorage.getItem('permissions'))
        .flatMap((permission) => permission.code)
    } catch (e) {
      return [];
    }
  }, []);

  return userHasPermissions;
}