import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Grid,
  Space,
  Button,
  Col,
  Row,
  Flex,
  Modal,
  Alert,
  Upload
} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";
import Checkbox from "@/components/Checkbox";
import {validators} from "../../utils/validators";
import Richtext from "../../components/Richtext";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import ImportModal from "./import-modal";
import {useRequest} from "ahooks";
import { examProfileAPI } from '@/api/request';

const PersonalParticularsModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const informCandidateValue = Form.useWatch('informCandidate', form);
  const [file, setFile] = useState(null);
  const [openImportModal, setOpenImportModal] = useState(null);
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [dataMsg, setDataMsg] = useState([]);
  const open = props.open;
  const record = props.record;

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

  const onImportModalCloseCallback = useCallback(() => {
    setOpenImportModal(false)
  }, []);

  const onImportModalFinishCallback = useCallback(async (result) => {
    console.log(result);
    form.setFieldsValue({
      newUeGrade: result?.['UE Grade'],
      newUcGrade: result?.['UC Grade'],
      newAtGrade: result?.['AT Grade'],
      newBlnstGrade: result?.['BL Grade'],
    })
    setOpenImportModal(false);
  }, []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'certLatestCandidateInfo':
          const data = response.data || {};
          const content = data.content || [];

          break;
        default:
          break;
      }
    },
    onError: (error) => {
      const message = error.data?.properties?.message || '';
      messageApi.error(message);
    },
    onFinally: (params, result, error) => {
    },
  });

  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => ({
        ...values,
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })
    console.log(values)
    if (values) {
      delete values.atGrade;
      delete values.blnstGrade;
      delete values.ucGrade;
      delete values.ueGrade;
      delete values.hkid;
      delete values.name;
      delete values.examDate;
      delete values.passportNo;
      runExamProfileAPI('certSingleUpdateResult', record.id, values)
        .then(() => onFinish());

    }
  }, [record]);

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        examDate: record?.examProfile?.examDate,
        name: record?.name,
        hkid: record?.hkid,
        passportNo: record?.passportNo,
        ueGrade: record?.ueGrade,
        ucGrade: record?.ucGrade,
        atGrade: record?.atGrade,
        blnstGrade: record?.blnstGrade,
      })
    }
  }, [open]);

  console.log(record)

  return (
    <Modal
      width={1000}
      okText={'Submit for renew'}
      closable={false}
      onOk={onSave}
      onCancel={onClose}
      style={{ top: 20 }}
      {...props}
    >
      <Form
        layout="vertical"
        autoComplete="off"
        form={form}
        colon={false}
        scrollToFirstError={{
          behavior: 'smooth',
          block: 'center',
          inline: 'center',
        }}
        name="form"
      >


        <br/>
        <Row gutter={24} justify={'end'}>
          <Col>
            <Button
              type="primary"
              onClick={() => {
                window.open('/import_csv_template_(appeal).csv', 'Download');
              }}
            >
              Download Template (CSV)
            </Button>
          </Col>
          <Col>
            <Upload
              accept={'text/csv'}
              multiple={false}
              maxCount={1}
              showUploadList={false}
              beforeUpload={async (file) => {
                if (file.type !== 'text/csv') {
                  messageApi.error(`${file.name} is not a csv file`);
                } else {
                  setOpenImportModal(true);
                  setFile(file);
                }
                return false;
              }}
            >
              <Button type="primary" onClick={() => {
              }}>Import Result (CSV)</Button>
            </Upload>
          </Col>
        </Row>
        <br/>
        <Row gutter={24} justify={'center'}>
          <Col span={24} md={12}>
            <Text name={'name'} label={'Candidate Name'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'examDate'} label={'ExamDate'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'hkid'} label={'HKID'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'passportNo'} label={'Passport'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'ueGrade'} label={'Current UE'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newUeGrade'} label={'New UE'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'ucGrade'} label={'Current UC'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newUcGrade'} label={'New UC'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'atGrade'} label={'Current AT'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newAtGrade'} label={'New AT'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'blnstGrade'} label={'Current BLNST'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newBlnstGrade'} label={'New BLNST'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'letterType'} label={'Old Letter Template'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newLetterType'} label={'New BLNST'} size={100} disabled/>
          </Col>
          <Col span={24}>
            <Textarea name={'remark'} label={'Remark'} size={100}/>
          </Col>
          {/*<Col span={24}>*/}
          {/*  <Richtext name={'informCandidateEmail'} label={'Inform candidate\'s email content after processing success'} size={100}/>*/}
          {/*</Col>*/}
        </Row>
        {/*<br />*/}
        <Alert type={'warning'} message={'Cert. renew required after submit.'} showIcon/>
      </Form>
      <ImportModal
        file={file}
        record={record}
        open={openImportModal}
        onCloseCallback={onImportModalCloseCallback}
        onFinishCallback={onImportModalFinishCallback}
      />
    </Modal>
  )
}

export default PersonalParticularsModal;