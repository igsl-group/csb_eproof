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
  Tag,
  Table
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
import {useModal} from "../../context/modal-provider";
import {
  toQueryString
} from "@/utils/util";
import {useAuth} from "../../context/auth-provider";
import PermissionControl from "../../components/PermissionControl";

const RoleList = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState(false);
  const [recordId, setRecordId] = useState('');
  const [type, setType] = useState('');
  const [filterCondition, setFilterCondition] = useState(null);
  const auth = useAuth();

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

  const onDeleteClickCallback = useCallback((id) => {
    modalApi.confirm({
      title:'Are you sure to remove role?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runUserRoleAPI('roleRemove', id)
    });
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
    getRoleList(pagination);
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

  const columns = useMemo(() => {
    const tmpColumns = [];
    if (auth.permissions.includes('Role_Maintenance')) {
      tmpColumns.push({
        title: 'Action',
        key: 'action',
        width: 100,
        render: (row) => (
          <Space>
            <Button size={'small'} title={'Edit'} icon={<EditOutlined />} onClick={() => onEditClickCallback(row.id)} />
            <Button size={'small'} title={'Remove'} icon={<DeleteOutlined />} onClick={() => onDeleteClickCallback(row.id)}/>
          </Space>
        )
      });
    }

    tmpColumns.push(

      {
        title: `Name`,
        key: "name",
        dataIndex: "name",
        width: 250,
        sorter: true,
      },
      {
        title: `Description`,
        key: "description",
        dataIndex: "description",
        width: 250,
        sorter: true,
      },
      {
        title: `Permission`,
        key: "permission",
        sorter: false,
        render: (row) => (
          <Space size={[0, 8]} wrap>
            {
              row.permissions?.map((values) => <Tag>{values.description}</Tag>)
            }
          </Space>
        )
      }
    )

    return tmpColumns;
  }, [auth.permissions]);

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
    getRoleList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getRoleList(tempPagination, filterCondition);
  }, [pagination]);

  const { runAsync: runUserRoleAPI } = useRequest(userRoleAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'roleList':
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setData(content);
          break;
        case 'roleGet':
          break;
        case 'roleRemove':
          messageApi.success('Remove successfully.');
          getRoleList(pagination);
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
    getRoleList(pagination);
  }, []);

  const getRoleList = useCallback((pagination = {}, filter = {}) => {
    runUserRoleAPI('roleList', toQueryString(pagination, filter));
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
    <div className={styles['role-list']} permissionRequired={'Role_Maintenance'}>
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
            showTotal={(total) => `Total ${total} item(s)`}
            showQuickJumper
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
              showTotal={(total) => `Total ${total} item(s)`}
              showSizeChanger
              showQuickJumper
            />
          </Col>
        </Row>
        <br/>
      </Card>
      <br/>
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