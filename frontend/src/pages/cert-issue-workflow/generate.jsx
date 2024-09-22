import React, { useRef, useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams, useSearchParams, useAsyncError} from "react-router-dom";
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
  Upload, Tag
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  MinusCircleOutlined,
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
import OnHoldModal from "./onhold-modal";
import {useMessage} from "../../context/message-provider";
import { examProfileAPI } from '@/api/request';
import {download} from "../../utils/util";
import {
  toQueryString
} from "@/utils/util";
import PermissionControl from "../../components/PermissionControl";
import ExamProfileSummary from "../../components/ExamProfileSummary";
import {HKIDToString, stringToHKID, stringToHKIDWithBracket} from "../../components/HKID";
import RandomPreviewModal from "./random-preview-modal";

const Generate = () =>  {

  const ref = useRef(null);
  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [searchForm] = Form.useForm();
  const [serialNoForm] = Form.useForm();
  const [searchParams, setSearchParams] = useSearchParams();
  // const serialNoValue = searchParams.get("serialNo");
  const serialNoValue = Form.useWatch('serialNo', serialNoForm);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [serialNoOptions, setSerialNoOptions] = useState([]);
  const [openImportModal, setImportModal] = useState(false);
  const [open, setOpen] = useState(false)
  const [previewPdfOpen, setPreviewPdfOpen] = useState(false)
  const [isOnHold, setIsOnHold] = useState(false)
  const [summary, setSummary] = useState({});
  const [generatedData, setGeneratedData] = useState([]);
  const [record, setRecord] = useState({});
  const [filterCondition, setFilterCondition] = useState(null);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
    setPreviewPdfOpen(false);
    setImportModal(false)
  });

  const onFinishCallback = useCallback(async () => {
    setOpen(false);
    setImportModal(false);
    getImportListAndSummary();
  }, [serialNoValue]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 100,
      render: (row) => {
        return (
          <div>
            { row.onHold ? <Tag color="default">On-hold</Tag> : null}
            { !row.onHold ? <Button size={'small'} type={'primary'} onClick={() => onOnHoldClickCallback(row)}>On hold</Button> : null}
          </div>
        )
      }
    },
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      render: (row) => stringToHKIDWithBracket(row),
      width: 100,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passport_no',
      dataIndex: 'passportNo',
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
    {
      title: 'Status',
      key: 'status',
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
    getCertList(serialNoValue, tempPagination, filterCondition);
  }, [serialNoValue, pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getCertList(serialNoValue, tempPagination, filterCondition);
  }, [serialNoValue, pagination]);

  const onOnHoldClickCallback = useCallback((row) => {
    setRecord(row);
    setOpen(true);
    setIsOnHold(true);
  }, []);

  const onResumeClickCallback = useCallback((row) => {
    setRecord(row);
    setOpen(true);
    setIsOnHold(false);
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Certificate Issuance',
    },
    {
      title: 'Generate PDF',
    },
  ], []);

  const onClickGeneratePdf = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to generate PDF?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceGenerate', serialNoValue),
    });

  },[serialNoValue]);

  const onClickDispatch = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to dispatch to sign and issue Cert. stage?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceDispatch', serialNoValue, 'GENERATED')
    });
  },[serialNoValue]);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceBulkDownload', selectedRowKeys.join(','))
    });
  },[selectedRowKeys]);

  const rowSelection = useMemo(() => ({
    selectedRowKeys,
    preserveSelectedRowKeys: true,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }), [selectedRowKeys]);

  console.log("selectedRowKeys", selectedRowKeys);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'examProfileList':

          break;
        case 'examProfileGet':
        {

          break;
        }
        case 'examProfileSummaryGet':
        {
          const data = response.data || {};
          setSummary(data);
          break;
        }
        case 'examProfileDropdown':
        {
          const data = response.data || [];
          const options = data.flatMap((row) => ({
            value: row.serialNo,
            label: row.serialNo,
          }))
          setSerialNoOptions(options);
          if (options.length > 0) {
            const value = searchParams.get("serialNo") || options[0].value
            updateCurrentSerialNo(value);
          }
          break;
        }
        case 'certListBackground':
        case 'certList':
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
        case 'certIssuanceDispatch':
          messageApi.success('Dispatch successfully.');
          getImportListAndSummary();
          break;
        case 'certIssuanceBulkDownload':
          download(response);
          messageApi.success('Download successfully.');
          break;
        case 'certIssuanceGenerate':
          messageApi.success('Generate certificates as PDF are in-progress, please wait a moment.');
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
    getImportListAndSummary();
  }, [serialNoValue]);

  useEffect(() => {
    runExamProfileAPI('examProfileDropdown');
  }, []);

  const getExamProfileSummary = useCallback(async (serialNoValue) => {
    return runExamProfileAPI('examProfileSummaryGet', serialNoValue);
  }, []);

  const getCertList = useCallback(async (serialNoValue, pagination = {}, filterCondition = {}) => {
    return runExamProfileAPI('certList', 'GENERATED', {
      ...filterCondition,
      examProfileSerialNo: serialNoValue,
    }, toQueryString(pagination));
  }, []);

  const getImportListAndSummary = useCallback(async() => {
    if (serialNoValue) {
      updateSummary();
      await getCertList(serialNoValue, pagination, filterCondition);
    }
  }, [serialNoValue, pagination, filterCondition]);

  const onClickSearchButton = useCallback(
    async () => {
      const values = await searchForm
        .validateFields()
        .then((values) => ({
          ...values,
        }))
        .catch(() => false);

      if (values) {
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
          await getCertList(serialNoValue, resetPage);
        } else {
          await getCertList(serialNoValue, resetPage, finalPayload);
          setFilterCondition(finalPayload);
        }
        // setOpen(false);
      }
    },
    [serialNoValue, pagination, filterCondition]
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

  const updateCurrentSerialNo = useCallback((value) => {
    navigate({
      search: `?serialNo=${value}`,
    });
    serialNoForm.setFieldValue('serialNo', value);
    setSelectedRowKeys([]);
    resetPagination();
  }, [])

  const updateSummary = () => {
    if (ref.current) {
      ref.current.updateSummary();
    }
  };

  return (
    <div className={styles['exam-profile']} permissionRequired={['Certificate_Generate']}>
      <Typography.Title level={3}>Generate PDF</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <Form
        layout="horizontal"
        autoComplete="off"
        form={serialNoForm}
        colon={true}
        scrollToFirstError={{
          behavior: 'smooth',
          block: 'center',
          inline: 'center',
        }}
        name="serialNoForm"
      >
        <Row justify={'space-between'}>
          <Col span={8} >
            <Dropdown
              name={"serialNo"}
              label={'Serial No.'}
              size={20}
              options={serialNoOptions}
              allowClear={false}
              onChange={(value) => updateCurrentSerialNo(value)}
            />
          </Col>
          <Col span={16}>
            <Row gutter={[16, 16]} justify={'end'}>
              <Col>
                <Button disabled={generatedData.length === 0 || ref.current?.summary?.generatePdfSuccess === 0} type="primary" onClick={onClickDispatch}>Dispatch to sign and issue Cert.</Button>
              </Col>
              <Col>
                <Button disabled={generatedData.length === 0} type="primary" onClick={onClickGeneratePdf}>Generate PDF</Button>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
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
      <ExamProfileSummary ref={ref} serialNo={serialNoValue}/>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Row gutter={[16, 16]} justify={'end'}>
            <Col>
              <Button type="primary" onClick={() => setPreviewPdfOpen(true)} disabled={generatedData.length === 0}>Random Check</Button>
            </Col>
            <Col>
              <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
                Selected ({selectedRowKeys.length})</Button>
            </Col>
            {/*<Col>*/}
            {/*  <Button type="primary" onClick={onClickDownloadAll}>Download All</Button>*/}
            {/*</Col>*/}
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
      <OnHoldModal
        open={open}
        record={record}
        isOnHold={isOnHold}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
      <RandomPreviewModal
        recordId={serialNoValue}
        open={previewPdfOpen}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default Generate;