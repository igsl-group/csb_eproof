import React, { useState, useEffect } from 'react';

export default function IsMobile() {
  const mobileMaxWidth = 900;
  const [isMobile, setIsMobile] = useState(false);
  const handleWindowSizeChange = () => {
    if (window.navigator.userAgent.indexOf("Windows NT")) {
      if (window.devicePixelRatio > 1.5) {
        setIsMobile(window.innerWidth * Number(window.devicePixelRatio/ 1.5).toFixed(5) < mobileMaxWidth)

      } else {
        setIsMobile(window.innerWidth < mobileMaxWidth)
      }
    } else {
      setIsMobile(window.innerWidth < mobileMaxWidth)
    }


  };

  useEffect(() => {
    window.addEventListener('resize', handleWindowSizeChange);
    return () => {
      window.removeEventListener('resize', handleWindowSizeChange);
    };
  }, []);

  return isMobile;
}