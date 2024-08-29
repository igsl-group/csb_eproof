import React, { useState, useEffect, useRef } from 'react';
import {Input, Form, Button, Space, Select, InputNumber, Row, Col} from "antd";
import _ from "lodash";

export const HKIDToString = (hkid = {}) => {
  if (hkid.id && hkid.checkDigit) {
    return `${hkid.id}${hkid.checkDigit}`
  }
  return '';
}

export const stringToHKID = (hkid = "") => {
  let _hkid = hkid || "";
  _hkid = _hkid.replaceAll(/\(|\)/g, '')

  return {
    id: _hkid.substring(0,  _hkid.length - 1),
    checkDigit: _hkid.charAt(_hkid.length - 1)
  }
}

export const stringToHKIDWithBracket = (hkid = "") => {

  let _hkid = hkid || "";
  _hkid = _hkid.replaceAll(/\(|\)/g, '')
  const id = _hkid.substring(0,  _hkid.length - 1);
  const checkDigit = _hkid.charAt(_hkid.length - 1);
  return hkid ? `${id}(${checkDigit})` : ''
}

const checkHKIDCheckDigit = (id, checkdigit) => {
  let valid = false;
  const hkidPattern = /^([A-Z]{1,2})([0-9]{6})$/;
  if (!_.isEmpty(id) && !_.isEmpty(checkdigit)) {
    const matchArray = id.toUpperCase().match(hkidPattern);
    if (matchArray === null) {
      valid = false;
    } else {
      const charPart = matchArray[1];
      const numPart = matchArray[2];
      // calculate the checksum for character part
      const strValidChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
      let checkSum = 0;
      if (charPart.length === 2) {
        checkSum += 9 * (10 + strValidChars.indexOf(charPart.charAt(0)));
        checkSum += 8 * (10 + strValidChars.indexOf(charPart.charAt(1)));
      } else {
        checkSum += 9 * 36;
        checkSum += 8 * (10 + strValidChars.indexOf(charPart));
      }

      // calculate the checksum for numeric part
      for (let i = 0, j = 7; i < numPart.length; i += 1, j -= 1) {
        checkSum += j * numPart.charAt(i);
      }

      // verify the check digit
      const remaining = checkSum % 11;
      const verify = remaining === 0 ? 0 : 11 - remaining;
      valid = (verify.toString() === checkdigit || (verify === 10 && checkdigit === 'A'));
    }
  }
  return valid;
};


const getStyle = (props) => {
  const style = {};
  let size = props.size || (props.check && props.check.maxLength) || 0;
  if (props.placeholder && props.placeholder.length > size) {
    size = props.placeholder.length;
  }
  if (size) {
    style.maxWidth = `${(size * 17) + 24}px`;
  }

  return style;
};

function HKID (props) {

  const required = props.required || false;
  const disabled = props.disabled || false;;
  const label = props.label || 'Hong Kong Identity Card (HKIC) No.';
  const name = props.name || 'hkid';
  const hidden = props.hidden;
  const [validateStatus, setValidateStatus] = useState('success');
  const [help, setHelp] = useState('');

  return (
    <div>
      <Form.Item
        label={label}
        required={required}
        hidden={hidden}
        style={{ marginBottom: 0, marginRight: 0 }}
      >
        <Row>
          <Col>
            <Form.Item
              name={[name, 'id']}
              validateStatus={validateStatus}
              help={help}

              rules={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const checkDigit = getFieldValue(name)?.checkDigit;
                    //To DO
                    return Promise.resolve();
                    if ((value || checkDigit) && !checkHKIDCheckDigit(value, checkDigit)) {
                      setValidateStatus('error');
                      setHelp('Invalid HKID number');
                      return Promise.reject(new Error(""));
                    } else if (required && (!value || !checkDigit)) {
                      setValidateStatus('error');
                      setHelp('Required');
                      return Promise.reject(new Error(""));
                    } else {
                      setValidateStatus('success');
                      setHelp('');
                    }
                    return Promise.resolve();
                  },
                }),
              ]}
            >
              <Input
                disabled={disabled}
                maxLength={7}
                style={{
                  width: 130,
                  textTransform: 'uppercase'
                }}

              />
            </Form.Item>
          </Col>
          <Col>
            <span style={{ lineHeight: '32px'}} >&nbsp;&nbsp;(&nbsp;&nbsp;</span>
          </Col>
          <Col>
            <Form.Item
              name={[name, 'checkDigit']}
              validateStatus={validateStatus}
              rules={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const id = getFieldValue(name)?.id;
                    //To DO
                    return Promise.resolve();
                    if ((value || id) && !checkHKIDCheckDigit(id, value)) {
                      // return Promise.reject(new Error(""));
                      setValidateStatus('error');
                      setHelp('Invalid HKID number');
                    } else if (required && (!value || !id)) {
                      setValidateStatus('error');
                      setHelp('Required');
                      // return Promise.reject(new Error(""));
                    } else {
                      setValidateStatus('success');
                      setHelp('');
                    }
                    return Promise.resolve();
                  },
                }),
              ]}
            >
              <Input
                disabled={disabled}
                maxLength={1}
                style={{
                  width: 35,
                  textTransform: 'uppercase',
                  textAlign: 'center'
                }}
              />
            </Form.Item>
          </Col>
          <Col>
            <span style={{lineHeight: '32px'}}>&nbsp;&nbsp;)</span>
          </Col>
        </Row>

      </Form.Item>

    </div>
  );
}

export default HKID;
