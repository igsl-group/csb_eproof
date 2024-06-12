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
import Dropdown from "@/components/Dropdown";
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
      examDate: '2024-01-01',
      type: 'Update Info',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      ue: 'L2',
      uc: 'L1',
      at: 'Pass',
      blnst: 'Pass',
      oldHkid: 'T7700002',
      oldName: 'Wong Tai Man',
      oldUe: 'L2',
      oldUc: 'L1',
      oldAt: 'Pass',
      oldBlnst: 'Pass',
      status: 'Success',
    },
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      type: 'Update Result',
      examDate: '2024-01-01',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      ue: 'L1',
      uc: 'L1',
      at: 'Pass',
      blnst: 'Pass',
      oldHkid: 'T7700002',
      oldName: 'Chan Tai Man',
      oldUe: 'L2',
      oldUc: 'L1',
      oldAt: 'Pass',
      oldBlnst: 'Fail',
      status: 'Success',
    }
  ]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 210,
      render: (row) => (
          <Space>
            <Button size={'small'} type={'primary'} onClick={() => {}}>Remove</Button>
            <Button size={'small'} type={'primary'} onClick={() => {}}>Sign and Issue</Button>
          </Space>
      )
    },
    {
      title: 'Type',
      key: 'type',
      dataIndex: 'type',
      width: 140,
      sorter: true,
    },
    {
      title: 'Exam Date',
      key: 'examDate',
      dataIndex: 'examDate',
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
      title: 'Passport',
      key: 'passport',
      dataIndex: 'passport',
      width: 100,
      sorter: true,
    },
    {
      title: 'Name',
      key: 'name',
      render: (row) => {
        return (
          <div>
            {
              row.oldName === row.name ? (
                <span>{row.name}</span>
              ) : (
                <div>
                  <div>{row.oldName}</div>
                  <div style={{ color: 'red'}}>{row.name}</div>
                </div>
              )
            }
          </div>
        )
      },
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
      render: (row) => {
        return (
          <div>
            {
              row.oldUe === row.ue ? (
                <span>{row.ue}</span>
              ) : (
                <div>
                  <div>{row.oldUe}</div>
                  <div style={{ color: 'red'}}>{row.ue}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'UC',
      key: 'uc',
      render: (row) => {
        return (
          <div>
            {
              row.oldUc === row.uc ? (
                <span>{row.uc}</span>
              ) : (
                <div>
                  <div>{row.oldUc}</div>
                  <div style={{ color: 'red'}}>{row.uc}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'AT',
      key: 'at',
      render: (row) => {
        return (
          <div>
            {
              row.oldAt === row.at ? (
                <span>{row.at}</span>
              ) : (
                <div>
                  <div>{row.oldAt}</div>
                  <div style={{ color: 'red'}}>{row.at}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'BLNST',
      key: 'blnst',
      render: (row) => {
        return (
          <div>
            {
              row.oldBlnst === row.blnst ? (
                <span>{row.blnst}</span>
              ) : (
                <div>
                  <div>{row.oldBlnst}</div>
                  <div style={{ color: 'red'}}>{row.blnst}</div>
                </div>
              )
            }
          </div>
        )
      },
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
      title: 'Certificate Reissuance',
    },
    {
      title: 'Sign and Issue Certificate',
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
      <Typography.Title level={3}>Sign and Issue Certificate</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Row justify={'end'}>
        {/*<Col>*/}
        {/*  <Dropdown name={"serialNo"} label={'Serial No.'} size={12}/>*/}
        {/*</Col>*/}
        <Col>
          <Row gutter={[16, 16]} justify={'end'}>
            <Col>
              <Button type="primary" onClick={onClickDispatch}>Dispatch to Notify Candidate</Button>
            </Col>
          </Row>
        </Col>
      </Row>
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
                  <HKID name={'hkid'} label={'HKID'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'passportNo'} label={'Passport'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'name'} label={'Candidate’s Name'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Email name={'email'} label={'Candidate’s Email'} size={12}/>
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
          <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
            Selected ({selectedRowKeys.length})</Button>
        </Col>
        <Col>
          <Button type="primary" onClick={onClickDownloadAll}>Download All</Button>
        </Col>
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