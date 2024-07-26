import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams, useSearchParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Divider, Form, Card, Typography, Breadcrumb, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  DeleteOutlined,
  DownloadOutlined,
  CopyOutlined,
  SendOutlined
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import HKID, { stringToHKID } from "@/components/HKID";
import Email from "@/components/Email";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import PersonalParticularsModal from "./personal-particulars-modal";
import AppealModal from "./appeal-modal";
import RevokeCertModal from "./revoke-modal";
import { examProfileAPI } from '@/api/request';
import {
  toQueryString
} from "@/utils/util";
import {download} from "../../utils/util";

const Candidate = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [revokeOpen, setRevokeOpen] = useState(false);
  const [openAppealModal, setOpenAppealModal] = useState(false);
  const [searchParams, setSearchParams] = useSearchParams();
  const hkid = searchParams.get("hkid");
  const passport = searchParams.get("passport");
  const [selectedRowKeys, setSelectedRowKeys] = useState('');
  const [filterCondition, setFilterCondition] = useState(null);
  const [validCertCandidateData, setValidCandidateCertData] = useState([]);

  const email = Form.useWatch('email', form);
  const emailError = useMemo(() => !/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email), [email]);

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
    getCertList(tempPagination, filterCondition);
  }, [pagination, filterCondition]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getCertList(tempPagination, filterCondition);
  }, [pagination, filterCondition]);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Certificate Management',
    },
    {
      title: 'Valid',
    },
    {
      title: 'Candidate',
    },
    {
      title: hkid,
    },
  ], []);

  const onClickDownload = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickRevoke= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 160,
      render: (row) => (
        <Row gutter={[8, 8]}>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => setOpenAppealModal(true)}>Update Result</Button></Col>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => messageApi.success('URL is copied')}>Copy URL</Button></Col>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => {}}>Resend Email</Button></Col>
        </Row>
      )
    },
    {
      title: 'Exam Date',
      key: 'examDate',
      dataIndex: 'examDate',
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
      title: 'Result Letter Date',
      key: 'resultLetterDate',
      dataIndex: 'resultLetterDate',
      width: 180,
      sorter: true,
    },
    {
      title: 'Email Issuance Date',
      key: 'emailIssuanceDate',
      dataIndex: 'emailIssuanceDate',
      width: 180,
      sorter: true,
    },
    {
      title: 'UE',
      key: 'ueGrade',
      dataIndex: 'ueGrade',
      width: 80,
    },
    {
      title: 'UC',
      key: 'ucGrade',
      dataIndex: 'ucGrade',
      width: 80,
    },
    {
      title: 'AT',
      key: 'atGrade',
      dataIndex: 'atGrade',
      width: 80,
    },
    {
      title: 'BLNST',
      key: 'blnstGrade',
      dataIndex: 'blnstGrade',
      width: 80,
    },
  ], []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'certList':
        {
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setValidCandidateCertData(content);
          break;
        }
        case 'certLatestCandidateInfo':
          const data = response.data || {};
          const content = data.content || [];
          if (content.length > 0) {
            form.setFieldsValue({
              ...content[0],
              hkid: stringToHKID(content[0].hkid),
            })
          }
          break;
        case 'certBatchUpdateEmail':
          getCandidateCertList();
          messageApi.success('Update successfully.');
          form.setFieldValue('email', '');
          break;
        case 'certIssuanceBulkDownload':
          download(response);
          messageApi.success('Download successfully.');
          break;
        default:
          break;
      }

    },
    onError: (error) => {
      console.log(error.data)
      const message = error.data?.properties?.message || error.data?.detail || '';
      messageApi.error(message);
    },
    onFinally: (params, result, error) => {
    },
  });

  const rowSelection = useCallback({
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }, []);

  const onClickUpdatePersonalParticulars = useCallback(() => {
    setOpen(true);
  }, []);

  const onClickBulkUpdateEmail = useCallback(() => {
    modalApi.confirm({
      title: `Are you sure to bulk update email to ${email}?`,
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certBatchUpdateEmail', {
        currentHkid: hkid,
        currentPassport: hkid ? '': passport,
        email,
      })
    });
  }, [email, hkid, passport]);

  const onClickDownloadAll = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download all PDF?',
      width: 500,
      okText: 'Confirm',

    });
  },[]);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceBulkDownload', selectedRowKeys.join(','))
    });
  },[selectedRowKeys]);

  const onClickRevokeSelected = useCallback(() => {
    setRevokeOpen(true);
  },[]);

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    setOpenAppealModal(false);
  },[]);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
    setOpenAppealModal(false);
  },[]);

  const getCertList = useCallback(async (pagination = {}, filter = {}) => {

    return runExamProfileAPI('certList', 'VALID', {
      hkid: hkid,
      passportNo: hkid ? '': passport,
    }, toQueryString(pagination, filter));
  }, [hkid, passport]);

  const getLatestCandidateInfo = useCallback(async (pagination = {}, filter = {}) => {

    return runExamProfileAPI('certLatestCandidateInfo', {
      hkid: hkid,
      passportNo: hkid ? '': passport,
    }, toQueryString({
      page: 1,
      pageSize: 1,
      sortBy: 'id',
      orderBy: 'descend',
    }, filter));
  }, [hkid, passport]);

  useEffect(() => {
    getCandidateCertList()
      .then(() => getLatestCandidateInfo())

  }, []);

  const getCandidateCertList = useCallback(async() => {
    await getCertList(pagination, filterCondition);
  }, [pagination, filterCondition]);

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
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Candidate</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <br/>
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
          <Col span={16}>
            <Row gutter={24} justify={'start'}>
              <Col span={24} md={12}>
                <HKID name={'hkid'} label={'HKID'} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Text name={'passportNo'} label={'Passport No.'} size={12} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Text name={'name'} label={'Candidate Name'} size={12} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Space>
                  <Email name={'email'} label={'Email'} size={12}/>
                  <Button type={'primary'}  onClick={onClickBulkUpdateEmail} disabled={emailError}>Bulk Update Email</Button>
                </Space>
              </Col>
            </Row>
          </Col>
          <Col span={8}>
            <Row gutter={[8, 8]} justify={'end'}>
              <Col>
                <Button type={'primary'} onClick={onClickUpdatePersonalParticulars}>Update Personal Particulars</Button>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
            Selected ({selectedRowKeys.length})</Button>
        </Col>
        <Col>
          <Button type="primary" onClick={onClickRevokeSelected} disabled={selectedRowKeys.length === 0}>Revoke
            Selected ({selectedRowKeys.length})</Button>
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
      <br/>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Certificate Management'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'candidateNo'}
          rowSelection={{
            type: 'checkbox',
            ...rowSelection,
          }}
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={validCertCandidateData}
        />
        <br/>
        <Row justify={'end'}>
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
      <PersonalParticularsModal
        open={open}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
        title={'Update Personal Particulars'}
      />
      <AppealModal
        open={openAppealModal}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
        title={'Update Result'}
      />
      <RevokeCertModal
          open={revokeOpen}
          title={'Revoke Certificate'}
          onCloseCallback={() => setRevokeOpen(false)}
          onFinishCallback={() => setRevokeOpen(false)}
      />
    </div>

  )
}

export default Candidate;