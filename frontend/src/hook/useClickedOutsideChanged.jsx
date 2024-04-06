import React, { useState, useEffect } from 'react';

export default function useClickedOutsideChanged(ref) {

  const [clickedOutside, setClickedOutside] = useState(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (ref.current && !ref.current.contains(event.target)) {
        setClickedOutside(true);
      } else {
        setClickedOutside(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [ref]);

  return clickedOutside
}