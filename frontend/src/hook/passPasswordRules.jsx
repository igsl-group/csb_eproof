import React, { useState, useEffect } from 'react';

export default function PassPasswordRules(password) {

  const [passPasswordRules, setPassPasswordRules] = useState({
    atLeastChars: false,
    include3following: false
  });

  useEffect(() => {
    if (password === '') {
      setPassPasswordRules({
        atLeastChars: false,
        include3following: false
      })
    } else {
      let pass = {}
      if (password.length >= 10) {
        pass.atLeastChars = true;
      }
      let counter = 0;
      if (/[0-9]/.test(password)) {
        counter++;
      }
      if (/[a-z]/.test(password)) {
        counter++;
      }
      if (/[A-Z]/.test(password)) {
        counter++;
      }
      if (/\W/.test(password)) {
        counter++;
      }
      if (counter >= 3) {
        pass.include3following = true;
      }
      setPassPasswordRules(pass)
    }
  }, [password]);

  return passPasswordRules
}