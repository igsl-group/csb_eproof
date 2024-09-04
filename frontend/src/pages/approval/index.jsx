import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Table, Badge, Form, Card, Typography, Breadcrumb, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
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
import RevokeTable from "./revoke-table";
import PermissionControl from "../../components/PermissionControl";

const ApprovalWorkflow = () =>  {

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
  const [actionReissueData, setActionReissueData] = useState([]);
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


  const certStageMapping = useMemo(() => ({
    GENERATED: "Generate",
    SIGN_ISSUE: "SignAndIssueCert",
    NOTIFY: "Notify",
  }), []);


  useEffect(() => {
    let workflowActionList = [];
    if (auth.permissions.includes('Certificate_Import')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['imported'], 'Import'))
    }
    if (auth.permissions.includes('Certificate_Generate')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['generatePdfTotal'], 'Generate'))
    }
    if (auth.permissions.includes('Certificate_Sign_And_Issue')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['issuedPdfTotal'], 'SignAndIssueCert'))
    }
    if (auth.permissions.includes('Certificate_Notify')) {
      workflowActionList.push(...actionListCallback(examProfileSummaryList, ['sendEmailTotal'], 'Notify'))
    }
    workflowActionList = workflowActionList.sort((a, b) => dayjs(a.examDate, 'YYYY-MM-DD') - dayjs(b.examDate, 'YYYY-MM-DD'))
    setActionListData(workflowActionList);
  }, [auth.permissions, examProfileSummaryList])


  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [10, 20, 40],
    pageSize: 99999,
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
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      width: 140,
      sorter: false,
    },
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      width: 100,
      sorter: false,
    },
    {
      title: 'HKID',
      key: 'passportNo',
      dataIndex: 'passportNo',
      width: 100,
      sorter: false,
    },
    {
      title: 'Name',
      key: 'name',
      dataIndex: 'name',
      width: 160,
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
        case 'getRevokeList':
          const data = response.data || {};
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

  useEffect(() => {
    getRevokeList(pagination, {}, auth.permissions);
  }, [auth.permissions]);

  const getRevokeList = useCallback(async (pagination = {}, filter = {}, permissions) => {
    // await runExamProfileAPI('getRevokeList', toQueryString(pagination, filter));
    await runExamProfileAPI('examProfileDropdown')
      .then(response => response.data)
      .then(async (data) => {
        const list = [];
        for (let row of data) {
          const result = await runExamProfileAPI('examProfileSummaryGet', row.serialNo)
            .then(response => response.data)
          list.push({
            ...result,
            examProfile: row
          });
        }
        return list;
      }).then((list) => {
        setExamProfileSummaryList(list);
      })
      const reissueList = [];
      if (permissions.includes('Certificate_Generate')) {
        const data = await runExamProfileAPI('certRenewList', 'GENERATED', {}, toQueryString(pagination))
          .then((response) => response.data)
          .then((data) => data.content);
        reissueList.push(...data);
      }

    if (permissions.includes('Certificate_Sign_And_Issue')) {
      const data = await runExamProfileAPI('certRenewList', 'SIGN_ISSUE', {}, toQueryString(pagination))
        .then((response) => response.data)
        .then((data) => data.content);
      reissueList.push(...data);
    }
    if (permissions.includes('Certificate_Notify')) {
      const data = await runExamProfileAPI('certRenewList', 'NOTIFY', {}, toQueryString(pagination))
        .then((response) => response.data)
        .then((data) => data.content);
      reissueList.push(...data);
    }
    setActionReissueData(reissueList);
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
    <div className={styles['approval']}>
      <Typography.Title level={3}>Outstanding Tasks</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <PermissionControl permissionRequired={['Revoke_Submit', 'Revoke_Approve']}>
        <RevokeTable/>
      </PermissionControl>
      <PermissionControl permissionRequired={['Certificate_Import', 'Certificate_Generate', 'Certificate_Sign_And_Issue', 'Certificate_Notify']}>
        <br/>
        <Card
          bordered={false}
          className={'card-body-nopadding'}
          title={'Pending Certificate Issuance'}
        >
          <ResizeableTable
            size={'big'}
            rowKey={'id'}
            onChange={tableOnChange}
            pagination={false}
            scroll={{
              x: '100%',
            }}
            columns={[
              {
                title: 'Serial No.',
                key: 'serialNo',
                width: 140,
                sorter: false,
                render: (row) => <Link to={`/Workflow/${row.stage}?serialNo=${row.serialNo}`}>{row.serialNo}</Link>,

              },
              {
                title: 'Exam Date',
                key: 'examDate',
                dataIndex: 'examDate',
                width: 100,
                sorter: false,
              },
              {
                title: 'Stage',
                key: 'stage',
                dataIndex: 'stage',
                width: 100,
                sorter: false,
              },
            ]}
            dataSource={actionListData}
          />
        </Card>
      </PermissionControl>
      <PermissionControl permissionRequired={['Certificate_Generate', 'Certificate_Sign_And_Issue', 'Certificate_Notify']}>
        <br/>
        <Card
          bordered={false}
          className={'card-body-nopadding'}
          title={'Pending Certificate Reissuance'}
        >
          <ResizeableTable
            size={'big'}
            rowKey={'id'}
            onChange={tableOnChange}
            pagination={false}
            scroll={{
              x: '100%',
            }}
            columns={[
              {
                title: 'Stage',
                key: 'stage',
                width: 140,
                sorter: false,
                render: (row) => <Link
                  to={`/WorkflowRenew/${certStageMapping[row?.certStage?.code]}`}>{certStageMapping[row?.certStage?.code]}</Link>,

              },
              {
                title: 'Type',
                key: 'type',
                dataIndex: 'type',
                width: 100,
                sorter: false,
              },
              {
                title: 'Remark',
                key: 'remark',
                dataIndex: 'remark',
                width: 100,
                sorter: false,
              },
            ]}
            dataSource={actionReissueData}
          />
        </Card>
      </PermissionControl>
      <br/>
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
          columns={[
            {
              title: 'Action',
              key: 'action',
              dataIndex: 'action',
              width: 140,
              sorter: false,

            },
            {
              title: 'Name',
              key: 'name',
              dataIndex: 'name',
              width: 140,
              sorter: false,

            },
            {
              title: 'HKID',
              key: 'hkid',
              dataIndex: 'hkid',
              width: 140,
              sorter: false,

            },
            {
              title: 'Remark',
              key: 'remark',
              dataIndex: 'remark',
              width: 100,
              sorter: false,
            },
          ]}
          dataSource={[]}
        />
      </Card>
      <br/>
    </div>
  )
}

export default ApprovalWorkflow;