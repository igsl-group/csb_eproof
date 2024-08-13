import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, List, Grid, Space, Button, Col, Row, Flex, Modal, Alert} from 'antd';
import CSVFileValidator from "csv-file-validator";
import ResizeableTable from "@/components/ResizeableTable";
import {useRequest} from "ahooks";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import { TYPE } from '@/config/enum';
import { examProfileAPI } from '@/api/request';
import dayjs from "dayjs";

const ImportModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const file = props.file;
  const open = props.open;
  const recordId = props.recordId;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const record = props.record;
  const [dataMsg, setDataMsg] = useState([]);
  const [result, setResult] = useState([]);

  const onClose = useCallback(() => {
    if (typeof onCloseCallback === "function") {
      form.resetFields();
      onCloseCallback();
    }
  }, [onCloseCallback]);

  const onFinish = useCallback((result) => {
    if (typeof onFinishCallback === "function") {
      form.resetFields();
      onFinishCallback(result);
    }
  }, [onFinishCallback]);

  useEffect(() => {
    if (open) {
      CSVFileValidator(file, {
        headers: [
          {
            name: 'Exam Date',
            inputName: 'Exam Date',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "Exam Date" is not allowed to be empty.`,
            validate: (value) => dayjs(value, 'D/M/YYYY').isSame(dayjs(record?.examProfile?.examDate, 'YYYY-MM-DD'), 'day'),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "Exam Date" is not correct compared with original record.`,

          },
          {
            name: 'Name in English',
            inputName: 'Name in English',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "Name in English" is not allowed to be empty.`,
            validate: (value) => value === record?.name,
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "Name in English" is not correct compared with original record.`,

          },
          {
            name: 'HKID',
            inputName: 'HKID',
            unique: false,
            optional: true,
            validate: (value) => value === record?.hkid,
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "HKID" is not correct compared with original record.`,
          },
          {
            name: 'Passport No.',
            inputName: 'Passport No.',
            optional: true,
            validate: (value) => value === record?.passportNo,
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "Passport No." is not correct compared with original record.`,
          },
          {
            name: 'Email',
            inputName: 'Email',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "Email" is not allowed to be empty`,
            validate: (value) => /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "Email" format.`,
          },

          {
            name: 'UE Grade',
            inputName: 'UE Grade',
            validate: (value) => ['L1', 'L2', 'Pass', 'Fail', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "UE Grade", only allow "L1", "L2", "Pass", "Fail" and "".`,
          },
          {
            name: 'UC Grade',
            inputName: 'UC Grade',
            validate: (value) => ['L1', 'L2', 'Pass', 'Fail', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "UC Grade", only allow "L1", "L2", "Pass", "Fail" and "".`,
          },
          {
            name: 'AT Grade',
            inputName: 'AT Grade',
            validate: (value) => ['L1', 'L2', 'Pass', 'Fail', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "AT grade", only allow "L1", "L2", "Pass", "Fail" and "".`,
          },
          {
            name: 'BLNST Grade',
            inputName: 'BLNST Grade',
            validate: (value) => ['L1', 'L2', 'Pass', 'Fail', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "BLNST Grade", only allow "L1", "L2", "Pass", "Fail" and "".`,
          },
          {
            name: 'Letter Type',
            inputName: 'Letter Type',
            validate: (value) => ['Pass', 'Fail'].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "Letter Type", only allow "Pass" and "Fail".`,
            required: true,
          }
        ]
      })
        .then(csvData => {
          setDataMsg(csvData.inValidData);
          setResult(csvData.data[0])
          if (csvData.inValidData.length === 0 && csvData.data.length > 1) {
            setDataMsg([
              {
                message: 'More than 1 appeal record is not allow'
              }
            ]);
          }

        })
        .catch(err => console.error(err))
    }
  }, [file, open, record]);

  //console.log(dataMsg)
  const onSave = useCallback(async () => {
    onFinish(result);
    console.log(result)
  }, [result]);

  return (
    <Modal
      width={800}
      title={'Import Appeal CSV'}
      okText={'Import'}
      style={{ top: 20 }}
      closable={false}
      maskClosable={false}
      onCancel={onClose}
      okButtonProps={{
        disabled: dataMsg.length > 0,
      }}
      onOk={onSave}
      {...props}
    >
      {
        file && dataMsg.map((row, i) => <List.Item>
          <Alert style={{marginBottom: 8}} message={row.message} type="error" showIcon />
        </List.Item>)
      }
      {
        file && dataMsg.length === 0 && <Alert message={'No error find in CSV'} type="success" showIcon />
      }

    </Modal>
  )
}

export default ImportModal;