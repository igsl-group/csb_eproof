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
import EmailModal from "./notify-modal.jsx";
import {HKIDToString, stringToHKID, stringToHKIDWithBracket} from "../../components/HKID";
import {useAuth} from "../../context/auth-provider";

const Notify = () =>  {
  const auth = useAuth();
  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [searchForm] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [serialNoOptions, setSerialNoOptions] = useState([]);
  const [openImportModal, setImportModal] = useState(false);
  const [open, setOpen] = useState(false)
  const [isOnHold, setIsOnHold] = useState(false)
  const [summary, setSummary] = useState({});
  const [generatedData, setGeneratedData] = useState([]);
  const [record, setRecord] = useState({});
  const [filterCondition, setFilterCondition] = useState(null);

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    getImportList();
  },[]);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
  },[]);

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
                <Button disabled={!auth.permissions.includes('Certificate_Notify_Maintenance')} size={'small'} style={{width: 108}} type={'primary'} onClick={() => onClickDispatch(row)}>Dispatch</Button>
              </Col>
            ) : null
          }
          {
            ['PENDING'].includes(row.certStatus.code) ? (
              <Col span={24}>
                <Button disabled={!auth.permissions.includes('Certificate_Notify_Maintenance')} size={'small'} style={{width: 108}} type={'primary'} onClick={() => onClickNotifyModal(row)}>Notify</Button>
              </Col>
            ) : null
          }
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
      sorter: false,
    },
    {
      title: 'HKID',
      key: 'new_hkid',
      render: (row) => {
        return (
          <div>
            {
              row.oldHkid === row.newHkid ? (
                <span>{stringToHKIDWithBracket(row.newHkid)}</span>
              ) : (
                <div>
                  <div>{stringToHKIDWithBracket(row.oldHkid)}</div>
                  <div style={{ color: 'red'}}>{stringToHKIDWithBracket(row.newHkid) || '-'}</div>
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
      key: 'new_passport',
      render: (row) => {
        return (
          <div>
            {
              row.oldPassport === row.newPassport ? (
                <span>{row.newPassport}</span>
              ) : (
                <div>
                  <div>{row.oldPassport}</div>
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
      key: 'new_name',
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
      key: 'new_email',
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
      title: 'Letter Type',
      key: 'new_letter_type',
      render: (row) => {
        return (
          <div>
            {
              row.oldLetterType === row.newLetterType ? (
                <span>{row.newLetterType}</span>
              ) : (
                <div>
                  <div>{row.oldLetterType}</div>
                  <div style={{ color: 'red'}}>{row.newLetterType}</div>
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
      title: 'Status',
      key: 'status',
      dataIndex: 'certStatus',
      width: 120,
      render: (row) => <Tag>{row.label}</Tag>,
      sorter: true,
    },
  ], [auth.permissions]);

  const onClickNotifyModal = useCallback((row) => {
    setRecord(row);
    setOpen(true);
  }, [])

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
      title: 'Notify Candidate',
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
      title:'Are you sure to dispatch to complete stage?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certRenewDispatch', row.id, 'NOTIFY')
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

  const rowSelection = useMemo(() => ({
    selectedRowKeys,
    preserveSelectedRowKeys: true,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }), [selectedRowKeys]);

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
          messageApi.success('Generate certificates as PDF are in-progress, please wait a moment.');
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
    return runExamProfileAPI('certRenewList', 'NOTIFY', {
      dummy: "",
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
            if ((key !== 'newHkid' && payload[key]) || (key === 'newHkid' && (payload[key].id || payload[key].checkDigit))) {
              isEmpty = false;
              if (key === "newHkid") {
                finalPayload[key] = HKIDToString(payload[key])
              } else {
                finalPayload[key] = payload[key];
              }
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

  const resetPagination = useCallback(() => {
    const tempPagination = {
      ...pagination,
      total: 0,
      page: defaultPaginationInfo.page,
    //  pageSize: defaultPaginationInfo.pageSize,
    //  sortBy: defaultPaginationInfo.sortBy,
    //  orderBy: defaultPaginationInfo.orderBy,
    }
    setPagination(tempPagination);
    return tempPagination;
  }, [pagination]);


  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Notify Candidate</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
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
                  <HKID name={'newHkid'} label={'New HKID'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <Text name={'newPassport'} label={'New Passport'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <Text name={'newName'} label={"New Candidate's Name"} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={5}>
                  <Text name={'newEmail'} label={"New Candidate's Email"} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={4}>
                  <Dropdown
                    name={'newLetterType'}
                    label={'New Letter Type'}
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
              <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0 || !auth.permissions.includes('Certificate_Notify_Maintenance')}>Download
                Selected ({selectedRowKeys.length})</Button>
            </Col>
          </Row>
        </Col>
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
        title={'Notify Candidate'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'id'}
          rowSelection={!auth.permissions.includes('Certificate_Notify_Maintenance') ? null : {
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
      <EmailModal
        open={open}
        title={'Notify Email'}
        record={record}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default Notify;