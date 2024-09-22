import React from 'react';
import { Form, Switch as AndSwitch} from "antd";

const getStyle = (props) => {
  const style = {};
  let size = props.size || (props.check && props.check.maxLength) || 15;
  if (props.placeholder && props.placeholder.length > size) {
    size = props.placeholder.length;
  }

  if (size) {
    style.maxWidth = `${(size * 17) + 24}px`;
    style.width = `100%`;
  }

  return style;
};


function Switch(props) {

  const validation = props.validation || [];
  const required = props.required || false;
  const disabled = props.disabled || false;
  const labelInValue = props.labelInValue || false;
  const hidden = props.hidden || false;
  const placeholder = props.placeholder || 'Please choose ...';
  const options = props.options || [];
  const mode = props.mode || 'combobox';
  const onChange = props.onChange || (() => {});
  const allowClear = props.allowClear !== false;

  return (
    <Form.Item
      name={props.name}
      label={props.label}
      hidden={hidden}
      valuePropName="checked"
    >
      <AndSwitch disabled={disabled}/>
    </Form.Item>
  );
}

export default Switch;
