import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Table, Form, Card, Typography, Breadcrumb, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  DeleteOutlined,
  DownloadOutlined,
  CopyOutlined,
  SendOutlined,
  EditOutlined
} from '@ant-design/icons';
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import EmailModal from "./modal";
import {download } from "../../utils/util";
import { generalAPI } from '@/api/request';
import {
  toQueryString
} from "@/utils/util";

const CertTemplateList = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [filterCondition, setFilterCondition] = useState(null);
  const {
    serialNo,
  } = useParams();

  // const [data, setData] = useState([
  //   {
  //     type: 'Pass Cert. Template',
  //     description: 'Issue the certificate when at least one subject is passed.',
  //   },
  //   {
  //     type: 'Fail Cert. Template',
  //     description: 'Issue the certificate when all subjects have failed.',
  //   }
  // ]);
  //


  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [10, 20, 40],
    pageSize: 10,
    page: 1,
    sortBy: 'name',
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
    getCertTemplateList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getCertTemplateList(tempPagination, filterCondition);
  }, [pagination]);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Template',
    },
    {
      title: 'Certificate',
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
      width: 80,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'Download'} onClick={() => runGeneralAPI('certTemplateDownload', row.id)} icon={<DownloadOutlined />}/>
        </Space>
      )
    },
    {
      title: 'Key',
      key: 'name',
      dataIndex: 'name',
      width: 260,
      sorter: true,
    },
    {
      title: 'Description',
      key: 'description',
      dataIndex: 'description',
      width: 500,
      sorter: true,
    },
  ], []);

  const { runAsync: runGeneralAPI } = useRequest(generalAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'certTemplateList':
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setData(content);
          break;
        case 'certTemplateDownload':
          download(response);
          messageApi.success('Download successfully.');
          break;
        default:
          break;
      }

    },
    onError: (error) => {
      //Message.error('');
    },
    onFinally: (params, result, error) => {
    },
  });

  useEffect(() => {
    getCertTemplateList(pagination);
  }, []);

  const getCertTemplateList = useCallback((pagination = {}, filter = {}) => {
    runGeneralAPI('certTemplateList', toQueryString(pagination, filter));
  }, []);

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
    <div className={styles['cert-template']}>
      <Typography.Title level={3}>Template Management - Certificate</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        {/*<Col>*/}
        {/*  <Button type="primary" onClick={() => setOpen(true)}>Create</Button>*/}
        {/*</Col>*/}
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
              current={pagination.page}
              pageSize={pagination.pageSize}
            />
          </Col>
        </Row>
        <br/>
      </Card>
      <br/>
    </div>

  )
}

export default CertTemplateList;