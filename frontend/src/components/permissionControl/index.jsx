import React, { useMemo } from 'react';

export const PermissionControl = (props) => {
  const {
    permissionRequired = '',
    forceHidden = false,
    log,
  } = props;

  const permissionArr = useMemo(() => {
    if (typeof permissionRequired === 'string' && permissionRequired !== '') {
      return permissionRequired.split(',')
    } else if (Array.isArray(permissionRequired)) {
      return permissionRequired;
    }
    return [];
  }, [permissionRequired]);

  const userHasPermissions = useMemo(() => {
    try {
      return JSON.parse(sessionStorage.getItem('permissions'))
        .flatMap((permission) => permission.code)
    } catch(e) {
      return [];
    }
  }, []);

  const hasPermission = useMemo(() => {
    let flag = true;
    if (permissionArr.length > 0) {
      permissionArr.forEach((permission) => {
        if (!userHasPermissions.includes(permission)) {
          flag = false;
        }
      })
    }
    return flag;
  }, [permissionArr, userHasPermissions]);

  return (
    <div>
      { hasPermission && !forceHidden ? props.children : (<div />)}
    </div>
  );
}

