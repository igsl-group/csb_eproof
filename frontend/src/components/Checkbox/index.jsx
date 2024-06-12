import React, { useState, useEffect, useMemo, useCallback, useRef  } from 'react';
import { Layout, Button, Dropdown, Space, Form, Checkbox as AntCheckbox } from "antd";
import classNames from "classnames";
import "./style/style.css";

function Checkbox (props) {

  const validation = props.validation || [];
  const required = props.required || false;
  const disabled = props.disabled || false;
  const hidden = props.hidden || false;

  return (
    <Form.Item
      name={props.name}
      valuePropName="checked"
      rules={[
        {
          validator: (_, value) => required && !value? Promise.reject('Required'): Promise.resolve()
        },
        ...validation
      ]}
      hidden={hidden}
    >
      <AntCheckbox disabled={disabled}>
        <b
          className={classNames({
            'ant-checkbox-label': true,
            'ant-form-item-required': required,
          })}

        >{props.label}</b>
      </AntCheckbox>
    </Form.Item>
  );
}

export default Checkbox;
