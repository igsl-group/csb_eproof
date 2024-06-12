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

const ApprovalWorkflow = () =>  {

  const navigate = useNavigate();
  const [form] = Form.useForm();
  const {
    serialNo,
  } = useParams();
  const [openUpdatePersonalParticularsModal, setOpenUpdatePersonalParticularsModal] = useState(false);
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

  const [data, setData] = useState([
    {
      id: 1,
      type: 'Revoke',
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700001',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      certificates: [
        {
          id: 1,
          issueDate: '2024-05-01',
          examDate: '2023-04-01',
          candidateNo: 'C000001',
          hkid: 'T7700002',
          name: 'Chan Tai Man',
          email: 'taiman.chan@hotmail.com',
          ue: 'L2',
          uc: 'L1',
          at: 'Pass',
          blnst: 'Pass',
          status: 'Success',
        },
        {
          id: 2,
          issueDate: '2023-04-01',
          examDate: '2023-04-01',
          candidateNo: 'C000001',
          hkid: 'T7700002',
          name: 'Chan Tai Man',
          email: 'taiman.chan@hotmail.com',
          ue: 'L2',
          uc: 'L1',
          at: 'Pass',
          blnst: 'Pass',
          status: 'Success',
        }
      ],
    },
    {
      id: 2,
      type: 'Revoke',
      serialNo: 'N000000002',
      candidateNo: 'C000002',
      hkid: 'T7700002',
      name: 'Wong Tai Man',
      certificates: [
        {
          id: 1,
          issueDate: '2024-05-01',
          examDate: '2023-04-01',
          candidateNo: 'C000001',
          hkid: 'T7700002',
          name: 'Chan Tai Man',
          email: 'taiman.chan@hotmail.com',
          ue: 'L2',
          uc: 'L1',
          at: 'Pass',
          blnst: 'Pass',
          status: 'Success',
        },
      ],
    },
  ]);

  const columns = useMemo(() => [
    // {
    //   title: 'Id',
    //   key: 'id',
    //   render: (row) => <Button size={'small'} type={'link'} onClick={() => setOpenUpdatePersonalParticularsModal(true)}>{row.id}</Button>,
    //   width: 140,
    //   sorter: true,
    // },
    {
      title: 'Action',
      key: 'action',
      width: 160,
      render: (row) => (
        <Space>
          <Button size={'small'} title={'Download'} onClick={() => setRevokeOpen(true)}>View Case</Button>
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
      title: 'Name',
      key: 'name',
      dataIndex: 'name',
      width: 160,
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
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
  }, [pagination]);

  useEffect(() => {
    form.setFieldsValue({
      serialNo: 'N000000001',
      examDate: dayjs('2024-01-11'),
      plannedAnnouncedDate: dayjs('2024-01-11'),
      location: 'Hong Kong',
    })
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Outstanding Tasks',
    },
  ], []);

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
              const child = row.certificates;
              return child ? <Table
                columns={[
                  {
                    title: 'Issue Date',
                    key: 'issueDate',
                    dataIndex: 'issueDate',
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
                    key: 'ue',
                    dataIndex: 'ue',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'UC',
                    key: 'uc',
                    dataIndex: 'uc',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'AT',
                    key: 'at',
                    dataIndex: 'at',
                    width: 100,
                    sorter: true,
                  },
                  {
                    title: 'BLNST',
                    key: 'blnst',
                    dataIndex: 'blnst',
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
        onCloseCallback={() => setRevokeOpen(false)}
        onFinishCallback={() => setRevokeOpen(false)}
      />
    </div>
  )
}

export default ApprovalWorkflow;