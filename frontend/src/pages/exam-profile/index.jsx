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
  Grid,
  Space,
  Tabs,
  Col,
  Row,
  Descriptions,
  Modal,
  Pagination,
  Button
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
import Textarea from "@/components/Textarea";
import Import from "./import";
import Generate from "./generate";
import Issue from "./issue";
import Notify from "./notify";
import dayjs from "dayjs";
import ExceptionalCaseModal from "./exceptional-case-modal";

const ExamProfile = () =>  {

  const navigate = useNavigate();
  const [exceptionalCaseOpen, setExceptionalCaseOpen] = useState(false);
  const [freezeExamProfile, setFreezeExamProfile] = useState(false);
  const [form] = Form.useForm();
  const {
    serialNo,
  } = useParams();

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [10, 20, 40],
    pageSize: 10,
    page: 1,
    sortBy: 'id',
    orderBy: 'descend',
  }), []);

  const [pagination, setPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  const tableOnChange = useCallback((pageInfo, filters, sorter, extra) => {
    const {
      order,
      columnKey
    } = sorter;
    const tempPagination = {
      ...pagination,
      orderBy: order || defaultPaginationInfo.orderBy,
      sortBy: order ? columnKey : defaultPaginationInfo.sortBy,
    }
    setPagination(tempPagination);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
  }, [pagination]);


  useEffect(() => {
    form.setFieldsValue({
      serialNo: 'N000000001',
      examDate: dayjs('2024-01-11'),
      plannedAnnouncedDate: dayjs('2024-01-11'),
      location: 'Hong Kong',
    })
  }, []);

  const tabItems = useMemo(() => [
    {
      key: 1,
      label: 'Import Result',
      children: <Import />,
    },
    {
      key: 2,
      label: 'Generate Cert. (PDF)',
      children: <Generate />,
    },
    {
      key: 3,
      label: 'Issue Cert.',
      children: <Issue />,
    },
    {
      key: 4,
      label: 'Notify Candidate',
      children: <Notify />,
    },
  ], []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Exam Profile',
    },
    {
      title: serialNo,
    },
  ], []);

  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Exam Profile</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
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
        <Row justify={'start'}>
          <Col span={16}>
            <Row gutter={24} justify={'center'}>
              <Col span={24} md={12}>
                <Text name={"serialNo"} label={'Serial No.'} required size={12} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Date name={'examDate'} label={'Exam Date'} required size={12} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Text name={'actualAnnouncedDate'} label={'Actual Announced Date'} disabled={true}
                      placeholder={'YYYY-MM-DD'} size={12}/>
              </Col>
              <Col span={24} md={12} />
              <Col span={24}>
                <Text name={'location'} label={'Location'} size={50} disabled/>
              </Col>
            </Row>
          </Col>
          <Col span={8}>
            <Row gutter={[8, 8]} justify={'end'}>
              <Col>
                <Button type={'primary'}>Reset Exam Profile</Button>
              </Col>
              <Col>
                <Button type={'primary'} onClick={() => {
                  setExceptionalCaseOpen(true)
                }}>Exceptional Case</Button>
              </Col>
              <Col>
                <Button type={'primary'} danger={freezeExamProfile} onClick={() => {
                  setFreezeExamProfile(!freezeExamProfile)
                }}>{!freezeExamProfile ? 'Freeze Exam Profile' : 'Un-freeze Exam Profile'}</Button>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
      <br/>
      <fieldset style={{paddingLeft: 30}}>
        <legend><Typography.Title level={5}>Workflow Summary</Typography.Title></legend>
        <Descriptions
          size={'small'}
          items={[
            {
              key: 1,
              label: 'Imported',
              children: 30000,
            },
            {
              key: 2,
              label: 'Generated PDF',
              children: '0 out of 0 failed',
            },
            {
              key: 3,
              label: 'Issued Cert.',
              children: '0 out of 0 failed',
            },
            {
              key: 4,
              label: 'Sent Email',
              children: '0 out of 0 failed',
            }
          ]}
        />
      </fieldset>
      <br/>
      <Tabs
        onChange={() => {}}
        items={tabItems}
      />
      <br/>
      <ExceptionalCaseModal
        open={exceptionalCaseOpen}
        title={'Exceptional Case'}
        onCloseCallback={() => setExceptionalCaseOpen(false)}
        // onFinishCallback={() => setRevokeOpen(false)}
      />
    </div>

  )
}

export default ExamProfile;