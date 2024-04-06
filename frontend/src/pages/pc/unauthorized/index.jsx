import React, {useRef, useCallback, useEffect, useState, useMemo} from 'react';
import styles from './style/index.module.less';

export default function Unauthorized(props) {

  return (
    <div className={styles['unauthorized']}>
      Unauthorized
    </div>
  )
}
