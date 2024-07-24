import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Upload, Form, Card, Typography, Breadcrumb, Tag, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined, SearchOutlined, CloseOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import Dropdown from "@/components/Dropdown";
import dayjs from "dayjs";
import { useModal } from "../../context/modal-provider";
import { useMessage } from "../../context/message-provider";
import OnHoldModal from "./onhold-modal";
import CSVFileValidator from 'csv-file-validator';
import ImportModal from "./import-modal";
import { examProfileAPI } from '@/api/request';
import {
  toQueryString
} from "@/utils/util";

const Import = () =>  {

  const navigate = useNavigate();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [searchForm] = Form.useForm();
  const [serialNoForm] = Form.useForm();
  const serialNoValue = Form.useWatch('serialNo', serialNoForm);
  const [file, setFile] = useState(null);
  const [serialNoOptions, setSerialNoOptions] = useState([]);
  const [openImportModal, setImportModal] = useState(false);
  const [open, setOpen] = useState(false);
  const [isOnHold, setIsOnHold] = useState(false);
  const [summary, setSummary] = useState({});
  const [importedData, setImportedData] = useState([]);
  const [record, setRecord] = useState({});
  const [filterCondition, setFilterCondition] = useState(null);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
    setImportModal(false)
  });

  const onFinishCallback = useCallback(async () => {
    setOpen(false);
    setImportModal(false);
    getImportListAndSummary();
  }, []);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 100,
      render: (row) => {
        return (
          <div>
            { row.onHold ? <Button size={'small'} type={'primary'} danger onClick={() => onResumeClickCallback(row)}>Resume</Button> : null}
            { !row.onHold ? <Button size={'small'} type={'primary'} onClick={() => onOnHoldClickCallback(row)}>On hold</Button> : null}
          </div>
        )
      }
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
      key: 'passportNo',
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
      title: 'Status',
      key: 'certStatus',
      dataIndex: 'certStatus',
      width: 100,
      render: (row) => <Tag>{row.code}</Tag>,
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
  
  // useEffect(() => {
  //   form.setFieldsValue({
  //     serialNo: 'N000000001',
  //     examDate: dayjs('2024-01-11'),
  //     plannedAnnouncedDate: dayjs('2024-01-11'),
  //     location: 'Hong Kong',
  //   })
  // }, []);

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

  const onClickOnHold = useCallback((recordId) => {
    modalApi.confirm({
      title:'Are you sure to on-hold the case?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceHold', recordId, )
    });
  },[serialNoValue]);


  const onClickDispatch = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to dispatch to generate PDF stage?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceDispatch', serialNoValue, 'IMPORTED')
    });
  },[serialNoValue]);


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
            serialNoForm.setFieldValue('serialNo', options[0].value);
          }
          break;
        }
        case 'certList':
        {
          // const data = response.data || {};
          const data = response.data || {};
          const content = data.content || [];
          setPagination({
            ...pagination,
            total: data.totalElements,
          });
          setImportedData(content);
          break;
        }
        case 'certIssuanceDispatch':
          messageApi.success('Dispatch successfully.');
          await getExamProfileSummary(serialNoValue);
          await getCertList(serialNoValue, pagination, filterCondition);
          getImportListAndSummary();
          break;
        case 'certIssuanceHold':
          messageApi.success('Hold case successfully.');
          // await getExamProfileSummary(serialNoValue);
          // await getCertList(serialNoValue, pagination, filterCondition);
          getImportListAndSummary();
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

  const getCertList = useCallback(async (serialNoValue, pagination = {}, filterCondition) => {
    return runExamProfileAPI('certList', 'IMPORTED', {
      ...filterCondition,
      examProfileSerialNo: serialNoValue,
      onHold: false,
    }, toQueryString(pagination));
  }, []);
console.log('serialNoValue', serialNoValue)
  const getImportListAndSummary = useCallback(async() => {
    console.log("1", serialNoValue)
    if (serialNoValue) {
      console.log("2", serialNoValue)
      await getExamProfileSummary(serialNoValue);
      await getCertList(serialNoValue, pagination, filterCondition);
    }
  }, [serialNoValue, pagination, filterCondition]);

  const onClickSearchButton = useCallback(
    async () => {
      const values = await searchForm
        .validateFields()
        .then((values) => ({
          ...values,
          hkid: values.hkid?.id && values.hkid?.checkDigit  ? `${values.hkid?.id}${values.hkid.checkDigit}` : '',
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
        console.log(finalPayload)
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


  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Import Result (CSV)</Typography.Title>
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
          <Col>
            <Dropdown
              name={"serialNo"}
              label={'Serial No.'}
              size={20}
              options={serialNoOptions}
              allowClear={false}
              onChange={(values) => console.log(values)}
            />
          </Col>
          <Col>
            <Row gutter={[16, 16]}>
              <Col>
                <Button type="primary" onClick={() => onClickDispatch()}>Dispatch to generate PDF</Button>
              </Col>
              <Col>
                <Button
                  type="primary"
                  onClick={() => {
                    window.open('/import_csv_template.csv', 'Download');
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
                <Col span={24} md={12} xl={8} xxl={6}>
                  <HKID name={'hkid'} label={'HKID'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={6}>
                  <Text name={'passportNo'} label={'Passport'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={6}>
                  <Text name={'canName'} label={'Candidate’s Name'} size={50}/>
                </Col>
                <Col span={24} md={12} xl={8} xxl={6}>
                  <Email name={'email'} label={'Candidate’s Email'} size={50}/>
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
      <fieldset style={{paddingLeft: 30}}>
        <legend><Typography.Title level={5}>Workflow Summary</Typography.Title></legend>
        <Descriptions
          size={'small'}
          items={[
            {
              key: 1,
              label: 'Imported',
              children: summary.imported,
            },
            {
              key: 2,
              label: 'Generated PDF',
              children: `${summary.generatePdfFailed} out of ${summary.generatePdfTotal} failed`,
            },
            {
              key: 3,
              label: 'Issued Cert.',
              children: `${summary.issuedPdfFailed} out of ${summary.issuedPdfTotal} failed`,
            },
            {
              key: 4,
              label: 'Sent Email',
              children: `${summary.sendEmailFailed} out of ${summary.sendEmailTotal} failed`,

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
          dataSource={importedData}
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
      <OnHoldModal
        open={open}
        record={record}
        isOnHold={isOnHold}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
      <ImportModal
        file={file}
        recordId={serialNoValue}
        open={openImportModal}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
    </div>

  )
}

export default Import;