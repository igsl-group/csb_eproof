import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link} from "react-router-dom";

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
  Button,
  Col,
  Row,
  Flex,
  Modal,
  Pagination,
  Tag
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
  AreaChartOutlined, EditOutlined, DeleteOutlined,
} from '@ant-design/icons';
import RoleModal from "./modal";
import { userRoleAPI } from '@/api/request';
import {TYPE } from '@/config/enum';
import {useMessage} from "../../context/message-provider";

const RoleList = () =>  {

  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState(false);
  const [recordId, setRecordId] = useState('');
  const [type, setType] = useState('');

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
    runUserRoleAPI('roleRemove', id);
    // runUserRoleAPI('userGet', recordId)

  }, []);

  const onEditClickCallback = useCallback((id) => {
    setRecordId(id);
    setOpen(true);
    setType(TYPE.EDIT);
  }, []);

  const onCreateClickCallback = useCallback(() => {
    setOpen(true);
    setType(TYPE.CREATE);
    // runUserRoleAPI('userGet', recordId)

  }, []);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  });

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    getRoleList();
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'System',
    },
    {
      title: 'Role',
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
      title: `Name`,
      key: "name",
      dataIndex: "name",
      width: 180,
      sorter: true,
    },
    {
      title: `Description`,
      key: "description",
      dataIndex: "description",
      width: 180,
      sorter: true,
    },
    {
      title: `Permission`,
      key: "permission",
      sorter: true,
      render: (row) => (
        <div>
          {
            row.permissions?.map((values) => <Tag>{values.description}</Tag>)
          }
        </div>
      )
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
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
  }, [pagination]);

  const { runAsync: runUserRoleAPI } = useRequest(userRoleAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'roleList':
          const data = response.data || {};
          const content = data.content || [];
          setData(content);
          break;
        case 'roleGet':
          break;
        case 'roleRemove':
          messageApi.success('Remove successfully.');
          getRoleList();
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
    getRoleList();
  }, []);

  const getRoleList = () => {
    runUserRoleAPI('roleList');
  }

  return (
    <div className={styles['role-list']}>
      <Typography.Title level={3}>Role</Typography.Title>
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
      <RoleModal
        type={type}
        recordId={recordId}
        open={open}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default RoleList;