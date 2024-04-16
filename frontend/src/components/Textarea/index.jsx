import React from 'react';
import { Form, Input, AutoComplete } from "antd";

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

function Textarea (props) {

  const validation = props.validation || [];
  const required = props.required || false;
  const disabled = props.disabled || false;
  const hidden = props.hidden || false;
  const row = props.row || 4;
  const placeholder = props.placeholder || false;

  return (
    <Form.Item
      style={{
        ...getStyle(props),
      }}
      name={props.name}
      label={props.label}
      rules={[
        { required, message: 'Required'},
        ...validation
      ]}
      hidden={hidden}
    >
      <Input.TextArea rows={row} disabled={disabled} placeholder={placeholder}/>
    </Form.Item>
  );
}

export default Textarea;
