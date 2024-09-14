import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
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
  SendOutlined,
  CloseOutlined
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import ResendEmailModal from "./modal";
import RevokeEmailModal from "./modal";
import { examProfileAPI } from '@/api/request';
import {
  toQueryString
} from "@/utils/util";
import {dataMapper} from "../pc/document-list/data-mapper";
import {HKIDToString, stringToHKIDWithBracket} from "../../components/HKID";

const CertificateManagementValid = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [searchForm] = Form.useForm();
  const [resendopen, setResendOpen] = useState(false);
  const [revokeOpen, setRevokeOpen] = useState(false);
  const [validCertData, setValidCertData] = useState([]);
  const [filterCondition, setFilterCondition] = useState(null);
  const {
    serialNo,
  } = useParams();

  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [data, setData] = useState([
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      examDate: '2024-01-01',
      resultLetterDate: '2024-01-25',
      emailIssuanceDate: '2024-01-31',
      ue: 'L2',
      uc: 'L1',
      at: 'Pass',
      blnst: 'Pass',
      status: 'Success',
    }
  ]);

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

  useEffect(() => {
    // searchForm.setFieldsValue({
    //   serialNo: 'N000000001',
    //   examDate: dayjs('2024-01-11'),
    //   plannedAnnouncedDate: dayjs('2024-01-11'),
    //   location: 'Hong Kong',
    // })
  }, []);

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
  ], []);

  const onClickDownload = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download certificate?',
      width: 500,
      okText: 'Confirm',

    });
  },[]);

  const onClickRevoke= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke certificate?',
      width: 500,
      okText: 'Confirm',
      onOk: () => {
        setRevokeOpen(true);
      }
    });
  },[]);

  const onClickRevokeSelected= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke certificate?',
      width: 500,
      okText: 'Confirm',
      onOk: () => {
        setRevokeOpen(true);
      }
    });
  },[]);

  const rowSelection = useMemo(() => ({
    selectedRowKeys,
    preserveSelectedRowKeys: true,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }), [selectedRowKeys]);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const columns = useMemo(() => [
    // {
    //   title: 'Action',
    //   key: 'action',
    //   width: 160,
    //   render: (row) => (
    //     <Space>
    //       <Button size={'small'} title={'Download'} icon={<DownloadOutlined />} onClick={onClickDownload}/>
    //       <Button size={'small'} title={'Revoke Cert.'} icon={<DeleteOutlined />} onClick={onClickRevoke}/>
    //       <Button size={'small'} title={'Copy URL'} icon={<CopyOutlined />} onClick={() => messageApi.success('URL is copied')}/>
    //       <Button size={'small'} title={'Resend Email'} icon={<SendOutlined />} onClick={() => setResendOpen(true)}/>
    //     </Space>
    //   )
    // },
    {
      title: 'Exam Date',
      key: 'exam_date',
      dataIndex: 'examDate',
      width: 140,
      sorter: true,
    },
    {
      title: 'HKID',
      key: 'hkid',
      width: 100,
      render: (row) => <Link to={`/CertificateManagement/Valid/Candidate?hkid=${row.hkid}`}>{stringToHKIDWithBracket(row.hkid)}</Link>,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passport_no',
      width: 100,
      render: (row) => <Link to={`/CertificateManagement/Valid/Candidate?passport=${row.passportNo}`}>{row.passportNo}</Link>,
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
      render: (row) => row.examProfile?.resultLetterDate,
      width: 180,
      sorter: false,
    },
    {
      title: 'Email Issuance Date',
      key: 'emailIssuanceDate',
      dataIndex: 'actualEmailSendTime',
      width: 180,
      // sorter: true,
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
    {
      title: 'Letter Type',
      key: 'letter_type',
      dataIndex: 'letterType',
      width: 80,
      sorter: true,
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
          setValidCertData(content);
          break;
        }
        default:
          break;
      }

    },
    onError: (error) => {
      //console.log(error.data)
      const message = error.data?.properties?.message || error.data?.detail || '';
      messageApi.error(message);
    },
    onFinally: (params, result, error) => {
    },
  });

  const onClickSearchButton = useCallback(
    async () => {
      const values = await searchForm
        .validateFields()
        .then((values) => ({
          ...values,
        }))
        .catch(() => false);

      if (values) {
        // const payload = dataMapperConvertPayload(dataMapper, TYPE.FILTER, values);
        const payload = values;
        const finalPayload = {};
        let isEmpty = true;
        for (let key in payload) {
          if (payload[key]) {
            isEmpty = false;
            if (key === "hkid") {
              finalPayload[key] = HKIDToString(payload[key])
            } else {
              finalPayload[key] = payload[key];
            }

          }
        }
        const resetPage = resetPagination();
        if (isEmpty) {
          setFilterCondition(null);
          await getCertList(resetPage);
        } else {
          await getCertList(resetPage, finalPayload);
          setFilterCondition(finalPayload);
        }
        // setOpen(false);
      }
    },
    [pagination, filterCondition]
  );

  const getCertList = useCallback(async (pagination = {}, filterCondition) => {
    return runExamProfileAPI('certList', 'VALID', filterCondition, toQueryString(pagination));
  }, []);

  const getAllCert = useCallback(async() => {
    await getCertList(pagination, filterCondition)
  }, [pagination, filterCondition])

  useEffect(() => {
    getAllCert();
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
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Certificate Management - Valid</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>

      <br/>
      <fieldset style={{padding: '0 30px'}}>
        <legend><Typography.Title level={5}>Search</Typography.Title></legend>
        <Form
          layout="vertical"
          autoComplete="off"
          form={searchForm}
          colon={false}
          scrollToFirstError={{
            behavior: 'smooth',
            block: 'center',
            inline: 'center',
          }}
          name="form"
        >
          <Row justify={'start'} gutter={[8, 8]}>
            <Col span={20}>
              <Row gutter={24} justify={'start'}>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <HKID name={'hkid'} label={'HKID'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <Text name={'passportNo'} label={'Passport'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <Text name={'canName'} label={'Candidate’s Name'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <Text name={'canEmail'} label={'Candidate’s Email'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={4}>
                  <Dropdown
                    name={'letterType'}
                    label={'Letter Type'}
                    size={50}
                    options={[
                      {
                        value: 'P',
                        label: 'P',
                      },
                      {
                        value: 'F',
                        label: 'F',
                      }
                    ]}
                  />
                </Col>
              </Row>
            </Col>
            <Col span={4}>
              <Row justify={'end'} gutter={[8, 8]}>
                <Col>
                  <Button shape="circle" icon={<CloseOutlined />} title={'Clean'} onClick={() => searchForm.resetFields()}/>
                </Col>
                <Col>
                  <Button
                    shape="circle"
                    type={filterCondition ? 'primary': 'default'}
                    icon={<SearchOutlined/>}
                    onClick={onClickSearchButton}
                  />
                </Col>
              </Row>
            </Col>
          </Row>
        </Form>
      </fieldset>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
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
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Certificate Management'}
      >
        <ResizeableTable
          size={'big'}
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={validCertData}
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
      <ResendEmailModal
        open={resendopen}
        title={'Resend Email'}
        onCloseCallback={() => setResendOpen(false)}
        onFinishCallback={() => setResendOpen(false)}
      />
      <RevokeEmailModal
        open={revokeOpen}
        title={'Revoke Email'}
        onCloseCallback={() => setRevokeOpen(false)}
        onFinishCallback={() => setRevokeOpen(false)}
      />

    </div>

  )
}

export default CertificateManagementValid;