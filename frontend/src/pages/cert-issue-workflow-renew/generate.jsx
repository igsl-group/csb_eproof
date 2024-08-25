import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {
  Divider,
  Button,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Grid,
  Space,
  Tabs,
  Col,
  Row,
  Descriptions,
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
  AreaChartOutlined,
  DownloadOutlined, SearchOutlined, CloseOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import Dropdown from "@/components/Dropdown";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {download} from "../../utils/util";
import { examProfileAPI } from '@/api/request';
import {
  toQueryString
} from "@/utils/util";
import PermissionControl from "../../components/PermissionControl";

const Generate = () =>  {

  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [searchForm] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState('');
  const [serialNoOptions, setSerialNoOptions] = useState([]);
  const [openImportModal, setImportModal] = useState(false);
  const [open, setOpen] = useState(false)
  const [isOnHold, setIsOnHold] = useState(false)
  const [summary, setSummary] = useState({});
  const [generatedData, setGeneratedData] = useState([]);
  const [record, setRecord] = useState({});
  const [filterCondition, setFilterCondition] = useState(null);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 150,
      render: (row) => (
          <Row gutter={[8, 8]}>
            {
              ['SUCCESS'].includes(row.certStatus.code) ? (
                <Col span={24}>
                  <Button size={'small'} style={{width: 108}} type={'primary'} onClick={() => onClickDispatch(row)}>Dispatch</Button>
                </Col>
              ) : null
            }
            {
              ['PENDING', 'FAIL'].includes(row.certStatus.code) ? (
                <Col span={24}>
                  <Button size={'small'} style={{width: 108}} type={'primary'} onClick={() => onClickGeneratePdfCallback(row)}>Generate PDF</Button>
                </Col>
              ) : null
            }
            <Col span={24}>
              <Button size={'small'} danger style={{width: 108}} type={'primary'} onClick={() => onClickRemoveCallback(row)}>Remove</Button>
            </Col>
          </Row>
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
      title: 'Reason',
      key: 'remark',
      dataIndex: 'remark',
      width: 140,
      sorter: true,
    },
    {
      title: 'Exam Date',
      key: 'examDate',
      dataIndex: 'certInfo',
      render: (row) => row.examDate,
      width: 140,
      sorter: true,
    },
    {
      title: 'HKID',
      key: 'hkid',
      render: (row) => {
        return (
          <div>
            {
              row.oldHkid === row.newHkid ? (
                <span>{row.newHkid}</span>
              ) : (
                <div>
                  <div>{row.oldHkid}</div>
                  <div style={{ color: 'red'}}>{row.newHkid || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passport',
      render: (row) => {
        return (
          <div>
            {
              row.oldPassport === row.newPassport ? (
                <span>{row.newPassport}</span>
              ) : (
                <div>
                  <div>{row.newPassport}</div>
                  <div style={{ color: 'red'}}>{row.newPassport || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'Name',
      key: 'name',
      render: (row) => {
        return (
          <div>
            {
              row.oldName === row.newName ? (
                <span>{row.newName}</span>
              ) : (
                <div>
                  <div>{row.oldName}</div>
                  <div style={{ color: 'red'}}>{row.newName || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 160,
      sorter: true,
    },
    {
      title: 'Email',
      key: 'newEmail',
      dataIndex: 'newEmail',
      width: 180,
      sorter: true,
    },
    {
      title: 'UE',
      key: 'ue',
      render: (row) => {
        return (
          <div>
            {
              row.oldUeGrade === row.newUeGrade ? (
                <span>{row.newUeGrade}</span>
              ) : (
                <div>
                  <div>{row.oldUeGrade}</div>
                  <div style={{ color: 'red'}}>{row.newUeGrade || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'UC',
      key: 'uc',
      render: (row) => {
        return (
          <div>
            {
              row.oldUcGrade === row.newUcGrade ? (
                <span>{row.newUcGrade}</span>
              ) : (
                <div>
                  <div>{row.oldUcGrade}</div>
                  <div style={{ color: 'red'}}>{row.newUcGrade || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'AT',
      key: 'at',
      render: (row) => {
        return (
          <div>
            {
              row.oldAtGrade === row.newAtGrade ? (
                <span>{row.newAtGrade}</span>
              ) : (
                <div>
                  <div>{row.oldAtGrade}</div>
                  <div style={{ color: 'red'}}>{row.newAtGrade || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'BLNST',
      key: 'blnst',
      render: (row) => {
        return (
          <div>
            {
              row.oldBlGrade === row.newBlGrade ? (
                <span>{row.newBlGrade}</span>
              ) : (
                <div>
                  <div>{row.oldBlGrade}</div>
                  <div style={{ color: 'red'}}>{row.newBlGrade || '-'}</div>
                </div>
              )
            }
          </div>
        )
      },
      width: 100,
      sorter: true,
    },
    {
      title: 'Status',
      key: 'certStatus',
      dataIndex: 'certStatus',
      width: 120,
      render: (row) => <Tag>{row.label}</Tag>,
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
    getCertList(tempPagination, filterCondition);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getCertList(tempPagination, filterCondition);
  }, [pagination]);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Certificate Reissuance',
    },
    {
      title: 'Generate PDF',
    },
  ], []);

  const onClickRemoveCallback = useCallback((row) => {
    modalApi.confirm({
      title:'Are you sure to remove case?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certRenewDelete', row.id)
    });
  }, []);

  const onClickGeneratePdfCallback = useCallback((row) => {
    modalApi.confirm({
      title:'Are you sure to generate PDF?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certRenewGenerate', row.id),
    });

  },[]);

  const onClickDispatch = useCallback((row) => {
    modalApi.confirm({
      title:'Are you sure to dispatch to sign and issue Cert. stage?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certRenewDispatch', row.id, 'GENERATED')
    });
  },[]);

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
      onOk: () => runExamProfileAPI('certRenewBulkDownload', selectedRowKeys.join(','))
    });
  },[selectedRowKeys]);

  const rowSelection = useCallback({
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }, []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'certRenewList':
        {
          // const data = response.data || {};
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setGeneratedData(content);
          break;
        }
        case 'certRenewDispatch':
          messageApi.success('Dispatch successfully.');
          getImportList();
          break;
        case 'certRenewBulkDownload':
          download(response);
          messageApi.success('Download successfully.');
          break;
        case 'certRenewGenerate':
          messageApi.success('Generate certificates as PDF successfully.');
          getImportList();
          break;
        case 'certRenewDelete':
          getImportList();
          messageApi.success('Case removed successfully.');
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
    getImportList();
  }, []);

  const getCertList = useCallback(async (pagination = {}, filterCondition = {}) => {
    return runExamProfileAPI('certRenewList', 'GENERATED', {
      ...filterCondition,
    }, toQueryString(pagination));
  }, []);

  const getImportList = useCallback(async() => {
    await getCertList(pagination, filterCondition);
  }, [pagination, filterCondition]);

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
            finalPayload[key] = payload[key];
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
      <Typography.Title level={3}>Generate PDF</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      {/*<Row justify={'end'}>*/}
      {/*  <Col>*/}
      {/*    <Row gutter={[16, 16]} justify={'end'}>*/}
      {/*      <Col>*/}
      {/*        <Button type="primary" onClick={onClickDispatch}>Dispatch to sign and issue Cert.</Button>*/}
      {/*      </Col>*/}
      {/*    </Row>*/}
      {/*  </Col>*/}
      {/*</Row>*/}
      {/*<br/>*/}
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
                <Col span={24} md={12} xl={8} xxl={6}>
                  <d  name={'newHkid'} label={'New HKID'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={6}>
                  <Text name={'newPassport'} label={'New Passport'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={6}>
                  <Text name={'newName'} label={'New Candidate’s Name'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={6}>
                  <Text name={'newEmail'} label={'Candidate’s Email'} size={50}/>
                </Col>
              </Row>
            </Col>
            <Col span={4}>
              <Row justify={'end'} gutter={[8, 8]}>
                <Col>
                  <Button shape="circle" icon={<CloseOutlined/>} title={'Clean'}
                          onClick={() => searchForm.resetFields()}/>
                </Col>
                <Col>
                  <Button
                    shape="circle"
                    type={filterCondition ? 'primary' : 'default'}
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
          <Row gutter={[16, 16]} justify={'end'}>
            <Col>
              <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
                Selected ({selectedRowKeys.length})</Button>
            </Col>
          </Row>
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
        title={'Generate PDF'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'id'}
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
          dataSource={generatedData}
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
    </div>

  )
}

export default Generate;