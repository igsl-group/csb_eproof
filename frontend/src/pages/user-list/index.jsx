import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link} from "react-router-dom";

import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Divider, Table, Card, Typography, Breadcrumb, Tag, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  DeleteOutlined,
  EditOutlined,
} from '@ant-design/icons';
import UserModal from "./modal";
import { userRoleAPI } from '@/api/request';
import {TYPE } from '@/config/enum';
import {useMessage} from "../../context/message-provider";
import {useModal} from "../../context/modal-provider";
import {
  toQueryString
} from "@/utils/util";

const UserList = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState(false);
  const [recordId, setRecordId] = useState('');
  const [type, setType] = useState('');
  const [filterCondition, setFilterCondition] = useState(null);

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

  const onDeleteClickCallback = useCallback((id) => {
    runUserRoleAPI('userRemove', id);
    // runUserRoleAPI('userGet', recordId)

  }, []);

  const onEditClickCallback = useCallback((id) => {
    setRecordId(id);
    setOpen(true);
    setType(TYPE.EDIT);
    // runUserRoleAPI('userGet', recordId)

  }, []);

  const onCreateClickCallback = useCallback(() => {
    setOpen(true);
    setType(TYPE.CREATE);
  }, []);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  }, []);

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    getUserList(pagination);
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'System',
    },
    {
      title: 'User',
    },
  ], []);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 100,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'Edit'} icon={<EditOutlined />} onClick={() => onEditClickCallback(row.id)} />
          <Button size={'small'} title={'Remove'} icon={<DeleteOutlined />} onClick={() => onDeleteClickCallback(row.id)}/>
        </Space>
      )
    },
    {
      title: `DP User Id`,
      key: "dpUserId",
      dataIndex: "dpUserId",
      width: 150,
      sorter: true,
    },
    {
      title: `Name`,
      key: "name",
      dataIndex: "name",
      width: 200,
      sorter: true,
    },
    {
      title: `Post`,
      key: "post",
      dataIndex: "post",
      width: 150,
      sorter: true,
    },
    {
      title: `Email`,
      key: "email",
      dataIndex: "email",
      width: 200,
      sorter: true,
    },
    {
      title: `Role`,
      key: "roles",
      dataIndex: "roles",
      // width: 350,
      render: (roles) => (
        <div>
          {
            roles.map((row) => <Tag>{row.name}</Tag>)
          }
        </div>
      )
    },
    {
      title: `Status`,
      key: "status",
      dataIndex: "status",
      width: 150,
      sorter: true,
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

  const { runAsync: runUserRoleAPI } = useRequest(userRoleAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'userList':
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setData(content);
          break;
        case 'userGet':
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
    runUserRoleAPI('userList', toQueryString(pagination, filter));
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
    <div className={styles['user-list']}>
      <Typography.Title level={3}>User</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br />
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Button type="primary" onClick={() => onCreateClickCallback()}>Create</Button>
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
      <br />
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={''}
      >
        <Table
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
      <UserModal
        type={type}
        recordId={recordId}
        open={open}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default UserList;