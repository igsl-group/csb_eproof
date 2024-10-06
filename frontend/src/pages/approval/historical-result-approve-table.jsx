import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {
  Table,
  Badge,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Button,
  Space,
  Tabs,
  Col,
  Row,
  Descriptions,
  Modal,
  Pagination,
  Tag
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import dayjs from "dayjs";
import RevokeCertModal from "./revoke-modal";
import { examProfileAPI } from '@/api/request';
import {useMessage} from "../../context/message-provider";
import {useModal} from "../../context/modal-provider";
import {
  toQueryString
} from "@/utils/util";
import {useAuth} from "../../context/auth-provider";
import {stringToHKIDWithBracket} from "../../components/HKID";
import HistoricalResultApproveModal from "./historical-result-approve-modal";

const HistoricalResultApproveTable = () =>  {

  const auth = useAuth();
  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [filterCondition, setFilterCondition] = useState(null);
  const [form] = Form.useForm();
  const [record, setRecord] = useState({});
  const [examProfileSummaryList, setExamProfileSummaryList] = useState([]);
  const [actionListData, setActionListData] = useState([]);
  const [revokeOpen, setRevokeOpen] = useState(false);

  const actionListCallback = useCallback((list, keys = [], stage = '') => {
    let _actionList = [];
    let flag = false;
    for (let row of list) {
      for (let key of keys) {
        if (row[key] > 0) {
          _actionList.push({
            serialNo: row.examProfile.serialNo,
            stage,
            examDate: row.examProfile.examDate,
          });
          break;
        }
      }
    }
    return _actionList;
  }, []);

  const onCloseCallback = useCallback(() => {
    setRevokeOpen(false);
  });

  const onFinishCallback = useCallback(() => {
    setRevokeOpen(false);
    getRevokeList();
  }, []);

  useEffect(() => {
    let workflowActionList = [];
    if (auth.permissions.includes('Certificate_Import_Maintenance')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['imported'], 'Import'))
    }
    if (auth.permissions.includes('Certificate_Generate_Maintenance')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['generatePdfTotal'], 'Generate'))
    }
    if (auth.permissions.includes('Certificate_Sign_And_Issue_Maintenance')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['issuedPdfTotal'], 'SignAndIssueCert'))
    }
    if (auth.permissions.includes('Certificate_Notify_Maintenance')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['sendEmailTotal'], 'Notify'))
    }
    workflowActionList = workflowActionList.sort((a, b) => dayjs(a.examDate, 'YYYY-MM-DD') - dayjs(b.examDate, 'YYYY-MM-DD'))
    setActionListData(workflowActionList);
  }, [auth.permissions, examProfileSummaryList])


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

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 160,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'View'} onClick={() => onClickRevoke(row)}>View Case</Button>
        </Space>
      )
    },
    {
      title: 'Name',
      key: 'name',
      dataIndex: 'historicalResult',
      render: (row) => row.name,
      width: 140,
      sorter: false,

    },
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'historicalResult',
      render: (row) => row.hkid,
      width: 140,
      sorter: false,

    },
    {
      title: 'Passport',
      key: 'passport',
      dataIndex: 'historicalResult',
      render: (row) => row.passport,
      width: 140,
      sorter: false,

    },
    {
      title: 'Remark',
      key: 'remark',
      dataIndex: 'remark',
      width: 300,
      sorter: false,
    },
    {
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      render: (row) => <Tag>{row}</Tag>,
      width: 300,
      sorter: false,
    },
  ], []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Outstanding Tasks',
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
    getRevokeList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getRevokeList(tempPagination, filterCondition);
  }, [pagination]);

  const onClickRevoke = useCallback((row) => {
    setRevokeOpen(true);
    setRecord(row);
  },[]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'historicalResultApproveList':
          const data = response.data || {};
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

  useEffect(() => {
    getRevokeList(pagination);
  }, []);

  const getRevokeList = useCallback(async (pagination = {}, filter = {}) => {
    await runExamProfileAPI('historicalResultApproveList');
  }, []);

  const resetPagination = useCallback(() => {
    const tempPagination = {
      ...pagination,
      total: 0,
      page: defaultPaginationInfo.page,
    //  pageSize: defaultPaginationInfo.pageSize,
    //  sortBy: defaultPaginationInfo.sortBy,
    //  orderBy: defaultPaginationInfo.orderBy,
    }
    setPagination(tempPagination);
    return tempPagination;
  }, [pagination]);


  return (
    <div className={styles['approval']}>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Pending to Void/Unvoid Historical Result'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'id'}
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={data}
        />
      </Card>
      <HistoricalResultApproveModal
        open={revokeOpen}
        title={'Historical Result Approve'}
        record={record}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>
  )
}

export default HistoricalResultApproveTable;