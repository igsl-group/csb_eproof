import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Upload, Form, Card, Typography, Breadcrumb, Grid, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined, SearchOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import Dropdown from "@/components/Dropdown";
import dayjs from "dayjs";
import { useModal } from "../../context/modal-provider";
import { useMessage } from "../../context/message-provider";
import OnholdModal from "./onhold-modal";
import CSVFileValidator from 'csv-file-validator';
import ImportModal from "./import-modal";

const Import = () =>  {
  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const [file, setFile] = useState(null);
  const [openImportModal, setImportModal] = useState(false);
  const {
    serialNo,
  } = useParams();
  const [open, setOpen] = useState(false)

  const [data, setData] = useState([
    {
      serialNo: 'N000000001',
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
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      passport: 'K34567893912',
      name: 'Yip Tai Man',
      email: 'taiman.yip@hotmail.com',
      ue: 'L2',
      uc: 'L2',
      at: 'Pass',
      blnst: 'Pass',
      status: 'Success',
    },
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700004',
      name: 'Wong Tai Man',
      email: 'taiman.wong@hotmail.com',
      ue: 'L2',
      uc: 'L1',
      at: 'Fail',
      blnst: 'Pass',
      status: 'Success',
    },
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700005',
      name: 'Lee Tai Man',
      email: 'taiman.lee@hotmail.com',
      ue: 'L2',
      uc: 'L1',
      at: 'Fail',
      blnst: 'Fail',
      status: 'Success',
    }
  ]);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
    setImportModal(false)
  });

  const columns = useMemo(() => [
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      width: 100,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passport',
      dataIndex: 'passport',
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
    {
      title: 'Email',
      key: 'email',
      dataIndex: 'email',
      width: 180,
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
    {
      title: 'Status',
      key: 'status',
      dataIndex: 'status',
      width: 100,
      sorter: true,
    },
  ], []);

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
      title: 'Certificate Issuance',
    },
    {
      title: 'Import Result (CSV)',
    },
  ], []);

  const onClickDispatch = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to dispatch to generate PDF stage?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Import Result (CSV)</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Row justify={'space-between'}>
        <Col>
          <Dropdown name={"serialNo"} label={'Serial No.'} size={12}/>
        </Col>
        <Col>
          <Row gutter={[16, 16]}>
            <Col>
              <Button type="primary" onClick={() => onClickDispatch()}>Dispatch to generate PDF</Button>
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
                    setImportModal(true);
                    setFile(file);
                  }
                  return false;
                }}
              >
                <Button type="primary" onClick={() => {
                }}>Import Result (CSV)</Button>
              </Upload>
            </Col>
          </Row>
        </Col>
      </Row>
      <br/>
      <fieldset style={{padding: '0 30px'}}>
        <legend><Typography.Title level={5}>Search</Typography.Title></legend>
        <Form
          layout="vertical"
          autoComplete="off"
          form={form}
          colon={false}
          scrollToFirstError={{
            behavior: 'smooth',
            block: 'center',
            inline: 'center',
          }}
          name="form"
        >
          <Row justify={'start'}>
            <Col span={20}>
              <Row gutter={24} justify={'start'}>
                <Col span={24} md={12}>
                  <HKID name={'hkid'} label={'HKID'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'passportNo'} label={'Passport'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'name'} label={'Candidate’s Name'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Email name={'email'} label={'Candidate’s Email'} size={12}/>
                </Col>
              </Row>
            </Col>
            <Col span={4}>
              <Row justify={'end'}>
                <Col>
                  <Button shape="circle" type={'primary'} icon={<SearchOutlined/>} onClick={() => {
                  }}/>
                </Col>
              </Row>
            </Col>
          </Row>
        </Form>
      </fieldset>
      <br/>
      <fieldset style={{paddingLeft: 30}}>
        <legend><Typography.Title level={5}>Workflow Summary</Typography.Title></legend>
        <Descriptions
          size={'small'}
          items={[
            {
              key: 1,
              label: 'Imported',
              children: 30000,
            },
            {
              key: 2,
              label: 'Generated PDF',
              children: '0 out of 0 failed',
            },
            {
              key: 3,
              label: 'Issued Cert.',
              children: '0 out of 0 failed',
            },
            {
              key: 4,
              label: 'Sent Email',
              children: '0 out of 0 failed',
            }
          ]}
        />
      </fieldset>
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
        title={'Import Result'}
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
      <OnholdModal
        open={open}
        onCloseCallback={onCloseCallback}
      />
      <ImportModal
        file={file}
        open={openImportModal}
        onCloseCallback={onCloseCallback}
      />
    </div>

  )
}

export default Import;