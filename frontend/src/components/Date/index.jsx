import React from 'react';
import { Form, DatePicker, TimePicker } from "antd";

const getStyle = (props) => {
  const style = {
    ...props,
  };
  let size = props.size || (props.check && props.check.maxLength) || 0;
  if (props.placeholder && props.placeholder.length > size) {
    size = props.placeholder.length;
  }
  if (size) {
    style.maxWidth = `${(size * 17) + 24}px`;
  }
  return style;
};

function Date (props) {

  const validation = props.validation || [];
  const required = props.required || false;
  const defaultValue = props.defaultValue || '';
  const disabled = props.disabled || false;
  const hidden = props.hidden || false;
  const placeholder = props.placeholder || 'YYYY-MM-DD';
  const noStyle = props.noStyle || false;
  const style = props.style || {};
  const mode = props.mode || 'date';
  const showTime = props.showTime || false;
  const needConfirm = props.needConfirm || false;

  return (
    <Form.Item
      noStyle={noStyle}
      style={{width: '100%'}}
      name={props.name}
      label={props.label}
      rules={[
        { required, message: 'Required'},
        ...validation
      ]}
      hidden={hidden}
    >
      {
        mode === 'date' ? (
          <DatePicker
            showTime={showTime}
            placeholder={placeholder}
            style={getStyle({
              width: '100%',
              ...style,
            })}
            needConfirm={needConfirm}
            defaultValue={defaultValue}
            disabled={disabled}
          />
        ) : (
          <TimePicker
            placeholder={placeholder}
            style={getStyle({
              width: '100%',
              ...style,
            })}
            needConfirm={needConfirm}
            defaultValue={defaultValue}
            disabled={disabled}
          />
        )
      }
    </Form.Item>
  );
}

export default Date;
