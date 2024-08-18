import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link} from "react-router-dom";

import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Watermark, Alert, Form, Card, Typography, Breadcrumb, Popconfirm, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
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
  DownloadOutlined,
  DeleteOutlined,
  CopyOutlined,
  SendOutlined,
  EditOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import HKID from "@/components/HKID";
import Email from "@/components/Email";

const HistoricalResultList = () =>  {

  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

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

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  });

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Historical Result',
    },
  ], []);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 100,
      render: (row) => (
        <Row gutter={[8, 8]}>
          { row.status === '' ? <Col span={24}><Button size={'small'} type={'primary'} onClick={() => {}}>Reissue</Button></Col> : null}
          { row.status === '' ? <Col span={24}><Button size={'small'} type={'primary'} danger onClick={() => {}}>Void</Button></Col> : null}
          { row.status === 'DQ' ? <Col span={24}><Button size={'small'} type={'primary'} danger onClick={() => {}}>Un-void</Button></Col> : null}
        </Row>
      )
    },
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      width: 100,
      sorter: true,
    },
    {
      title: 'Passport No.',
      key: 'passport',
      dataIndex: 'passport',
      width: 120,
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
      title: 'UE (Exam Date)',
      key: 'ue',
      render: (row) => (
        <Popconfirm
          disabled
          placement="bottomLeft"
          title={'Are you sure to void the UE result?'}
          okText="Yes"
          cancelText="No"
        >
          <Link style={{ textDecoration: row.ueVoid ? 'line-through' : 'inherit'}}>{row.ue}</Link>
        </Popconfirm>
      ),
      width: 120,
      sorter: true,
    },
    {
      title: 'UC (Exam Date)',
      key: 'uc',
      render: (row) => (
        <Popconfirm
          placement="bottomLeft"
          title={'Are you sure to void the UC result?'}
          okText="Yes"
          cancelText="No"
        >
          <Link style={{ textDecoration: row.ucVoid ? 'line-through' : 'inherit'}}>{row.uc}</Link>
        </Popconfirm>
      ),
      width: 120,
      sorter: true,
    },
    {
      title: 'AT (Exam Date)',
      key: 'at',
      render: (row) => (
        <Popconfirm
          placement="bottomLeft"
          title={'Are you sure to void the AT result?'}
          okText="Yes"
          cancelText="No"
        >
          <Link style={{ textDecoration: row.atVoid ? 'line-through' : 'inherit'}}>{row.at}</Link>
        </Popconfirm>
      ),
      width: 120,
      sorter: true,
    },
    {
      title: 'BLNST (Exam Date)',
      key: 'blnst',
      render: (row) => (
        <Popconfirm
          placement="bottomLeft"
          title={'Are you sure to void the BLNST result?'}
          okText="Yes"
          cancelText="No"
        >
          <Link style={{ textDecoration: row.blnstVoid ? 'line-through' : 'inherit'}}>{row.blnst}</Link>
        </Popconfirm>
      ),
      width: 120,
      sorter: true,
    },
    {
      title: 'Remark',
      key: 'remark',
      dataIndex: 'remark',
      width: 100,
      sorter: true,
    },
  ], []);

  const data = [
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700002',
      name: 'Lee Tai Man',
      email: 'taiman.lee@hotmail.com',
      ue: 'L2 (2011-01-01)',
      uc: 'L1 (2012-01-01)',
      at: 'Pass (2013-01-01)',
      blnst: 'Pass (2014-01-01)',
      ueVoid: false,
      ucVoid: false,
      atVoid: false,
      blnstVoid: false,
      status: 'DQ',
    },
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700003',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      ue: 'L2 (2019-01-01)',
      uc: 'L1 (2008-01-01)',
      at: 'Pass (2019-01-01)',
      blnst: '',
      ueVoid: true,
      ucVoid: false,
      atVoid: false,
      blnstVoid: false,
      status: '',
    },
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700004',
      name: 'Wong Tai Man',
      email: 'taiman.wong@hotmail.com',
      ue: 'L2 (2019-01-01)',
      uc: 'L1 (2019-01-01)',
      at: 'Pass (2020-01-01)',
      blnst: 'Pass (2017-05-01)',
      ueVoid: false,
      ucVoid: false,
      atVoid: true,
      blnstVoid: false,
      status: '',
    }
  ];

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

  return (
    <Watermark content={'Mockup'} className={styles['user-list']}>
      <Alert
        message={<b>The functionality of historical result management page will be fully developed by the end of 2024.</b>}
        type="warning"
        closable
      />
      <br/>
      <Typography.Title level={3}>Historical Result</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <br/>
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
                  <Text name={'hkid'} label={'HKID'}/>
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
        title={''}
      >
        <ResizeableTable
          size={'big'}
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
    </Watermark>

  )
}

export default HistoricalResultList;