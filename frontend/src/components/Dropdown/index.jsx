import React from 'react';
import { Form, Select } from "antd";

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


function Dropdown(props) {

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
      style={{
        ...getStyle(props),
        marginRight: 50
      }}
      name={props.name}
      label={props.label}
      rules={[
        { required, message: 'Required'},
        ...validation
      ]}
      hidden={hidden}
    >
      <Select onChange={onChange} labelInValue={labelInValue} options={options} mode={mode} allowClear={allowClear}  disabled={disabled} placeholder={placeholder}/>
    </Form.Item>
  );
}

export default Dropdown;
