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
import {useModal} from "../../context/modal-provider";

const ExamProfile = () =>  {

  const navigate = useNavigate();
  const modalApi = useModal();
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

  const [data, setData] = useState([
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      ue: 'L2',
      uc: 'L1',
      at: 'Pass',
      blnst: 'Pass',
      status: 'Success',
      stage: 'Import',
    }
  ]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 140,
      render: (row) => <Button size={'small'} type={'primary'} danger onClick={() => {}}>Resume Case</Button>
    },
    {
      title: 'Current Stage',
      key: 'stage',
      dataIndex: 'stage',
      width: 130,
      sorter: true,
    },
    {
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      width: 100,
      sorter: true,
    },
    {
      title: 'Serial No.',
      key: 'serialNo',
      dataIndex: 'serialNo',
      width: 140,
      sorter: true,
    },
    {
      title: 'Candidate No.',
      key: 'candidateNo',
      dataIndex: 'candidateNo',
      width: 140,
      sorter: true,
    },
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      width: 100,
      sorter: true,
    },
    {
      title: 'Name',
      key: 'name',
      dataIndex: 'name',
      width: 160,
      sorter: true,
    },
    {
      title: 'Email',
      key: 'email',
      dataIndex: 'email',
      width: 180,
      sorter: true,
    },
    {
      title: 'UE',
      key: 'ue',
      dataIndex: 'ue',
      width: 100,
      sorter: true,
    },
    {
      title: 'UC',
      key: 'uc',
      dataIndex: 'uc',
      width: 100,
      sorter: true,
    },
    {
      title: 'AT',
      key: 'at',
      dataIndex: 'at',
      width: 100,
      sorter: true,
    },
    {
      title: 'BLNST',
      key: 'blnst',
      dataIndex: 'blnst',
      width: 100,
      sorter: true,
    },

  ], []);


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

  const onClickReset = useCallback(() => {
    modalApi.confirm({
      title:'If you confirm to reset Exam Profile, all imported results and generated PDF and Sign and Issues will be removed. Are you sure to reset Exam Profile? ',
      width: 500,
      okText: 'Confirm',
    });
  },[]);


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
                <Button type={'primary'} onClick={onClickReset}>Reset Exam Profile</Button>
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
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Pagination
            showSizeChanger
            total={pagination.total}
            pageSizeOptions={defaultPaginationInfo.sizeOptions}
            onChange={paginationOnChange}
            current={pagination.page}
            pageSize={pagination.pageSize}
          />
        </Col>
      </Row>
      <br/>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'On Hold Case'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'candidateNo'}
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={data}
        />
        <br/>
        <Row justify={'end'}>
          <Col>
            <Pagination
              total={pagination.total}
              pageSizeOptions={defaultPaginationInfo.sizeOptions}
              onChange={paginationOnChange}
              pageSize={defaultPaginationInfo.pageSize}
              current={pagination.page}
            />
          </Col>
        </Row>
        <br/>
      </Card>
      {/*<fieldset style={{paddingLeft: 30}}>*/}
      {/*  <legend><Typography.Title level={5}>Workflow Summary</Typography.Title></legend>*/}
      {/*  <Descriptions*/}
      {/*    size={'small'}*/}
      {/*    items={[*/}
      {/*      {*/}
      {/*        key: 1,*/}
      {/*        label: 'Imported',*/}
      {/*        children: 30000,*/}
      {/*      },*/}
      {/*      {*/}
      {/*        key: 2,*/}
      {/*        label: 'Generated PDF',*/}
      {/*        children: '0 out of 0 failed',*/}
      {/*      },*/}
      {/*      {*/}
      {/*        key: 3,*/}
      {/*        label: 'Issued Cert.',*/}
      {/*        children: '0 out of 0 failed',*/}
      {/*      },*/}
      {/*      {*/}
      {/*        key: 4,*/}
      {/*        label: 'Sent Email',*/}
      {/*        children: '0 out of 0 failed',*/}
      {/*      }*/}
      {/*    ]}*/}
      {/*  />*/}
      {/*</fieldset>*/}
      {/*<br/>*/}
      {/*<Import />*/}
      {/*<Tabs*/}
      {/*  onChange={() => {}}*/}
      {/*  items={tabItems}*/}
      {/*/>*/}
      {/*<br/>*/}
      {/*<ExceptionalCaseModal*/}
      {/*  open={exceptionalCaseOpen}*/}
      {/*  title={'Exceptional Case'}*/}
      {/*  onCloseCallback={() => setExceptionalCaseOpen(false)}*/}
      {/*  // onFinishCallback={() => setRevokeOpen(false)}*/}
      {/*/>*/}
    </div>

  )
}

export default ExamProfile;