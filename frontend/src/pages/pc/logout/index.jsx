import React, {useRef, useCallback, useEffect, useState, useMemo} from 'react';
import styles from './style/index.module.less';

export default function Logout(props) {

  return (
    <div className={styles['unauthorized']}>
      You have been logout.
    </div>
  )
}
