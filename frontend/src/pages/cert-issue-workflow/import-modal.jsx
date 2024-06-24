import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, List, Grid, Space, Button, Col, Row, Flex, Modal, Alert} from 'antd';
import CSVFileValidator from "csv-file-validator";
import ResizeableTable from "@/components/ResizeableTable";
import {useRequest} from "ahooks";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import { TYPE } from '@/config/enum';
import { examProfileAPI } from '@/api/request';


const ImportModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const file = props.file;
  const open = props.open;
  const recordId = props.recordId;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [dataMsg, setDataMsg] = useState([]);

  const onClose = useCallback(() => {
    if (typeof onCloseCallback === "function") {
      form.resetFields();
      onCloseCallback();
    }
  }, [onCloseCallback]);

  const onFinish = useCallback(() => {
    if (typeof onFinishCallback === "function") {
      form.resetFields();
      onFinishCallback();
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
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "hkid" is not allowed to be empty`,
          },
          // {
          //   name: 'can_serial',
          //   inputName: 'can_serial',
          //   required: true,
          //   requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "can_serial" is not allowed to be empty`,
          // },
          {
            name: 'Name in English ',
            inputName: 'Name in English ',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "name" is not allowed to be empty`,
          },
          // {
          //   name: 'cname',
          //   inputName: 'cname',
          //   optional: true,
          // },
          {
            name: 'HKID',
            inputName: 'HKID',
            unique: false,
            optional: true,
          },
          {
            name: 'Passport No.',
            inputName: 'Passport No.',
            optional: true,
            dependentValidate: (value, row) => !(!value && !row[4]),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Among the "hkid" and "passport" should be completed at least one field.`,
          },
          {
            name: 'Email',
            inputName: 'Email',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "email" is not allowed to be empty`,
          },

          {
            name: 'UE Grade',
            inputName: 'UE Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect UE Grade`,
          },
          {
            name: 'UC Grade',
            inputName: 'UC Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect UC Grade`,
          },
          {
            name: 'AT Grade',
            inputName: 'AT Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect AT Grade`,
          },
          {
            name: 'BL Grade',
            inputName: 'BL Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect BL Grade`,
          },
          {
            name: 'Letter Type (Pass/Fail)',
            inputName: 'Letter Type (Pass/Fail)',
            validate: (value) => ['P', 'F'].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect Letter Type`,
            required: true,
          }
        ]
      })
        .then(csvData => {
          setDataMsg(csvData.inValidData)
        })
        .catch(err => console.error(err))
    }
  }, [file, open]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'certIssuanceImport':
        {
          messageApi.success('Import successfully.');
          break;
        }
        default:
          break;
      }

    },
    onError: (error) => {
      const message = error.data?.properties?.message || '';
      dataMsg.push(message)
      // messageApi.error(message);

    },
    onFinally: (params, result, error) => {
    },
  });

  const onSave = useCallback(async () => {
    // const values = await form.validateFields()
    //   .then((values) => ({
    //     ...values,
    //   }))
    //   .catch((e) => {
    //     console.error(e);
    //     return false;
    //   })
    const values = {
      file,
    }

    if (values) {
      runExamProfileAPI('certIssuanceImport', recordId, '2024-06-12', values)
        .then(() => onFinish());
    }
  }, [recordId, file]);

  return (
    <Modal
      width={800}
      title={'Import CSV'}
      okText={'Import'}
      style={{ top: 20 }}
      closable={false}
      maskClosable={false}
      onCancel={onClose}
      onOk={onSave}
      {...props}
    >
      {
        file && dataMsg.map((row, i) => <List.Item>
          <Alert message={row.message} type="error" showIcon />
        </List.Item>)
      }
      {
        file && dataMsg.length === 0 && <Alert message={'No error find in CSV'} type="success" showIcon />
      }

    </Modal>
  )
}

export default ImportModal;