import React from 'react';
import {Button as AntdButton} from "antd";

function Button (props) {

  const hidden = props.hidden || false;
  return !hidden ? <AntdButton {...props}>{props.children}</AntdButton> : <div />;
}

export default Button;
