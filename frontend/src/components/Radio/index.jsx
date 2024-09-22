import React from 'react';
import { Form, Radio as AntRadio } from "antd";
import "./style/style.css";

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


function Radio(props) {

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
  const optionType = props.optionType || 'default ';
  const labelWidth = props.labelWidth;

  return (
    <Form.Item
      name={props.name}
      label={props.label}
      rules={[
        { required, message: 'Required'},
        ...validation
      ]}
      hidden={hidden}
    >
      <AntRadio.Group
        style={{ width: '100'}}
        disabled={disabled}
        options={options.flatMap((row) => ({...row, style: { width: labelWidth }}))}
        optionType={optionType}
        buttonStyle="solid"
        onChange={onChange}
      />
    </Form.Item>
  );
}

export default Radio;
