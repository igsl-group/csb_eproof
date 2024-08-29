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
import {useMessage} from "../../../context/message-provider";
import {systemAPI} from "../../../api/request";
import {
  toQueryString
} from "@/utils/util";


const AuditLog = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState(false);
  const [recordId, setRecordId] = useState('');
  const [type, setType] = useState('');
  const [filterCondition, setFilterCondition] = useState(null);

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [100, 200, 500],
    pageSize: 100,
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

  const columns = useMemo(() => [
    {
      title: "Time",
      dataIndex: "createdDate",
      width: 200,
    },
    {
      title: "DP User ID",
      dataIndex: "createdBy",
      width: 150,
    },
    {
      title: "Source Ip",
      dataIndex: "ipAddress",
      width: 150,
    },
    {
      title: "Action",
      dataIndex: "logAction",
      width: 120,
    },
    {
      title: "Accessed Resources",
      dataIndex: "logDetails",
      width: 300,
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
    getAuditLogList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getAuditLogList(tempPagination, filterCondition);
  }, [pagination]);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'System',
    },
    {
      title: 'Audit Log',
    },
  ], []);

  const { runAsync: runSystemAPI } = useRequest(systemAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'auditLogList':
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
    getAuditLogList(pagination);
  }, []);

  const getAuditLogList = useCallback((pagination = {}, filter = {}) => {
    runSystemAPI('auditLogList', toQueryString(pagination, filter));
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
      <Typography.Title level={3}>Audit Log</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
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
            showTotal={(total) => `Total ${total} items`}
            showQuickJumper
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
              showSizeChanger={false}
              total={pagination.total}
              pageSizeOptions={defaultPaginationInfo.sizeOptions}
              onChange={paginationOnChange}
              current={pagination.page}
              pageSize={pagination.pageSize}
              showTotal={(total) => `Total ${total} items`}
              showQuickJumper
            />
          </Col>
        </Row>
        <br/>
      </Card>
      <br/>
    </div>

  )
}

export default AuditLog;