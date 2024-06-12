import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Divider, Button, Form, Card, Typography, Breadcrumb, Grid, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined,
  DownloadOutlined, SearchOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import Textarea from "@/components/Textarea";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";

const Issue = () =>  {

  const navigate = useNavigate();
  const modalApi = useModal();
  const [form] = Form.useForm();
  const {
    serialNo,
  } = useParams();

  const [selectedRowKeys, setSelectedRowKeys] = useState('');
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
    }
  ]);

  const columns = useMemo(() => [
    // {
    //   title: 'Action',
    //   key: 'action',
    //   width: 80,
    //   render: (row) => <Button size={'small'} icon={<DownloadOutlined />} onClick={() => {}}/>
    // },
    {
      title: 'Serial No.',
      key: 'serialNo',
      dataIndex: 'serialNo',
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
    {
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      width: 100,
      sorter: true,
    },
  ], []);

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

  const onClickDispatch = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to dispatch to "Notify Candidate" stage?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickDownloadAll = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download all PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const rowSelection = useCallback({
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }, []);

  return (
    <div className={styles['exam-profile']}>
      <fieldset style={{padding: '0 30px'}}>
        <legend><Typography.Title level={5}>Search</Typography.Title></legend>
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
            <Col span={20}>
              <Row gutter={24} justify={'start'}>
                <Col span={24} md={12}>
                  <Text name={"serialNo"} label={'Serial No.'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'candidateNo'} label={'Candidate No.'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <HKID name={'hkid'} label={'HKID'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'passportNo'} label={'Passport No.'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'name'} label={'Candidate Name'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Email name={'email'} label={'Candidate Email'} size={12}/>
                </Col>
              </Row>
            </Col>
            <Col span={4}>
              <Row justify={'end'}>
                <Col>
                  <Button shape="circle" type={'primary'} icon={<SearchOutlined/>} onClick={() => {
                  }}/>
                </Col>
              </Row>
            </Col>
          </Row>
        </Form>
      </fieldset>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Button type="primary" onClick={onClickDispatch}>Dispatch to Notify Candidate</Button>
        </Col>
        <Col>
          <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
            Selected ({selectedRowKeys.length})</Button>
        </Col>
        <Col>
          <Button type="primary" onClick={onClickDownloadAll}>Download All</Button>
        </Col>
        {/*<Col>*/}
        {/*  <Button type="primary" onClick={() => {*/}
        {/*  }}>Issue Cert.</Button>*/}
        {/*</Col>*/}
      </Row>
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
        title={'Sign and Issue Certificate'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'candidateNo'}
          rowSelection={{
            type: 'checkbox',
            ...rowSelection,
          }}
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
    </div>

  )
}

export default Issue;