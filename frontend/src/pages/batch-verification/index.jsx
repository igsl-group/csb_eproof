import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {
  Table,
  Badge,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Button,
  Space,
  Tabs,
  Col,
  Row,
  Descriptions,
  Modal,
  Pagination,
  Alert, Upload
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
  DownOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import dayjs from "dayjs";
import { examProfileAPI } from '@/api/request';
import {useMessage} from "../../context/message-provider";
import {useModal} from "../../context/modal-provider";
import {
  toQueryString
} from "@/utils/util";
import {download} from "../../utils/util";

const BatchVerification = () =>  {

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

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Other',
    },
    {
      title: 'Batch Verification',
    },
  ], []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'certEnquiryByCsv':
          download(response);
          messageApi.success('Download successfully.');
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

  return (
    <div className={styles['batch-verification']}>
      <Typography.Title level={3}>Batch Verification</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Row justify={'end'}  gutter={[16, 16]}>
        <Col>
          <Button
            type="primary"
            onClick={() => {
              window.open('/batch_verification_csv_template.csv', 'Download');
            }}
          >
            Download Template (CSV)
          </Button>
        </Col>
        <Col>
          <Upload
            accept={'text/csv'}
            multiple={false}
            maxCount={1}
            showUploadList={false}
            beforeUpload={async (file) => {
              if (file.type !== 'text/csv') {
                messageApi.error(`${file.name} is not a csv file`);
              } else {
                const values = {
                  file,
                }
                runExamProfileAPI("certEnquiryByCsv", values)
              }
              return false;
            }}
          >
            <Button type="primary" onClick={() => {
            }}>Import Enquiry (CSV)</Button>
          </Upload>
        </Col>
      </Row>
    </div>
  )
}

export default BatchVerification;