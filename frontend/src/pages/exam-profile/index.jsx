import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
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
  Tabs,
  Col,
  Row,
  Descriptions,
  Modal,
  Pagination,
  Button,
  Flex
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
  AreaChartOutlined, SearchOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import Import from "./import";
import Generate from "./generate";
import Issue from "./issue";
import Notify from "./notify";
import dayjs from "dayjs";
import ExceptionalCaseModal from "./exceptional-case-modal";
import {useModal} from "../../context/modal-provider";
import ExamProfileFormModal from "./modal";
import { examProfileAPI } from '@/api/request';
import {any} from "joi";
import {TYPE} from '@/config/enum';
import {useMessage} from "../../context/message-provider";

const ExamProfile = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [freezeExamProfile, setFreezeExamProfile] = useState(false);
  const [form] = Form.useForm();
  const [summary, setSummary] = useState({});
  const {
    serialNo,
  } = useParams();

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

  const [data, setData] = useState([
    {
      serialNo: 'N000000001',
      reason: 'Checking',
      candidateNo: 'C000001',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
      ue: 'L2',
      uc: 'L1',
      at: 'Pass',
      blnst: 'Pass',
      status: 'Success',
      stage: 'Import',
    }
  ]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 140,
      render: (row) => (
        <Row gutter={[8, 8]}>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => {}}>Resume Case</Button></Col>
          <Col span={24}><Button size={'small'} type={'primary'} danger onClick={() => {}}>Remove Case</Button></Col>
        </Row>
      )
    },
    {
      title: 'Current Stage',
      key: 'stage',
      dataIndex: 'stage',
      width: 130,
      sorter: true,
    },
    {
      title: 'Reason',
      key: 'reason',
      dataIndex: 'reason',
      width: 100,
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

  ], []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'examProfileList':
          const data = response.data || {};
          const content = data.content || [];
          setData(content);
          break;
        case 'examProfileGet':
        {
          const data = response.data || {};
          form.setFieldsValue({
            ...data,
          });
          setFreezeExamProfile(data.isFreezed);
          getExamProfileSummary();
          break;
        }
        case 'examProfileSummaryGet':
        {
          const data = response.data || {};
          setSummary(data);
          break;
        }
        case 'examProfileFreeze':

        {
          getExamProfile();
          messageApi.success('Freeze successfully.');
          break;
        }
        case 'examProfileUnfreeze':
        {
          getExamProfile();
          messageApi.success('Un-freeze successfully.');
          break;
        }
        case 'examProfileReset':
        {
          getExamProfileSummary();
          messageApi.success('Reset successfully.');
          break;
        }
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


  const onCloseCallback = useCallback(() => {
    setOpen(false);
  });

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    getExamProfile();
  }, []);

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

  const getExamProfile = useCallback(async () => {
    return runExamProfileAPI('examProfileGet', serialNo);
  }, [serialNo]);

  const getExamProfileSummary = useCallback(async () => {
    return runExamProfileAPI('examProfileSummaryGet', serialNo);
  }, [serialNo]);

  useEffect(() => {
    (async () => {
      await getExamProfile();
      await getExamProfileSummary();
    })()
  }, []);

  const tabItems = useMemo(() => [
    {
      key: 1,
      label: 'Import Result',
      children: <Import />,
    },
    {
      key: 2,
      label: 'Generate PDF',
      children: <Generate />,
    },
    {
      key: 3,
      label: 'Sign and Issue Certificate',
      children: <Issue />,
    },
    {
      key: 4,
      label: 'Notify Candidate',
      children: <Notify />,
    },
  ], []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Exam Profile',
    },
    {
      title: serialNo,
    },
  ], [serialNo]);

  const onClickReset = useCallback(() => {
    modalApi.confirm({
      title:'If you confirm to reset Exam Profile, all imported results, generated PDFs, signed and issued certificates will be removed.',
      width: 500,
      okText: 'Confirm',
      onOk: () => {
        runExamProfileAPI('examProfileReset', serialNo)
      }
    });
  },[serialNo]);

  const freezeExamProfileCallback = useCallback(() => {
    if (freezeExamProfile) {
      runExamProfileAPI('examProfileUnfreeze', serialNo);
    } else {
      runExamProfileAPI('examProfileFreeze', serialNo);
    }

  }, [freezeExamProfile])

  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Exam Profile</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
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
        <Row justify={'start'} gutter={[8, 8]}>
          <Col span={20} >
            <Row gutter={24} justify={'start'}>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={"serialNo"} label={'Serial No.'} size={50} disabled/>
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'examDate'} label={'Exam Date'} size={50} disabled placeholder={'YYYY-MM-DD'} size={50} />
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'resultLetterDate'} label={'Result Letter Date'} disabled={true}
                      placeholder={'YYYY-MM-DD'} size={50}/>
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'plannedEmailIssuanceDate'} label={'Planned Email Issuance Date'} disabled={true}
                      placeholder={'YYYY-MM-DD'} size={50}/>
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'examDate'} label={'Effective Date'} disabled placeholder={'YYYY-MM-DD'} size={50} />
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'actualAnnouncedDate'} label={'Actual Email Issuance Date (From)'} disabled={true}
                      placeholder={'YYYY-MM-DD'} size={50}/>
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'actualAnnouncedDate'} label={'Actual Email Issuance Date (To)'} disabled={true}
                      placeholder={'YYYY-MM-DD'} size={50}/>
              </Col>
              <Col span={24} md={12} xl={8} xxl={6}>
                <Text name={'location'} label={'Location'} size={100} disabled/>
              </Col>
            </Row>
          </Col>
          <Col span={4}>
            <Row gutter={[8, 8]} justify={'end'}>
              <Col span={24}>
                <Row justify={'end'}>
                  <Col>
                    <Button style={{ width: 115}} type={'primary'} onClick={() => setOpen(true)}>Edit</Button>
                  </Col>
                </Row>
              </Col>
              <Col span={24}>
                <Row justify={'end'}>
                  <Col>
                    <Button
                      style={{ width: 115}}
                      type={'primary'}
                      onClick={onClickReset}
                    >
                      Reset
                    </Button>
                  </Col>
                </Row>
              </Col>
              <Col span={24}>
                <Row justify={'end'}>
                  <Col>
                    <Button
                      style={{ width: 115}}
                      type={'primary'}
                      danger={freezeExamProfile}
                      // onClick={() => runExamProfileAPI('examProfileFreeze', serialNo)}
                      onClick={freezeExamProfileCallback}
                    >
                      {!freezeExamProfile ? 'Freeze' : 'Un-freeze'}
                    </Button>
                  </Col>
                </Row>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
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
              children: `${summary.generatePdfTotal} out of ${summary.generatePdfFailed} failed`,
            },
            {
              key: 3,
              label: 'Issued Cert.',
              children: `${summary.issuedPdfTotal} out of ${summary.issuedPdfFailed} failed`,
            },
            {
              key: 4,
              label: 'Sent Email',
              children: `${summary.sendEmailTotal} out of ${summary.sendEmailFailed} failed`,

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
        title={'On Hold Case'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'candidateNo'}
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
      <ExamProfileFormModal
        type={TYPE.EDIT}
        open={open}
        recordId={serialNo}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
      />
      {/*<fieldset style={{paddingLeft: 30}}>*/}
      {/*  <legend><Typography.Title level={5}>Workflow Summary</Typography.Title></legend>*/}
      {/*  <Descriptions*/}
      {/*    size={'small'}*/}
      {/*    items={[*/}
      {/*      {*/}
      {/*        key: 1,*/}
      {/*        label: 'Imported',*/}
      {/*        children: 30000,*/}
      {/*      },*/}
      {/*      {*/}
      {/*        key: 2,*/}
      {/*        label: 'Generated PDF',*/}
      {/*        children: '0 out of 0 failed',*/}
      {/*      },*/}
      {/*      {*/}
      {/*        key: 3,*/}
      {/*        label: 'Issued Cert.',*/}
      {/*        children: '0 out of 0 failed',*/}
      {/*      },*/}
      {/*      {*/}
      {/*        key: 4,*/}
      {/*        label: 'Sent Email',*/}
      {/*        children: '0 out of 0 failed',*/}
      {/*      }*/}
      {/*    ]}*/}
      {/*  />*/}
      {/*</fieldset>*/}
      {/*<br/>*/}
      {/*<Import />*/}
      {/*<Tabs*/}
      {/*  onChange={() => {}}*/}
      {/*  items={tabItems}*/}
      {/*/>*/}
      {/*<br/>*/}
      {/*<ExceptionalCaseModal*/}
      {/*  open={exceptionalCaseOpen}*/}
      {/*  title={'Exceptional Case'}*/}
      {/*  onCloseCallback={() => setExceptionalCaseOpen(false)}*/}
      {/*  // onFinishCallback={() => setRevokeOpen(false)}*/}
      {/*/>*/}
    </div>

  )
}

export default ExamProfile;