import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Table, Badge, Form, Card, Typography, Breadcrumb, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
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
  DownOutlined,
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

const ApprovalWorkflow = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [filterCondition, setFilterCondition] = useState(null);
  const [form] = Form.useForm();
  const [record, setRecord] = useState({});

  const [revokeOpen, setRevokeOpen] = useState(false);

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
      title: 'Type',
      key: 'type',
      dataIndex: 'type',
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
      title: 'HKID',
      key: 'passportNo',
      dataIndex: 'passportNo',
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
    getRevokeList(pagination);
  }, []);

  const getRevokeList = useCallback((pagination = {}, filter = {}) => {
    runExamProfileAPI('getRevokeList', toQueryString(pagination, filter));
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
        title={'Pending Action'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'id'}
          // rowSelection={{
          //   type: 'checkbox',
          //   ...rowSelection,
          // }}
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          expandable={{
            expandedRowRender: (row) => {
              const child = row.certInfos;
              return child ? <Table
                columns={[
                  {
                    title: 'Issue Date',
                    key: 'actualSignTime',
                    render: (row) => dayjs(row.actualSignTime).format('YYYY-MM-DD'),
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'Exam Date',
                    key: 'examDate',
                    dataIndex: 'examDate',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'UE',
                    key: 'ueGrade',
                    dataIndex: 'ueGrade',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'UC',
                    key: 'ucGrade',
                    dataIndex: 'ucGrade',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'AT',
                    key: 'atGrade',
                    dataIndex: 'atGrade',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'BLNST',
                    key: 'blnstGrade',
                    dataIndex: 'blnstGrade',
                    width: 100,
                    sorter: true,
                  },
                ]}
                rowKey={'id'}
                dataSource={child}
                pagination={false}
              /> : null
            },
            defaultExpandedRowKeys: ['0'],
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
      <RevokeCertModal
        open={revokeOpen}
        title={'Revoke Certificate'}
        record={record}
        onCloseCallback={() => setRevokeOpen(false)}
        onFinishCallback={() => setRevokeOpen(false)}
      />
    </div>
  )
}

export default ApprovalWorkflow;