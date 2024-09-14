import React, { useEffect, useState, useMemo, useCallback, useRef } from 'react';
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
  Input,
  Modal,
  Pagination,
  DatePicker
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
  AreaChartOutlined,
} from '@ant-design/icons';
import ExamProfileFormModal from "./modal";
import {TYPE } from '@/config/enum';
import { examProfileAPI } from '@/api/request';
import {useMessage} from "../../context/message-provider";
import {useModal} from "../../context/modal-provider";
import axios from "axios";
import {
  toQueryString
} from "@/utils/util";
import PermissionControl from "../../components/PermissionControl";

const ExamProfileList = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const keywordRef = useRef(null);
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [filterCondition, setFilterCondition] = useState(null);
  const [keyword, setKeyword] = useState("");
  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [10, 20, 40],
    pageSize: 10,
    page: 1,
    sortBy: 'exam_date',
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

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    console.log(resetPagination())
    getExamProfileList(resetPagination());
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Exam Profile',
    },
  ], []);

  const columns = useMemo(() => [
    {
      title: 'Serial No.',
      key: 'serial_no',
      width: 100,
      render: (row) => <Link to={`/ExamProfile/${row.serialNo}`}>{row.serialNo}</Link>,
      sorter: true,
    },
    {
      title: 'Exam Date',
      key: 'exam_date',
      dataIndex: 'examDate',
      width: 150,
      sorter: true,
    },
    {
      title: 'Result Letter Date',
      key: 'result_letter_date',
      dataIndex: 'resultLetterDate',
      width: 150,
      sorter: true,
    },
    {
      title: 'Planned Email Issuance Date',
      key: 'planned_email_issuance_date',
      dataIndex: 'plannedEmailIssuanceDate',
      width: 150,
      sorter: true,
    },
    {
      title: 'Location',
      key: 'location',
      dataIndex: 'location',
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
    getExamProfileList(tempPagination, {keyword});
  }, [pagination, keyword]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getExamProfileList(tempPagination, {keyword});
  }, [pagination, keyword]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'examProfileList':
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setData(content);
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
    getExamProfileList(pagination);
  }, []);

  const getExamProfileList = useCallback((pagination = {}, filter = {}) => {
    runExamProfileAPI('examProfileList', toQueryString(pagination, filter));
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

  const onSearchClicked = useCallback(async () => {
    if (keywordRef.current) {
      const value = keywordRef.current.input?.value;
      const resetPage = resetPagination();
      setKeyword(value);
      if (value) {
        await getExamProfileList(resetPage, { keyword: value });
      } else {
        await getExamProfileList(resetPage);
      }
    }
  }, [pagination, filterCondition, keyword, resetPagination]);


  return (
    <div className={styles['exam-profile-list']}
         permissionRequired={['Examination_Profile_Maintenance', 'Examination_Profile_View']}>
      <Typography.Title level={3}>Exam Profile</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <PermissionControl permissionRequired={['Examination_Profile_Maintenance']}>
          <Col>
            <Button type="primary" onClick={() => setOpen(true)}>Create</Button>
          </Col>
        </PermissionControl>
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
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Input.Search
            placeholder="Search ..."
            ref={keywordRef}
            onSearch={onSearchClicked}
            enterButton
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
              current={pagination.page}
              pageSize={pagination.pageSize}
              showTotal={(total) => `Total ${total} item(s)`}
              showSizeChanger
              showQuickJumper
            />

          </Col>
          <Col></Col>
        </Row>
        <br/>
      </Card>
      <ExamProfileFormModal
        type={TYPE.CREATE}
        open={open}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default ExamProfileList;