import React, { useMemo } from 'react';
import {useAuth} from "../../context/auth-provider";
import Button from "../Button";


const PermissionControl = (props) => {
  const {
    permissionRequired = '',
    forceHidden = false,
    log,
  } = props;

  const auth = useAuth();

  const permissionArr = useMemo(() => {
    if (typeof permissionRequired === 'string' && permissionRequired !== '') {
      return permissionRequired.split(',')
    } else if (Array.isArray(permissionRequired)) {
      return permissionRequired;
    }
    return [];
  }, [permissionRequired]);

  const userHasPermissions = useMemo(() => auth.permissions, [auth]);

  const hasPermission = useMemo(() => {
    let flag = false;
    if (permissionArr.length > 0) {
      permissionArr.forEach((permission) => {
        if (userHasPermissions.includes(permission)) {
          flag = true;
        }
      })
    }
    return flag;
  }, [permissionArr, userHasPermissions]);

  return (hasPermission && !forceHidden ? props.children : null);
}

export default PermissionControl;

