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
  SendOutlined, EditOutlined
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import EmailModal from "./modal";
import {download} from "../../utils/util";
import { generalAPI } from '@/api/request';
import {TYPE } from '@/config/enum';
import parse, { attributesToProps } from 'html-react-parser';
import {
  toQueryString
} from "@/utils/util";

const EmailTemplateList = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [type, setType] = useState('');
  const [record, setRecord] = useState('');
  const [filterCondition, setFilterCondition] = useState(null);

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [10, 20, 40],
    pageSize: 10,
    page: 1,
    sortBy: 'type',
    orderBy: 'descend',
  }), []);

  const [pagination, setPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  // const onDeleteClickCallback = useCallback((id) => {
  //   // runUserRoleAPI('userRemove', id);
  //   // runUserRoleAPI('userGet', recordId)
  //
  // }, []);

  const onEditClickCallback = useCallback((row) => {
    setRecord(row);
    setOpen(true);
    setType(TYPE.EDIT);
    // runUserRoleAPI('userGet', recordId)

  }, []);

  // const onCreateClickCallback = useCallback(() => {
  //   setOpen(true);
  //   setType(TYPE.CREATE);
  // }, []);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  }, []);

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    getEmailTemplateList(pagination);
  }, []);

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
    getEmailTemplateList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getEmailTemplateList(tempPagination, filterCondition);
  }, [pagination]);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Template',
    },
    {
      title: 'Email',
    },
  ], []);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 80,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'Edit'} onClick={() => onEditClickCallback(row)} icon={<EditOutlined />}/>
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
      title: 'Key',
      key: 'templateName',
      dataIndex: 'templateName',
      width: 140,
      sorter: true,
    },
    {
      title: 'Include Email Address',
      key: 'includeEmails',
      dataIndex: 'includeEmails',
      width: 200,
      render: (value) => (
        <div>
          {
            value?.split(',').map((row, index) => <div key={index}>{row}</div>)
          }
        </div>
      )
      // sorter: true,
    },
    {
      title: 'Subject',
      key: 'subject',
      dataIndex: 'subject',
      width: 200,
      sorter: true,
    },
    {
      title: 'Body',
      key: 'body',
      dataIndex: 'body',
      // width: 200,
      render: (value) => <div style={{ textWrap: 'wrap'}}>{parse(value)}</div>,
      sorter: true,
    },
  ], []);

  const { runAsync: runGeneralAPI } = useRequest(generalAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'emailTemplateList':
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setData(content);
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

  useEffect(() => {
    getEmailTemplateList(pagination);
  }, []);

  const getEmailTemplateList = useCallback((pagination = {}, filter = {}) => {
    runGeneralAPI('emailTemplateList', toQueryString(pagination, filter));
  }, [])

  const resetPagination = useCallback(() => {
    const tempPagination = {
      ...pagination,
      total: 0,
      page: defaultPaginationInfo.page,
      pageSize: defaultPaginationInfo.pageSize,
      sortBy: defaultPaginationInfo.sortBy,
      orderBy: defaultPaginationInfo.orderBy,
    }
    setPagination(tempPagination);
    return tempPagination;
  }, [pagination]);


  return (
    <div className={styles['email-template']}>
      <Typography.Title level={3}>Template Management - Email</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        {/*<Col>*/}
        {/*  <Button type="primary" onClick={() => setOpen(true)}>Create</Button>*/}
        {/*</Col>*/}
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
      <br />
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
        <Row justify={'end'} >
          <Col>
            <Pagination
              total={pagination.total}
              pageSizeOptions={defaultPaginationInfo.sizeOptions}
              onChange={paginationOnChange}
              pageSize={defaultPaginationInfo.pageSize}
              current={pagination.page}
              showTotal={(total) => `Total ${total} item(s)`}
              showSizeChanger
              showQuickJumper
            />
          </Col>
        </Row>
        <br/>
      </Card>
      <br/>
      <EmailModal
        type={type}
        record={record}
        open={open}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default EmailTemplateList;