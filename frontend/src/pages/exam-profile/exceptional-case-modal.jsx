import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Divider, Form, Card, Typography, Breadcrumb, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  DeleteOutlined,
  DownloadOutlined,
  CopyOutlined,
  SendOutlined
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";

const ExceptionalCaseModal = (props) =>  {

  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, []);

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const {
    serialNo,
  } = useParams();

  const [data, setData] = useState([
    {
      revokeDate: '2024-01-01',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      remark: 'DQ',
    }
  ]);



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
      title: 'Certificate Management',
    },
    {
      title: 'Invalid',
    },
  ], []);

  const onClickDownload = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickRevoke= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 120,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'Download'} icon={<DownloadOutlined />} onClick={onClickDownload}/>
        </Space>
      )
    },
    {
      title: 'Revoke Date',
      key: 'revokeDate',
      dataIndex: 'revokeDate',
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
      title: 'Remark',
      key: 'remark',
      dataIndex: 'remark',
      width: 200,
      sorter: true,
    },
  ], []);

  return (
    <Modal
      width={1000}
      title={'Create Exam Profile'}
      okText={'Save'}
      closable={false}
      maskClosable={false}
      onCancel={onClose}
      {...props}
    >
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
                {/*<Col span={24} md={12}>*/}
                {/*  <Text name={"serialNo"} label={'Serial No.'} size={12}/>*/}
                {/*</Col>*/}
                {/*<Col span={24} md={12}>*/}
                {/*  /!*<Text name={'candidateNo'} label={'Candidate No.'} size={12}/>*!/*/}
                {/*</Col>*/}
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
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Pagination
            total={pagination.total}
            pageSizeOptions={defaultPaginationInfo.sizeOptions}
            onChange={paginationOnChange}
            current={pagination.page}
            pageSize={pagination.pageSize}
            showTotal={(total) => `Total ${total} item(s)`}
            showSizeChanger
            showQuickJumper
          />
        </Col>
      </Row>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Certificate Management'}
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
        <Row justify={'end'} gutter={[24, 8]}>
          <Col>
            <Pagination
              total={pagination.total}
              pageSizeOptions={defaultPaginationInfo.sizeOptions}
              onChange={paginationOnChange}
              current={pagination.page}
              pageSize={pagination.pageSize}
              showTotal={(total) => `Total ${total} item(s)`}
              showSizeChanger
              showQuickJumper
            />
          </Col>
          <Col></Col>
        </Row>
        <br/>
      </Card>
    </Modal>
  )
}

export default ExceptionalCaseModal;