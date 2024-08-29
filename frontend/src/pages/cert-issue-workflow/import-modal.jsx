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
            name: 'doe',
            inputName: 'Exam Date',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "doe" is not allowed to be empty`,
          },
          {
            name: 'name',
            inputName: 'Name in English',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "name" is not allowed to be empty`,
          },
          {
            name: 'hkid',
            inputName: 'HKID',
            unique: false,
            optional: true,
          },
          {
            name: 'passport',
            inputName: 'Passport No.',
            optional: true,
            dependentValidate: (value, row) => !(!value && !row[4]),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Among the "hkid" and "passport" should be completed at least one field.`,
          },
          {
            name: 'email',
            inputName: 'Email',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "email" is not allowed to be empty`,
            validate: (value) => /^[\w-\.]+@([\w-]+\.)+[\w-]{2,10}$/.test(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "email" format.`,
          },

          {
            name: 'ue_grade',
            inputName: 'UE Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "ue_grade", only allow "L1", "L2", "P", "F" and "".`,
          },
          {
            name: 'uc_grade',
            inputName: 'UC Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "uc_grade", only allow "L1", "L2", "P", "F" and "".`,
          },
          {
            name: 'at_grade',
            inputName: 'AT Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "at_grade", only allow "L1", "L2", "P", "F" and "".`,
          },
          {
            name: 'bl_grade',
            inputName: 'BLNST Grade',
            validate: (value) => ['L1', 'L2', 'P', 'F', ''].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "bl_grade", only allow "L1", "L2", "P", "F" and "".`,
          },
          {
            name: 'lett_type',
            inputName: 'Letter Type',
            validate: (value) => ['P', 'F'].includes(value),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Incorrect "lett_type", only allow "P" and "F".`,
            required: true,
          }
        ]
      })
        .then(csvData => {
          setDataMsg(csvData.inValidData.slice(0, 20))
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
      dataMsg.push({
        message
      })

    },
    onFinally: (params, result, error) => {
    },
  });
  //console.log(dataMsg)
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
      runExamProfileAPI('certIssuanceImport', recordId, values)
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