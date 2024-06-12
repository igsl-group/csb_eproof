import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, List, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import CSVFileValidator from "csv-file-validator";
import ResizeableTable from "@/components/ResizeableTable";

const ImportModal = (props) =>  {

  const [form] = Form.useForm();
  const file = props.file;
  const open = props.open;
  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, []);
  const [data, setData] = useState([]);
  const [dataMsg, setDataMsg] = useState([]);

  useEffect(() => {
    if (open) {
      CSVFileValidator(file, {
        headers: [
          {
            name: 'exam_date',
            inputName: 'exam_date',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "hkid" is not allowed to be empty`,
          },
          {
            name: 'can_serial',
            inputName: 'can_serial',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "can_serial" is not allowed to be empty`,
          },
          {
            name: 'name',
            inputName: 'name',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "name" is not allowed to be empty`,
          },
          {
            name: 'cname',
            inputName: 'cname',
            optional: true,
          },
          {
            name: 'hkid',
            inputName: 'hkid',
            unique: false,
            optional: true,
          },
          {
            name: 'passport',
            inputName: 'passport',
            optional: true,
            dependentValidate: (value, row) => !(!value && !row[4]),
            validateError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: Among the "hkid" and "passport" should be completed at least one field.`,
          },
          {
            name: 'email',
            inputName: 'email',
            required: true,
            requiredError: (headerName, rowNumber, columnNumber) => `Row: ${rowNumber}: "email" is not allowed to be empty`,
          },
          {
            name: 'bl_grade',
            inputName: 'bl_grade',
            required: true,
          },
          {
            name: 'ue_grade',
            inputName: 'ue_grade',
            required: true,
          },
          {
            name: 'uc_grade',
            inputName: 'uc_grade',
            required: true,
          },
          {
            name: 'at_grade',
            inputName: 'at_grade',
            required: true,
          },
          {
            name: 'remark',
            inputName: 'remark',
            optional: true,
          }
        ]
      })
        .then(csvData => {
          setDataMsg(csvData.inValidData)
        })
        .catch(err => console.error(err))
    }
  }, [file, open]);

  return (
    <Modal
      width={800}
      title={'Import CSV'}
      okText={'Save'}
      closable={false}
      onCancel={onClose}
      {...props}
    >
      {
        dataMsg.map((row, i) => <List.Item>{row.message}</List.Item>)
      }

    </Modal>
  )
}

export default ImportModal;