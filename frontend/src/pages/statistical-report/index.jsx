import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Watermark,
  Grid,
  Space,
  Tabs,
  Col,
  Row,
  Descriptions,
  Modal,
  Pagination,
  Button, Alert
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined, SearchOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";
// import Import from "./import";
// import Generate from "./generate";
// import Issue from "./issue";
// import Notify from "./notify";
import dayjs from "dayjs";
// import ExceptionalCaseModal from "./exceptional-case-modal";
import {useModal} from "../../context/modal-provider";
import { reportAPI } from "../../api/request";
import {useMessage} from "../../context/message-provider";
import {download} from "../../utils/util";
import {HKIDToString} from "../../components/HKID";
// import ExamProfileFormModal from "./modal";

const StatisticalReports = () =>  {

  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();
  const [record, setRecord] = useState({});

  const onFinish = useCallback(() => {
    form.resetFields();
    setOpen(false);
    setRecord({});
  }, []);

  const disabledFutureDate = useCallback((current) => {
    return current && current > dayjs().endOf('day');
  }, []);

  const [data, setData] = useState([
    {
      type: 'Details of Certificate(s) with Personal Particulars Updated',
      id: '6',
      formFields: (
        <Row gutter={24} justify={'start'}>
          <Col span={12}>
            <Date
              name={'start'}
              label={'From'}
              required
              disabledDate={disabledFutureDate}
            />
          </Col>
          <Col span={12}>
            <Date
              name={'end'}
              label={'To'}
              required
              dependencies={['start']}
              disabledDate={disabledFutureDate}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const startDate = getFieldValue('start');
                    const isSameOrAfter = value.isSame(startDate, 'day') || value.isAfter(startDate, 'day');
                    if (!value || isSameOrAfter) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('The "To" should be no earlier than the "From".'));
                  },
                }),
              ]}
            />
          </Col>
          <Col span={12}>
            <Text name={'candidateName'} label={'Candidate Name'}/>
          </Col>
          <Col span={12}>
          </Col>
          <Col span={12}>
            <HKID name={'hkidNumber'} label={'HKID'}/>
          </Col>
          <Col span={12}>
            <Text name={'passportNumber'} label={'Passport'}/>
          </Col>
        </Row>
      )
    },
    {
      type: 'Details of Certificate(s) with Results Updated',
      id: '7',
      formFields: (
        <Row gutter={24} justify={'start'}>
          <Col span={12}>
            <Date
              name={'start'}
              label={'From'}
              required
              disabledDate={disabledFutureDate}
            />
          </Col>
          <Col span={12}>
            <Date
              name={'end'}
              label={'To'}
              required
              disabledDate={disabledFutureDate}
              dependencies={['start']}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const startDate = getFieldValue('start');
                    const isSameOrAfter = value.isSame(startDate, 'day') || value.isAfter(startDate, 'day');
                    if (!value || isSameOrAfter) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('The "To" should be no earlier than the "From".'));
                  },
                }),
              ]}
            />
          </Col>
          <Col span={12}>
            <Text name={'candidateName'} label={'Candidate Name'}/>
          </Col>
          <Col span={12}>
          </Col>
          <Col span={12}>
            <HKID name={'hkidNumber'} label={'HKID'}/>
          </Col>
          <Col span={12}>
            <Text name={'passportNumber'} label={'Passport'}/>
          </Col>
        </Row>
      )
    },
    {
      type: 'Statistics of Examination Results (by Examination Profile)',
      id: '9',
      formFields: (
        <Row gutter={24} justify={'start'}>
          <Col span={12}>
            <Date
              name={'start'}
              label={'From'}
              required
              disabledDate={disabledFutureDate}
            />
          </Col>
          <Col span={12}>
            <Date
              required
              name={'end'}
              label={'To'}
              disabledDate={disabledFutureDate}
              dependencies={['start']}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const startDate = getFieldValue('start');
                    const isSameOrAfter = value.isSame(startDate, 'day') || value.isAfter(startDate, 'day');
                    if (!value || isSameOrAfter) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('The "To" should be no earlier than the "From".'));
                  },
                }),
              ]}
            />
          </Col>
        </Row>
      )
    },
    {
      type: 'Statistics of Examination Results (by Year)',
      id: '10',
      formFields: (
        <Row gutter={24} justify={'start'}>
          <Col span={24}>
            <Dropdown
              required
              name={'year'}
              label={'Year'}
              size={50}
              options={(() => {
                const startYear = 2022;
                const currentYear = dayjs().year();
                return Array.from({ length: currentYear - startYear + 1 }, (_, i) => ({ value: `${currentYear - i}`, label: `${currentYear - i}` }));
              })()}
            />
          </Col>
        </Row>
      )
    }
  ]);

  const onExportButtonClick = useCallback((row) => {
    setOpen(true);
    setRecord(row);
  }, [])

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 140,
      render: (row) => (
        <Row gutter={[8, 8]}>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => onExportButtonClick(row)}>Export</Button></Col>
        </Row>
      )
    },
    {
      title: 'Type',
      key: 'type',
      dataIndex: 'type',
      sorter: false,
    },
  ], []);

  const onCloseCallback = useCallback(() => {
    form.resetFields();
    setOpen(false);
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Statistical Reports',
    },
  ], []);

  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => ({
        year: "",
        examSerialNumber: "",
        ...values,
        start: values.start ? dayjs(values.start).format('YYYY-MM-DD') : '',
        end: values.end ? dayjs(values.end).format('YYYY-MM-DD') : '',
        hkidNumber: HKIDToString(values.hkidNumber),
        reportType: record.id,
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      runReportAPI('exportReport', values)
        .then(() => onFinish());
    }
  }, [record]);

  const { runAsync: runReportAPI } = useRequest(reportAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'exportReport':
          download(response);
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

  return (
    <div content={'Mockup'} className={styles['exam-profile']}>
      <Typography.Title level={3}>Statistical Reports</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Report'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'id'}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={data}
        />
      </Card>
      <Modal
        width={600}
        open={open}
        style={{ top: 20 }}
        title={record.type}
        okText={'Export'}
        onOk={onSave}
        closable={false}
        maskClosable={false}
        onCancel={onCloseCallback}
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

          {
            record?.formFields ? record.formFields : null
          }
        </Form>
      </Modal>
    </div>

  )
}

export default StatisticalReports;