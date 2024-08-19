import React, { useMemo } from 'react';
import { Form, Input } from "antd";
import format from "../../utils/string-template";
import {
  BulbOutlined
} from '@ant-design/icons';

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

function Email (props) {

  const validation = props.validation || [];
  const required = props.required || false;
  const disabled = props.disabled || false;
  const hidden = props.hidden || false;
  const max = 250;
  const placeholder = props.placeholder || '';
  const message = props.message || '';

  const extra  = useMemo(() => {
    if (!message) {
      return null;
    }
    return (
      <div style={{display: "flex"}}>
        <div><BulbOutlined style={{color: "#43B8E2"}}/></div>
        <div style={{color: "#43B8E2", marginLeft: "8px", textAlign: "justify"}}><span>{message}</span>
        </div>
      </div>
    )
  }, [message])

  return (
    <Form.Item
      style={{
        ...getStyle(props),
        marginRight: 50
      }}
      name={props.name}
      label={props.label}
      hidden={hidden}
      extra={extra}
      rules={[
        {
          required,
        },
        {
          validator: async (_, value) => {
            if (value && !/^[\w-\.]+@([\w-]+\.)+[\w-]{2,10}$/.test(value)) {
              return Promise.reject(new Error('Incorrect email format'));
            }  else if (value && value.toString().length > max) {
              return Promise.reject(new Error(format('Should not be more than {0} characters', max)));
            }
            return Promise.resolve();
          },
        },
        ...validation
      ]}
    >
      <Input disabled={disabled} placeholder={placeholder}/>
    </Form.Item>
  );
}

export default Email;
