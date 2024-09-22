import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link} from "react-router-dom";

import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Watermark, Alert, Form, Card, Typography, Breadcrumb, Tag, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
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
import { examProfileAPI } from '@/api/request';
import {useMessage} from "../../context/message-provider";
import {useModal} from "../../context/modal-provider";
import {
  toQueryString
} from "@/utils/util";
import PermissionControl from "../../components/PermissionControl";
import {useAuth} from "../../context/auth-provider";
import VoidModal from "./modal";
import {stringToHKIDWithBracket} from "../../components/HKID";
const HistoricalResultList = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [searchForm] = Form.useForm();

  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState(false);
  const [recordId, setRecordId] = useState('');
  const [type, setType] = useState('');
  const [record, setRecord] = useState({});
  const [filterCondition, setFilterCondition] = useState(null);
  const auth = useAuth();

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

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Historical Result',
    },
  ], []);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  }, []);

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    getUserList(pagination);
  }, []);

  const columns = useMemo(() => {
    const tmpColumns = [];
    if (auth.permissions.includes('Historical_Result_Submit')) {
      tmpColumns.push(      {
        title: 'Action',
        key: 'action',
        width: 100,
        render: (row) => (
          <Row gutter={[8, 8]}>
            { [null, true].includes(row.valid) ? <Col span={24}><Button style={{width: 80}} size={'small'} type={'primary'} disabled onClick={() => {}}>Reissue</Button></Col> : null}
            <Col span={24}><Button style={{width: 80}}size={'small'} type={'primary'} disabled={row.actionFreeze} danger onClick={() => onVoidWholeResultClickCallback(row)}>Update</Button></Col>          {/*{ [false].includes(row.valid) ? <Col span={24}><Button style={{width: 80}} size={'small'} type={'primary'} danger onClick={() => onVoidWholeResultClickCallback(row)}>Un-void</Button></Col> : null}*/}
          </Row>
        )
      },);
    }

    tmpColumns.push(
      {
        title: 'Valid/Invalid',
        key: 'valid',
        dataIndex: 'valid',
        render: (row) => <span>{row ? 'Valid' : 'Invalid'}</span>,
        width: 100,
        sorter: true,
      },
      {
        title: 'HKID',
        key: 'hkid',
        dataIndex: 'hkid',
        render: (row) => stringToHKIDWithBracket(row),
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
        key: 'ueGrade',
        render: (row) => (row.ueGrade ? (<span style={{ textDecoration: row.ueVoid ? 'line-through' : 'inherit'}}>{row.ueGrade} ({row.ueDate})</span>) : null),
        width: 120,
        sorter: false,
      },
      {
        title: 'UC (Exam Date)',
        key: 'ucGrade',
        render: (row) => (row.ucGrade ? (<span style={{ textDecoration: row.ucVoid ? 'line-through' : 'inherit'}}>{row.ucGrade} ({row.ucDate})</span>) : null),
        width: 120,
        sorter: false,
      },
      {
        title: 'AT (Exam Date)',
        key: 'atGrade',
        render: (row) => (row.atGrade ? (<span style={{ textDecoration: row.atVoid ? 'line-through' : 'inherit'}}>{row.atGrade} ({row.atDate})</span>) : null),
        width: 120,
        sorter: false,
      },
      {
        title: 'BLNST (Exam Date)',
        key: 'blGrade',
        render: (row) => (row.blGrade ? (<span style={{ textDecoration: row.blVoid ? 'line-through' : 'inherit'}}>{row.blGrade} ({row.blDate})</span>) : null),
        width: 120,
        sorter: false,
      },
      {
        title: 'Remark',
        key: 'remark',
        dataIndex: 'remark',
        width: 100,
        sorter: true,
      },
    );
    return tmpColumns;
  }, [auth.permissions]);


  const onVoidClickCallback = useCallback((recordId, subject, valid) => {
    runExamProfileAPI("historicalResultVoid", recordId, {
      subject,
      valid,
      remark: "",
    });
  }, [])

  const onVoidWholeResultClickCallback = useCallback((row) => {
    setOpen(true);
    setRecord(row);
  }, [])


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
    getUserList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getUserList(tempPagination, filterCondition);
  }, [pagination]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'historicalResultList':
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });

          setData(content);
          break;
        case 'historicalResultVoid':
          messageApi.success('Request successfully.');
          getUserList(pagination);
          break;
        case 'userRemove':
          messageApi.success('Remove successfully.');
          getUserList(pagination);
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
    getUserList(pagination);
  }, []);

  const getUserList = useCallback((pagination = {}, filter = {}) => {
    runExamProfileAPI('historicalResultList', toQueryString(pagination, filter));
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
    <div content={'Mockup'} className={styles['user-list']}>
      <Typography.Title level={3}>Historical Result</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
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
        <Row justify={'end'} gutter={[24, 8]}>
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
          <Col></Col>
        </Row>
        <br/>
      </Card>
      <VoidModal
        open={open}
        record={record}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default HistoricalResultList;