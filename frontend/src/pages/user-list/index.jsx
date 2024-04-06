import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link} from "react-router-dom";

import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined, DownloadOutlined, DeleteOutlined, CopyOutlined, SendOutlined,
  EditOutlined,
} from '@ant-design/icons';
import UserModal from "./modal";

const UserList = () =>  {

  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

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

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  });

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
      width: 160,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'Edit'} icon={<EditOutlined />}/>
          <Button size={'small'} title={'Remove'} icon={<DeleteOutlined />}/>
        </Space>
      )
    },
    {
      title: `DP User Id`,
      key: "uid",
      dataIndex: "uid",
      width: 200,
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
      width: 200,
      sorter: true,
    },
    {
      title: `Status`,
      key: "status",
      dataIndex: "status",
      width: 200,
      sorter: true,
    },
  ], []);

  const data = [
    {
      uid: 'wilfred.lai',
      name: 'Wilfred Lai',
      post: 'System Analyst',
      status: 'Active',
    },
  ];

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

  return (
    <div className={styles['user-list']}>
      <Typography.Title level={3}>User</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br />
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Button type="primary" onClick={() => setOpen(true)}>Create</Button>
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
            />
          </Col>
        </Row>
        <br/>
      </Card>
      <UserModal
        open={open}
        onCloseCallback={onCloseCallback}
      />
    </div>

  )
}

export default UserList;