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
// import Import from "./import";
// import Generate from "./generate";
// import Issue from "./issue";
// import Notify from "./notify";
import dayjs from "dayjs";
// import ExceptionalCaseModal from "./exceptional-case-modal";
import {useModal} from "../../../context/modal-provider";
import {systemAPI} from "../../../api/request";
import {useMessage} from "../../../context/message-provider";
// import ExamProfileFormModal from "./modal";
import {
  toQueryString
} from "@/utils/util";

const SystemConfiguration = () =>  {

  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [999, 999, 999],
    pageSize: 9999,
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

  const [data, setData] = useState([]);

  const columns = useMemo(() => [
    {
      title: 'Key',
      key: 'code',
      dataIndex: 'code',
      width: 130,
      sorter: true,
    },
    {
      title: 'Description',
      key: 'description',
      dataIndex: 'description',
      width: 100,
      sorter: true,
    },
  ], []);

  const { runAsync: runSystemAPI } = useRequest(systemAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'permissionList':
          const data = response.data || [];
          // const content = data.content || [];
          // setPagination({
          //   ...pagination,
          //   total: data.totalElements,
          // });
          setData(data);
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

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'System',
    },
    {
      title: 'System Configuration',
    },
  ], []);


  useEffect(() => {
    getSystemConfigList(pagination);
  }, []);

  const getSystemConfigList = useCallback((pagination = {}, filter = {}) => {
    runSystemAPI('permissionList', toQueryString(pagination, filter));
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
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>System Configuration</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br />
      {/*<Row gutter={[16, 16]} justify={'end'}>*/}
      {/*  <Col>*/}
      {/*    <Pagination*/}
      {/*      showSizeChanger*/}
      {/*      total={pagination.total}*/}
      {/*      pageSizeOptions={defaultPaginationInfo.sizeOptions}*/}
      {/*      onChange={paginationOnChange}*/}
      {/*      current={pagination.page}*/}
      {/*      pageSize={pagination.pageSize}*/}
      {/*    />*/}
      {/*  </Col>*/}
      {/*</Row>*/}
      {/*<br/>*/}
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Permission'}
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
        {/*<br/>*/}
        {/*<Row justify={'end'}>*/}
        {/*  <Col>*/}
        {/*    <Pagination*/}
        {/*      total={pagination.total}*/}
        {/*      pageSizeOptions={defaultPaginationInfo.sizeOptions}*/}
        {/*      onChange={paginationOnChange}*/}
        {/*      pageSize={defaultPaginationInfo.pageSize}*/}
        {/*      current={pagination.page}*/}
        {/*    />*/}
        {/*  </Col>*/}
        {/*</Row>*/}
        {/*<br/>*/}
      </Card>
    </div>

  )
}

export default SystemConfiguration;