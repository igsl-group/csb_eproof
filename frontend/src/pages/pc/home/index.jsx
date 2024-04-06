import React, { useEffect, useState } from 'react';
import {createSearchParams, useNavigate, Link} from "react-router-dom";

import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import dayjs from "dayjs";
import { meetingAPI } from "../../../api/request";
import {MainContext} from "../../../context/mainContext";
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Tag} from 'antd';
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
} from '@ant-design/icons';

export default function Home() {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
  });
  const permissions = JSON.parse(sessionStorage.getItem('permissions'));
  useEffect(() => {
    fetchData();
  }, [pagination.page, pagination.size])

  const fetchData = () => {
    const { page, size } = pagination;
    setPagination({
      ...pagination,
      page,
      size,
    })
    run('getAllMeetings', page, size)
  }

  const onChange = (pageNumber, pageSize) => {
    setPagination({
      ...pagination,
      page: pageNumber - 1,
      size: pageSize
    })
  }

  const onClick = (item) => {
    navigate({
      pathname: `/Meeting/SOM`,
      search: `?${createSearchParams({
        meetingId: item.meetingWorkspaceId
      })}`
    })
  }

  const { run } = useRequest(meetingAPI, {
    manual: true,
    onSuccess: (result, params) => {
      switch (params[0]) {
        case 'getAllMeetings':
          result.content.map((item, index) => {
            item.date = item.startTime.substr(0, 10);
            item.time = item.startTime.substr(11);
          })
          setData(result.content)
          setTotalElements(result.totalElements)
          break;
        default:
          break;
      }

    },
    onError: (error) => {
    },
    onFinally: (params, result, error) => {
      setLoading(false)
    },
  });

  const caseColumns = [
    {
      title: 'Case ID',
      dataIndex: 'caseId',
      render: (text) => <Link to={`/Cases/${text}/View`}>{text}</Link>,
    },
    {
      title: 'FSD Ref.',
      dataIndex: 'fsdRef',
    },
    {
      title: 'Section',
      dataIndex: 'section',
    },
    {
      title: 'Created Date',
      dataIndex: 'createdDate',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (text) => <Tag color="purple">{text}</Tag>

    },
  ];

  const caseData = [
    {
      caseId: '2004',
      fsdRef: 'xxxxx/xxxxx1',
      bdRef: 'YYYY/YYYY1',
      lifipsWorkflowId: '312345',
      section: 'DGC',
      licenceType: 'FS163',
      applicant: 'Chan Siu Ming',
      caseOfficer: 'Chan Siu Ming',
      createdDate: '2024/01/15',
      status: 'Opened',
    },
  ];

  const documentColumns = [
    {
      title: 'Doc ID',
      dataIndex: 'documentId',
      render: (text) => <Link to={`/Documents/${text}/View`}>{text}</Link>,
    },
    {
      title: 'Case ID',
      dataIndex: 'caseId',
    },
    {
      title: 'FSD Ref.',
      dataIndex: 'fsdRef',
    },
    {
      title: 'Section',
      dataIndex: 'section',
    },
    {
      title: 'Created Date',
      dataIndex: 'createdDate',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (text) => <Tag color="blue">{text}</Tag>

    },
  ];

  const documentData = [
    {
      documentId: '10011',
      caseId: '2004',
      fsdRef: 'XXXXX/XXXXX1',
      bdRef: 'YYYY/YYYY1',
      section: 'DGC',
      licenceType: 'FS163',
      applicant: 'Chan Siu Ming',
      template: 'RNO with FSRs lietter (general)',
      createdDate: '2024/01/15',
      status: 'Submitted for Approval',
    },
  ];


  return (
    <div className={styles['home']}>
      <Typography.Title level={3}>Dashboard</Typography.Title>
      <Breadcrumb>
        <Breadcrumb.Item><HomeOutlined /></Breadcrumb.Item>
        <Breadcrumb.Item>Dashboard</Breadcrumb.Item>
      </Breadcrumb>
      <br />
      <Row gutter={[16, 16]}>
        <Col className="gutter-row" span={24} md={8}>
          <Card>
            <Flex justify="space-between">
              <Flex vertical justify="space-between">
                <div><b>Received Cases</b></div>
                <div style={{color: '#652BFB', fontSize: 20}}><b>5</b></div>
              </Flex>
              <Flex vertical justify="center" align={'center'}>
                <div>
                  <Button type="link" style={{ backgroundColor: '#E5E4FF'}} icon={<FolderOpenOutlined style={{ color: '#8381BD'}}/>} size={'large'} />
                </div>
              </Flex>

            </Flex>

          </Card>
        </Col>
        <Col className="gutter-row" span={24} md={8}>
          <Card>
            <Flex justify="space-between">
              <Flex vertical justify="space-between">
                <div><b>To-do Cases</b></div>
                <div style={{color: '#ECAF15', fontSize: 20}}><b>17</b></div>
              </Flex>
              <Flex vertical justify="center" align={'center'}>
                <div>
                  <Button type="link" style={{ backgroundColor: '#FFF3D7'}} icon={<ScheduleOutlined style={{ color: '#FCCD5B'}}/>} size={'large'} />
                </div>
              </Flex>
            </Flex>

          </Card>
        </Col>
        <Col className="gutter-row" span={24} md={8}>
          <Card>
            <Flex justify="space-between">
              <Flex vertical justify="space-between">
                <div><b>To-do Documents</b></div>
                <div style={{color: '#32AD73', fontSize: 20}}><b>28</b></div>
              </Flex>
              <Flex vertical justify="center" align={'center'}>
                <div>
                  <Button type="link" style={{ backgroundColor: '#DAF6E9'}} icon={<FileDoneOutlined style={{ color: '#56CF99'}}/>} size={'large'} />
                </div>
              </Flex>
            </Flex>

          </Card>
        </Col>
        <Col className="gutter-row" span={24} md={12}>
          <Card
            bordered={false}
            className={'card-body-nopadding'}
            title={"Latest Cases"}
          >
            <ResizeableTable size={'big'} columns={caseColumns} dataSource={caseData} />
          </Card>
        </Col>
        <Col className="gutter-row" span={24} md={12}>
          <Card
            bordered={false}
            className={'card-body-nopadding'}
            title={"Latest Documents"}
          >
            <ResizeableTable size={'big'} columns={documentColumns} dataSource={documentData} />
          </Card>
        </Col>
      </Row>
    </div>

  )
}
