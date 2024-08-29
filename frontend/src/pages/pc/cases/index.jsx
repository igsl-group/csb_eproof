import React, {useCallback, useEffect, useMemo, useState} from 'react';
import styles from './style/index.module.less';
import {useNavigate, useParams, Link} from "react-router-dom";
import {
  Col,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Upload,
  Button,
  Space,
  Tag,
  Row,
  Flex,
  message,
  Pagination,
  Modal
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  FilterOutlined,
  DownloadOutlined,
  UploadOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Textarea from "@/components/Textarea";
import Dropdown from "@/components/Dropdown";
import Date from "@/components/Date";
import {useRequest} from "ahooks";
import {
  caseAPI,
  postAPI,
  documentAPI,
  licenceTypeAPI,
} from '@/api/request';
import {
  toQueryString,
  toBase64,
  download
} from "@/utils/util";
import {dataMapper} from "./data-mapper";
import {
  dataMapperRequired,
  dataMapperConvertPayload,
} from "@/utils/data-mapper";
import { sectionOptions} from "@/config/config";

import {
  TYPE
} from "@/config/enum";
import dayjs from "dayjs";
import {useAuth} from "../../../context/auth-provider";
import {
  validators
} from "../../../utils/validators";

export default function Cases(props) {
  const {
    type = 'View'
  } = props;
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [messageApi, messageContextHolder] = message.useMessage();
  const [modal, modalContextHolder] = Modal.useModal();
  const {
    caseId,
  } = useParams();
  const readOnly = ['View'].includes(type);
  const [supplementaryDocumentData, setSupplementaryDocumentData] = useState([]);
  const [openSupplementaryDocumentModal, setOpenSupplementaryDocumentModal] = useState(false);
  const [bdOptions, setBdOptions] = useState([]);
  const [documentData, setDocumentData] = useState([]);
  const [licenceTypeOptions, setLicenceTypeOptions] = useState([]);
  const [postOptions, setPostOptions] = useState([]);
  const [disabledForm, setDisabledForm] = useState(false);
  const [sectionCdOptions, setSectionCdOptions] = useState([]);
  const [selectedSupplementaryDocumentRowKeys, setSelectedSupplementaryDocumentRowKeys] = useState('');
  const sectionCd = Form.useWatch('sectionCd', form);

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [5, 10, 15],
    pageSize: 5,
    page: 1,
    sortBy: 'id',
    orderBy: 'descend',
  }), []);

  const [docsPagination, setDocsPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  const [suppDocsPagination, setSuppDocsPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  const dropdownPagination = useMemo(() => ({
    total: 0,
    page: 1,
    pageSize: 9999999,
    sortBy: 'name',
    orderBy: 'ascend',
  }), []);

  const { runAsync: runPostAPI } = useRequest(postAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'post-list':
        {
          const data  = response.result?.data || [];
          setPostOptions(data.flatMap((row) => ({
            label: row.name,
            value: row.id,
          })));
          break;
        }
        default:
          break;
      }

    },
    onError: (error) => {
      //Message.error('');
    },
    onFinally: (params, result, error) => {
    },
  });

  const { runAsync: runCaseAPI } = useRequest(caseAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'view':
        {
          const data  = response.result?.data || {};
          form.setFieldsValue({
            ...data,
            applicationDate: data.applicationDate && dayjs(data.applicationDate, "YYYY-MM-DD"),
          })
          break;
        }
        case 'supp-doc-list':
        {
          const result  = response.result;
          if (result) {
            setSuppDocsPagination({
              ...suppDocsPagination,
              total: result.totalCount,
            });
            const data = result?.data || [];
            setSupplementaryDocumentData(data);
          }
          break;
        }
        case 'create':
        {
          const id  = response.result?.data?.id || '';
          if (id) {
            messageApi.success('Create successfully.');
            navigate(`/Cases/${id}/View`);
          }
          break;
        }
        case 'update':
        {
          messageApi.success('Update successfully.');
          navigate(`/Cases/${caseId}/View`);
          retrieveCase();
          break;
        }
        case 'supp-doc-create':
        {
          messageApi.success('Upload successfully.');
          runCaseAPI('supp-doc-list', caseId, toQueryString(suppDocsPagination));
          break;
        }
        case 'supp-doc-download':
        {
          messageApi.success('Download successfully.');
          download(response);
          break;
        }
        case 'bd-list':
        {
          const data  = response.result?.data || [];
          setBdOptions(data.flatMap((row) => ({
            label: row.label,
            value: row.value,
          })));
          break;
        }
        case 'section-list':
        {
          const data  = response.result?.data || [];
          setSectionCdOptions(data.flatMap((row) => ({
            label: row.label,
            value: row.value,
          })));
          break;
        }

        default:
          break;
      }

    },
    onError: (error) => {
      messageApi.success('Create successfully.');
    },
    onFinally: (params, result, error) => {
    },
  });

  const { runAsync: runLicenceTypeAPI } = useRequest(licenceTypeAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'licence-type-list':
          const data  = response.result?.data || [];
          setLicenceTypeOptions(data.flatMap((row) => ({
            label: row.name,
            value: row.id,
          })));
          break;
        case 'user':

          break;
        default:
          break;
      }
    },
    onError: (error) => {
      //Message.error('');
    },
    onFinally: (params, result, error) => {
    },
  });

  const { runAsync: runDocument } = useRequest(documentAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'list':
          const result  = response.result;
          if (result) {
            setDocsPagination({
              ...docsPagination,
              total: result.totalCount,
            });
            const data = result?.data || [];
            setDocumentData(data);
          }
          break;
        default:
          break;
      }

    },
    onError: (error) => {
      //Message.error('');
    },
    onFinally: (params, result, error) => {
    },
  });

  // useEffect(() => {
  //   (async () => {
  //     await getBdList();
  //     if (caseId) {
  //       await getCase();
  //       await getSuppDocList(caseId, suppDocsPagination);
  //       await getDocList(caseId, docsPagination);
  //     }
  //   })();
  // }, [caseId]);
  //


  const getCase = useCallback(() => {
    return runCaseAPI('view', caseId);
  }, [caseId]);

  const getBdList = useCallback(() => {
    return runCaseAPI('bd-list', toQueryString(dropdownPagination, { orderBy: 'label' }));
  }, [dropdownPagination]);

  const getSectionList = useCallback(() => {
    return runCaseAPI('section-list', toQueryString(dropdownPagination, { orderBy: 'label' }));
  }, [dropdownPagination]);

  const getLicenceTypeList = useCallback( () => {
    return runLicenceTypeAPI('licence-type-list', toQueryString(dropdownPagination, { sectionCd }));
  }, [dropdownPagination, sectionCd]);

  const getPostList = useCallback( () => {
    return runPostAPI('post-list', toQueryString(dropdownPagination, { sectionCd }));
  }, [dropdownPagination, sectionCd]);

  const getSuppDocList = useCallback((pagination) => {
    setSelectedSupplementaryDocumentRowKeys([]);
    return runCaseAPI('supp-doc-list', caseId, toQueryString(pagination));
  }, [caseId]);

  const getDocList = useCallback((pagination) => {
    return runDocument('list', toQueryString(pagination, { caseId }));
  }, [caseId]);

  useEffect(() => {
    document.getElementById('layout-content')
      .scrollTo(0, 0);
  }, []);

  const retrieveCase = useCallback(async () => {
    await getBdList();
    await getSectionList();
    if (caseId) {
      await getCase();
      await getSuppDocList(suppDocsPagination);
      await getDocList(docsPagination);
    }
  }, [caseId]);

  useEffect(() => {
    (async () => {
      if (sectionCd) {
        await getLicenceTypeList();
        await getPostList();
      }
      if ([TYPE.CREATE, TYPE.EDIT].includes(type)) {
        form.setFieldsValue({
          licenceType: '',
          caseOfficer: '',
        });
      }

    })();
  }, [sectionCd]);

  useEffect(() => {
    retrieveCase();
  }, [caseId])

  const documentColumns = useMemo(() => [
    {
      title: 'Document ID',
      key: 'id',
      width: 120,
      render: (row) => <Link to={`/Documents/${row.id}/View`}>{row.id}</Link>,
      sorter: true,
    },
    {
      title: 'Template',
      key: 'documentTemplateVersion.documentTemplate.name',
      width: 210,
      render: (row) => <span>{row.documentTemplateVersion?.documentTemplate?.name}</span>,
      sorter: true,
    },
    {
      title: 'Created Date',
      dataIndex: 'createdTime',
      width: 140,
      sorter: true,
    },
    {
      title: 'Status',
      key: 'status',
      width: 180,
      render: (row) => <Tag color="blue">{row.statusLabel}</Tag>,
      sorter: true,
    },
  ], []);

  const supplementaryDocumentColumns = useMemo(() => [
    {
      title: 'File Name',
      key: 'file.fileName',
      width: 180,
      render: (row) => <span>{row.file?.fileName}</span>,
      sorter: true,
    },
    {
      title: 'Added By',
      key: 'createdBy.fullNameEng',
      width: 140,
      render: (row) => <span>{row.createdBy?.fullNameEng}</span>,
      sorter: true,
    },
    {
      title: 'Added Time',
      key: 'createdTime',
      width: 140,
      render: (row) => <span>{row.createdTime}</span>,
      sorter: true,
    },
    {
      title: 'Remarks',
      key: 'file.remarks',
      width: 200,
      render: (row) => <span>{row.file?.remarks}</span>,
      sorter: true,
    },
    {
      title: 'Download',
      key: 'download',
      width: 150,
      render: (row) => <Button icon={<DownloadOutlined />} onClick={() => runCaseAPI('supp-doc-download', caseId, row.id)}/>
    },
  ], [caseId]);

  const rowSelection = useCallback({
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedSupplementaryDocumentRowKeys(selectedRowKeys);
    },
  }, []);

  const onFinish = useCallback(async () => {
    setDisabledForm(true);
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      const payload = dataMapperConvertPayload(dataMapper, type, values);
      if (type === TYPE.CREATE) {
        runCaseAPI('create', payload);
      } else if (type === TYPE.EDIT) {
        runCaseAPI('update', payload);
      }
    }
    setDisabledForm(false);
  }, [type]);

  const docsTableOnChange = useCallback((pageInfo, filters, sorter, extra) => {
    const {
      order,
      columnKey
    } = sorter;
    const tempPagination = {
      ...docsPagination,
      orderBy: order || defaultPaginationInfo.orderBy,
      sortBy: order ? columnKey : defaultPaginationInfo.sortBy,
    }
    setDocsPagination(tempPagination);
    getDocList(tempPagination);
  }, [docsPagination]);

  const docsPaginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...docsPagination,
      page,
      pageSize,
    }
    setDocsPagination(tempPagination);
    getDocList(tempPagination);
  }, [docsPagination]);

  const supplementaryDocumentTableOnChange = useCallback((pageInfo, filters, sorter, extra) => {
    const {
      order,
      columnKey
    } = sorter;
    const tempPagination = {
      ...suppDocsPagination,
      orderBy: order || defaultPaginationInfo.orderBy,
      sortBy: order ? columnKey : defaultPaginationInfo.sortBy,
    }
    setSuppDocsPagination(tempPagination);
    getSuppDocList(tempPagination);
  }, [suppDocsPagination]);

  const supplementaryDocumentPaginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...suppDocsPagination,
      page,
      pageSize,
    }
    setSuppDocsPagination(tempPagination);
    getSuppDocList(tempPagination);
  }, [suppDocsPagination]);

  const onRmoveSupplementaryDocumentsClicked = useCallback(() => {
    if (selectedSupplementaryDocumentRowKeys.length > 0) {
      modal.confirm({
        width: 500,
        title: <div style={{ textAlign: 'justify' }}>Are you sure want to remove supplementary documents?</div>,
        icon: <ExclamationCircleOutlined />,
        content: null,
        okText: 'Confirm',
        cancelText: 'Cancel',
        onOk: async () => {
          for (let key of selectedSupplementaryDocumentRowKeys) {
            await runCaseAPI('supp-doc-remove', caseId, toQueryString({}, {
              caseSupplementaryDocumentId: key,
            }))
              .then(() => messageApi.success(`remove supplementary document id ${key} successfully.`))
              .catch(() => messageApi.error(`remove supplementary document id ${key} fail.`))
          }
          getSuppDocList(suppDocsPagination);
        }
      });
    }

  }, [caseId, selectedSupplementaryDocumentRowKeys]);

  return (
    <div className={styles['case']}>
      {messageContextHolder}
      {modalContextHolder}
      <Typography.Title level={3}>Cases [{type}]</Typography.Title>
      <Breadcrumb>
        <Breadcrumb.Item><HomeOutlined /></Breadcrumb.Item>
        <Breadcrumb.Item>Case</Breadcrumb.Item>
        <Breadcrumb.Item>Case [{type}]</Breadcrumb.Item>
      </Breadcrumb>
      <br />
      <br />
      <Card bordered={false}>
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
          onFinish={onFinish}
        >
          {
            type === 'Create' ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType={'submit'}>Save</Button>
                  <Button onClick={() => navigate(`/Cases/All`)}>Cancel</Button>
                </Flex>
                <br />
              </div>
            ) : null
          }
          {
            type === 'Edit' ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType={'submit'}>Save</Button>
                  <Button
                    onClick={() => {
                      navigate(`/Cases/${caseId}/View`);
                      retrieveCase();
                    }}
                  >
                    Cancel
                  </Button>
                </Flex>
                <br />
              </div>
            ) : null
          }
          {
            type === 'View' ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} onClick={() => navigate(`/Cases/${caseId}/Edit`)}>Edit</Button>
                </Flex>
                <br />
              </div>
            ) : null
          }
          <Row justify={'center'}>
            <Col span={24} md={16}>
              <Row gutter={24}>
                <Col span={24} md={12}>
                  <Text name={'id'} label={'Case ID'} disabled={true} hidden={type === 'Create'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'fake'} label={'Fake'} disabled={true} hidden={true}/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={'sectionCd'} label={'Section'} options={sectionCdOptions} disabled={readOnly} required/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={'bd'} label={'Bureau / Department'} options={bdOptions} disabled={readOnly}/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={['licenceType', 'id']} label={'Licence Type'} options={licenceTypeOptions} disabled={readOnly} required/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'bdRef'} label={'Bureau / Department Ref.'} disabled={readOnly} validation={[validators.maxLengthValidator(50)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'fsdRef'} label={'FSD Ref.'} disabled={readOnly} required validation={[validators.maxLengthValidator(50)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'bdCaseOfficerName'} label={'Bureau / Department Case Officer Name'} disabled={readOnly} validation={[validators.maxLengthValidator(100)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'lifipsWorkflowId'} label={'LIFIPS Case No'} disabled={readOnly} required validation={[validators.digitValidator({ maxLength: 7, minLength: 7 })]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'bdCaseOfficerContact'} label={'Bureau / Department Case Officer Contact No'} disabled={readOnly} validation={[validators.digitValidator({ maxLength: 8, minLength: 8 })]}/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={['caseOfficer', 'id']} label={'Case Officer'} options={postOptions} disabled={readOnly} required/>
                </Col>
                <Col span={24} md={12}>
                  <Text
                    name={'bdCaseOfficerEmail'}
                    label={'Bureau / Department Case Officer Email'}
                    disabled={readOnly}
                    validation={[validators.emailValidator(50)]}
                  />
                </Col>
                 <Col span={24} md={12}>
                  <Text name={'applicantNameEng'} label={'Applicant Name (English)'} disabled={readOnly} required validation={[validators.maxLengthValidator(100)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Date name={'applicationDate'} label={'Application Submission / Received Date'} disabled={readOnly}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'applicantNameChi'} label={'Applicant Name (Chinese)'} disabled={readOnly} validation={[validators.maxLengthValidator(100)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'hkidNo'} label={'HKID'} disabled={readOnly} validation={[validators.maxLengthValidator(11)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'brcNo'} label={'BR Cert No.'} disabled={readOnly} validation={[validators.digitValidator({ maxLength: 8, minLength: 8 })]}/>
                </Col>
                <Col span={24}>
                  <Text name={'premisesAddress'} label={'Premises Address'} disabled={readOnly} required validation={[validators.maxLengthValidator(250)]}/>
                </Col>
                <Col span={24}>
                  <Text name={'corrAddress'} label={'Correspondence Address'} disabled={readOnly} required validation={[validators.maxLengthValidator(250)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'contactNameEng'} label={'Contact Person (English)'} disabled={readOnly} required validation={[validators.maxLengthValidator(100)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'positionInCompany'} label={'Position in Company'} disabled={readOnly} validation={[validators.maxLengthValidator(100)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'contactNameChi'} label={'Contact Person (Chinese)'} disabled={readOnly} validation={[validators.maxLengthValidator(100)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'contactPhoneFax'} label={'Fax No.'} disabled={readOnly} validation={[validators.digitValidator({ maxLength: 8, minLength: 8 })]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text
                    name={'authorizedEmail'}
                    label={'Authorized Email Address'}
                    disabled={readOnly}
                    required
                    validation={[validators.emailValidator(50)]}
                  />
                </Col>
                <Col span={24} md={12}>
                  <Text name={'contactPhoneOffice'} label={'Office Tel No.'} disabled={readOnly} validation={[validators.digitValidator({ maxLength: 8, minLength: 8 })]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'authorizedMobile'} label={'Authorized Mobile No.'} disabled={readOnly} required validation={[validators.digitValidator({ maxLength: 8, minLength: 8 })]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text hidden name={'fake'} label={'Fake'} disabled={readOnly}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={['createdBy', 'fullNameEng']} label={'Created By'} disabled={true} hidden={type === 'Create'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={['updatedBy', 'fullNameEng']} label={'Last Updated By'} disabled={true} hidden={type === 'Create'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'createdTime'} label={'Created Time'} disabled={true} hidden={type === 'Create'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'updatedTime'} label={'Last Updated Time'} disabled={true} hidden={type === 'Create'}/>
                </Col>
              </Row>
            </Col>

          </Row>
          <br />
          {
            type === 'Create' ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType={'submit'}>Save</Button>
                  <Button onClick={() => navigate(`/Cases/All`)}>Cancel</Button>
                </Flex>
                <br />
              </div>
            ) : null
          }
          {
            type === 'Edit' ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType={'submit'}>Save</Button>
                  <Button onClick={() => navigate(`/Cases/${caseId}/View`)}>Cancel</Button>
                </Flex>
                <br />
              </div>
            ) : null
          }
        </Form>
      </Card>
      <br />
      {
        type === 'View' ? (
          <div>
            <Card
              title={'Documents'}
              className={'card-body-nopadding'}
              extra={<Button
                type={'primary'}
                onClick={() => {
                  navigate('/Documents/Create', {
                    state: {
                      caseId,
                      licenceTypeId: form.getFieldValue("licenceType")?.id || '',
                    }
                  })
                }}
              >Create Document</Button>}
            >
              <ResizeableTable
                size={'big'}
                onChange={docsTableOnChange}
                columns={documentColumns}
                dataSource={documentData}
                scroll={{
                  x: '100%',
                }}
                pagination={false}
              />
              <br/>
              <Row justify={'end'}>
                <Col>
                  <Pagination
                    total={docsPagination.total}
                    pageSizeOptions={defaultPaginationInfo.sizeOptions}
                    onChange={docsPaginationOnChange}
                    pageSize={defaultPaginationInfo.pageSize}
                    current={docsPagination.page}
                    showTotal={(total) => `Total ${total} items`}
                    showSizeChanger
                    showQuickJumper
                  />
                </Col>
              </Row>
              <br/>
            </Card>
            <br/>
            <Card
              className={'card-body-nopadding'}
              title={'Supplementary Documents'}
              extra={(
                <Space align="center">
                  <Button type={'primary'} onClick={() => setOpenSupplementaryDocumentModal(true)}>Add Document</Button>
                  <Button type={'primary'} onClick={onRmoveSupplementaryDocumentsClicked}>Remove Document</Button>
                </Space>
              )}
            >
              <ResizeableTable
                rowKey={'id'}
                rowSelection={{
                  type: 'checkbox',
                  ...rowSelection,
                }}
                size={'big'}
                columns={supplementaryDocumentColumns}
                dataSource={supplementaryDocumentData}
                onChange={supplementaryDocumentTableOnChange}
                scroll={{x: "100%"}}
                pagination={false}
              />
              <br/>
              <Row justify={'end'}>
                <Col>
                  <Pagination
                    total={suppDocsPagination.total}
                    pageSizeOptions={defaultPaginationInfo.sizeOptions}
                    onChange={supplementaryDocumentPaginationOnChange}
                    pageSize={defaultPaginationInfo.pageSize}
                    current={suppDocsPagination.page}
                    showTotal={(total) => `Total ${total} items`}
                    showSizeChanger
                    showQuickJumper
                  />
                </Col>
              </Row>
              <br/>
            </Card>
            <br/>
          </div>
        ) : null
      }
      <SupplementaryDocumentModal
        open={openSupplementaryDocumentModal}
        documentId={caseId}
        onCancelCallback={() => setOpenSupplementaryDocumentModal(false)}
        onFinishCallback={async () => {
          setOpenSupplementaryDocumentModal(false);
          await getSuppDocList(suppDocsPagination);
        }}
        caseId={caseId}
      />
    </div>
  )
}

const SupplementaryDocumentModal = ({open, onCancelCallback, onFinishCallback, caseId}) => {
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();
  const [fileList, setFileList] = useState([]);

  const normFile = useCallback((e) => {
    if (Array.isArray(e)) {
      return e;
    }
    return e?.fileList;
  }, []);

  const { runAsync: runCaseAPI } = useRequest(caseAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {

        case 'supp-doc-create':
        {
          messageApi.success('Upload successfully.');
          form.resetFields();
          onFinishCallback();
          break;
        }
        default:
          break;
      }

    },
    onError: (error, params) => {
      messageApi.error('Upload fail.');
    },
    onFinally: (params, result, error) => {

    },
  });

  const onFinish = useCallback(async () => {
    const values = await form
      .validateFields()
      .then((values) => ({
        ...values,
        file: values.file[0].originFileObj,
      }))
      .catch(() => false);

    if (values) {
      runCaseAPI('supp-doc-create', caseId, values);
    }
  }, []);

  const onCancel = useCallback(() => {
    form.resetFields();
    onCancelCallback();
  }, []);

  return (
    <Modal
      title={'Add Supplementary Document'}
      open={open}
      onOk={onFinish}
      onCancel={onCancel}
      okText={'Confirm'}
      closable={false}
      maskClosable={false}
    >
      {contextHolder}
      <Form
        layout="vertical"
        autoComplete="off"
        form={form}
        name="form"
      >
        <Form.Item
          name="file"
          label="File"
          valuePropName="fileList"
          getValueFromEvent={normFile}
          rules={[
            {
              required: true,
              message: 'Required',
            },
          ]}
        >
          <Upload
            accept={".doc,.docx,.pdf"}
            maxCount={1}
            multiple={false}
            onRemove={(file) => {
              const index = fileList.indexOf(file);
              const newFileList = fileList.slice();
              newFileList.splice(index, 1);
              setFileList(newFileList);
            }}
            beforeUpload={(file) => {
              setFileList([...fileList, file]);
              return false;
            }}
          >
            <Button icon={<UploadOutlined />}>Select File</Button>
          </Upload>
        </Form.Item>
        <Textarea name={'remarks'} label={'Remarks'} validation={[validators.maxLengthValidator(250)]}/>
      </Form>
    </Modal>
  );
};