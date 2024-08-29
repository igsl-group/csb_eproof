import React, { useCallback, useEffect, useState, useMemo } from 'react';
import styles from './style/index.module.less';
import {useNavigate, useLocation, useParams, Link} from "react-router-dom";
import {
  message,
  Modal,
  Col,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Flex,
  Space,
  Tag,
  Row,
  Divider,
  Select,
  Pagination
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  DownloadOutlined,
  ExclamationCircleOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import Text from "@/components/Text";
import Dropdown from "@/components/Dropdown";
import Textarea from "@/components/Textarea";
import Button from "@/components/Button";
import Editor from "@/components/Editor";
import { useRequest } from "ahooks";
import {
  caseAPI,
  documentAPI,
  licenceTypeAPI,
  templateAPI,
} from "@/api/request";
import { dataMapper } from "./data-mapper";
import {
  dataMapperRequired,
  dataMapperConvertPayload,
} from "@/utils/data-mapper";
import {
  TYPE
} from "@/config/enum";
import {
  toQueryString,
  download,
  getFilename,
} from "@/utils/util";
import Date from "../../../components/Date";
import {DOCUMENT_ACTION} from "../../../config/enum";
import dayjs from "dayjs";
import {useAuth} from "../../../context/auth-provider";
import _ from "lodash";
import {previewPdf} from "../../../utils/util";
import {validators} from "../../../utils/validators";
import {
  FIELD_INVALID_EMAIL_MESSAGE,
  FIELD_MAX_LENGTH_MESSAGE,
} from "@/utils/util";
import format from "@/utils/string-template";
export default function Document(props) {
  const {
    type = TYPE.VIEW
  } = props;
  const navigate = useNavigate();
  const {
    state
  } = useLocation();
  const [modal, modalContextHolder] = Modal.useModal();

  const auth = useAuth();
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();
  const [openSubmitRemarkModal, setOpenSubmitRemarkModal] = useState(false);
  const [addAttachmentModal, setAddAttachmentModal] = useState(false);
  const [openConfirmModal, setOpenConfirmModal] = useState(false);
  const [openRejectModal, setOpenRejectModal] = useState(false);
  const [openWithdrawModal, setOpenWithdrawModal] = useState(false);
  const [openDespatchModal, setOpenDespatchModal] = useState(false);
  const [openDoc, setOpenDoc] = useState(false);
  const [docTitle, setDocTitle] = useState("");
  const [docReadOnly, setDocReadOnly] = useState(true);
  const [licenceTypeOptions, setLicenceTypeOptions] = useState([]);
  const [preparerOptions, setPreparerOptions] = useState([]);
  const [despatcherOptions, setDespatcherOptions] = useState([]);
  const [approver1Options, setApprover1Options] = useState([]);
  const [approver2Options, setApprover2Options] = useState([]);
  const [templateOptions, setTemplateOptions] = useState([]);
  const [ccOptions, setCcOptions] = useState([]);
  const [toOptions, setToOptions] = useState([]);
  const [actions, setActions] = useState([]);
  const [currentTemplate, setCurrentTemplate] = useState({});
  const [attachmentData, setAttachmentData] = useState([]);
  const [historyData, setHistoryData] = useState([]);
  const documentTemplateVersion = Form.useWatch('documentTemplateVersion', form);
  const [sfdt, setSfdt] = useState(null);
  const [pdf, setPdf] = useState(null);
  const caseId = useMemo(() => state?.caseId || '', [state]);
  const licenceTypeId = useMemo(() => {
    if (state?.licenceTypeId) {
      return state?.licenceTypeId;
    }
    return currentTemplate?.documentTemplate?.id || '';
  }, [state, currentTemplate]);

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [5, 10, 15],
    pageSize: 5,
    page: 1,
    sortBy: 'id',
    orderBy: 'descend',
  }), []);

  const dropdownPagination = useMemo(() => ({
    total: 0,
    page: 1,
    pageSize: 9999999,
    sortBy: 'name',
    orderBy: 'ascend',
  }), []);

  const {
    documentsId,
  } = useParams();

  const [historyPagination, setHistoryPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  const [attachmentPagination, setAttachmentPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  // const [templatePagination, setTemplatePagination] = useState({
  //   total: 0,
  //   page: 1,
  //   pageSize: 10,
  //   sortBy: 'id',
  //   orderBy: 'descend',
  // });

  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'attachment-download':
        {
          messageApi.success('Download successfully.');
          download(response);
          break;
        }
        case 'attachment-remove':
        {
          messageApi.success('Remove successfully.');
          retrieveAttachment();
        }
        case 'attachment-list':
        {
          const result  = response.result;
          if (result) {
            setAttachmentPagination({
              ...attachmentPagination,
              total: result.totalCount,
            });
            const data = result?.data || [];
            setAttachmentData(data);
          }
          break;
        }
        case 'document-history':
        {
          const result  = response.result;
          if (result) {
            setHistoryPagination({
              ...historyPagination,
              total: result.totalCount,
            });
            const data = result?.data || [];
            setHistoryData(data);
          }
          break;
        }
        case 'create':
        {
          const id  = response.result?.data?.id || '';
          if (id) {
            messageApi.success('create successfully.');
            navigate(`/Documents/${id}/View`);
          }
          break;
        }
        case 'update':
        {
          messageApi.success('Update successfully.');
          navigate(`/Documents/${documentsId}/View`);
          getDocument();
          break;
        }
        case 'licence-type-list':
          break;
        case 'view':
        {
          const data  = response.result?.data || {};
          form.setFieldsValue({
            ...data,
            validFrom: data.validFrom && dayjs(data.validFrom, "YYYY-MM-DD"),
            validTo: data.validTo && dayjs(data.validTo, "YYYY-MM-DD"),
            inspectionDate: data.inspectionDate && dayjs(data.inspectionDate, "YYYY-MM-DD"),
            toList: data.toList ? data.toList.split(",") : [],
            ccList: data.ccList ? data.ccList.split(",") : [],
          });

          setupTemplateVersion(response.result?.data?.documentTemplateVersion);
          break;
        }
        case 'action':
        {
          const data  = response.result?.data || [];
          setActions(data);
          break;
        }
        case 'doc-reject':
        {
          getDocument();
          messageApi.success('Status update successfully.');
          break;
        }
        case 'doc-withdraw':
        {
          getDocument();
          messageApi.success('Status update successfully.');
          break;
        }
        case 'doc-despatch':
        {
          getDocument();
          messageApi.success('Status update successfully.');
          break;
        }
        case 'confirm-doc':
        {
          runDocumentAPI('view-pdf-for-sign', documentsId);
          break;
        }
        case 'get-signing-cert':
        {
          // runDocumentAPI('view-pdf-for-sign', documentsId);
          break;
        }
        case 'view-pdf-for-sign':
        {
          runDocumentAPI('sign-pdf', {
            unsignedPdf: response.data,
          })
          break;
        }
        case 'view-pdf':
        {
          // download(response);
          // setPdf(response.data);
          // var file = new Blob([response.data], {type: 'application/pdf'});
          // var fileURL = URL.createObjectURL(file);
          // window.open(fileURL);
          break;
        }
        case 'sign-pdf':
        {
          runDocumentAPI('upload-pdf', documentsId, {
            file: response.data,
          })
          break;
        }
        case 'upload-pdf':
        {
          messageApi.success('Sign successfully.');
          break;
        }
        case 'view-doc':
        {

          break;
        }
        case 'view-sfdt':
        {
          const request = response?.request;
          let fileName = `${documentsId}.docx`;
          if (request.getResponseHeader('Content-Disposition')) {
            fileName = decodeURI(request.getResponseHeader('Content-Disposition').split('filename=')[1].replaceAll('"', ''))
          }
          if (response.data) {
            const text = await response.data.text();
            setOpenDoc(true);
            setSfdt(text);
            setDocTitle(fileName);
          }
        }
        default:
          break;
      }

    },
    onError: (response, params) => {
      messageApi.error(response?.data?.message);
    },
    onFinally: (params, result, error) => {
    },
  });

  const { runAsync : runCaseAPI } = useRequest(caseAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'supp-doc-download':
        {
          messageApi.success('Download successfully.');
          download(response);
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

  const { runAsync : runTemplateAPI } = useRequest(templateAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'template-list':
        {
          const data  = response.result?.data || [];
          setTemplateOptions(data.flatMap((row) => ({
            label: row.name,
            value: row.id,
          })));
          break;
        }
        case 'template-version-current':
        {
          setupTemplateVersion(response.result?.data);
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

  const setupTemplateVersion = (templateVersionInfo) => {

    const {
      version = '',
      preparerList = [],
      despatcherList = [],
      approver1List = [],
      approver2List = [],
      ccList = "",
      toList = "",
    } = templateVersionInfo;

    form.setFieldValue("documentTemplateVersion", {
      ...form.getFieldValue("documentTemplateVersion"),
      version,
    });

    if (Array.isArray(despatcherList)) {
      setDespatcherOptions(_.sortBy(despatcherList.flatMap((row) => ({
        label: row.name,
        value: row.id,
      })), ['label']));
    }

    if (Array.isArray(preparerList)) {
      setPreparerOptions(_.sortBy(preparerList.flatMap((row) => ({
        label: row.name,
        value: row.id,
      })), ['label']));
    }

    if (Array.isArray(approver1List)) {
      setApprover1Options(_.sortBy(approver1List.flatMap((row) => ({
        label: row.name,
        value: row.id,
      })), ['label']));
    }

    if (Array.isArray(approver2List)) {
      setApprover2Options(_.sortBy(approver2List.flatMap((row) => ({
        label: row.name,
        value: row.id,
      })), ['label']));
    }

    if (ccList) {
      setCcOptions(ccList.split(",").flatMap((value) => ({
        label: value,
        value,
      })));
    }


    if (toList) {
      setToOptions(toList.split(",").flatMap((value) => ({
        label: value,
        value,
      })));

    }
    setCurrentTemplate(templateVersionInfo);
  }

  const attachmentTableOnChange = useCallback((pageInfo, filters, sorter, extra) => {
    const {
      order,
      columnKey
    } = sorter;
    const tempPagination = {
      ...attachmentPagination,
      orderBy: order || defaultPaginationInfo.orderBy,
      sortBy: order ? columnKey : defaultPaginationInfo.sortBy,
    }
    setAttachmentPagination(tempPagination);
    getAttachmentList(tempPagination);
  }, [attachmentPagination]);

  const attachmentPaginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...attachmentPagination,
      page,
      pageSize,
    }
    setAttachmentPagination(tempPagination);
    getAttachmentList(tempPagination);
  }, [attachmentPagination]);

  const historyTableOnChange = useCallback((pageInfo, filters, sorter, extra) => {
    const {
      order,
      columnKey
    } = sorter;
    const tempPagination = {
      ...historyPagination,
      orderBy: order || defaultPaginationInfo.orderBy,
      sortBy: order ? columnKey : defaultPaginationInfo.sortBy,
    }
    setHistoryPagination(tempPagination);
    getHistoryList(tempPagination);
  }, [historyPagination]);

  const historyPaginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...historyPagination,
      page,
      pageSize,
    }
    setHistoryPagination(tempPagination);
    getHistoryList(tempPagination);
  }, [historyPagination]);

  useEffect(() => {
    if (type === TYPE.CREATE && documentTemplateVersion?.id) {

      form.setFieldsValue({
        preparer: {
          id: preparerOptions.filter((row) => row.label === auth.post).length > 0 ? auth.post: '',
        },
        toList: toOptions.flatMap((row) => row.value),
        ccList: ccOptions.flatMap((row) => row.value),
      })
    }
  }, [toOptions, ccOptions, preparerOptions]);

  const { run: runLicenceTypeAPI } = useRequest(licenceTypeAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'licence-type-list':
          const result  = response.result;
          if (result) {
            const data = result.data?.flatMap((row) => ({
              label: row.name,
              value: row.id,
            })) || [];
            setLicenceTypeOptions(data);
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

  const retrieveAttachment = useCallback(async () => {
    await getAttachmentList(attachmentPagination);
    await getHistoryList(historyPagination);
  }, [form.getFieldValue("eisCase")?.id]);

  useEffect(() => {
    form.setFieldValue("eisCase", {
      id: caseId
    })
  }, [caseId]);

  useEffect(() => {
    if (licenceTypeId) {
      runTemplateAPI('template-list', toQueryString(dropdownPagination, { licenceTypeId }));
    }
  }, [licenceTypeId]);

  useEffect(() => {
    document.getElementById('layout-content')
      .scrollTo(0, 0);
  }, []);

  window.form = form;
  useEffect(() => {
    (async () => {
      if (documentTemplateVersion?.id) {
        await runTemplateAPI('template-version-current', documentTemplateVersion.id);
      }
      if (licenceTypeId) {
        await runTemplateAPI('template-list', toQueryString(dropdownPagination, { licenceTypeId }));
      }
    })()
  }, [documentTemplateVersion?.id, licenceTypeId]);

  const getSfdt = useCallback(() => {
    runDocumentAPI('view-sfdt', documentsId)
  }, [documentsId]);

  const onSubmitClicked = useCallback(() => {
    setOpenSubmitRemarkModal(true);
  }, [documentsId])

  const onConfirmClicked = useCallback(() => {
    setOpenConfirmModal(true);
  }, [documentsId]);

  const onRejectClicked = useCallback(() => {
    setOpenRejectModal(true);
  }, [documentsId]);

  const onWithdrawClicked = useCallback(() => {
    setOpenWithdrawModal(true);
  }, [documentsId]);

  const onDespatchClicked = useCallback(() => {
    setOpenDespatchModal(true);
  }, [documentsId]);

  const documentColumns = useMemo(() => [
    {
      title: 'File Name',
      key: 'caseSupplementaryDocument.file.fileName',
      width: 180,
      render: (row) => <span>{row.caseSupplementaryDocument?.file?.fileName}</span>,
      sorter: true,
    },
    {
      title: 'Added By',
      key: 'createdBy',
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
      title: 'Action',
      key: 'action',
      width: 150,
      render: (row) => (
        <Space>
          <Button icon={<DownloadOutlined />} onClick={() => runDocumentAPI('attachment-download', documentsId, row.caseSupplementaryDocument?.id)}/>
          <Button
            icon={<DeleteOutlined />}
            onClick={() =>{
              modal.confirm({
                title: 'Are you sure want to remove attachment?',
                icon: <ExclamationCircleOutlined />,
                okText: 'Yes',
                cancelText: 'No',
                onOk: () => {
                  runDocumentAPI('attachment-remove', documentsId, { caseSupplementaryDocumentId: row.caseSupplementaryDocument?.id});
                }
              });
            }}/>
        </Space>
      )
    },
  ], [form.getFieldValue('eisCase')?.id, documentsId]);

  const amendmentHistoryColumns = useMemo(() => [
    {
      title: 'Action Time',
      key: 'createdTime',
      render: (row) => <span>{row.createdTime}</span>,
      sorter: true,
    },
    {
      title: 'Action',
      key: 'action',
      render: (row) => <span>{row.actionLabel}</span>,
      sorter: true,
    },
    {
      title: 'Action By',
      key: 'createdBy.fullNameEng',
      render: (row) => <span>{row.createdBy?.fullNameEng}</span>,
      sorter: true,
    },
    {
      title: 'Remarks',
      key: 'remarks',
      render: (row) => <span>{row.remarks}</span>,
      sorter: true,
    },
  ], []);

  const onFinish = useCallback(async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch((e) => false);

    if (values) {
      const payload = dataMapperConvertPayload(dataMapper, type, values);
      if (type === TYPE.CREATE) {
        runDocumentAPI('create', payload);
      } else if (type === TYPE.EDIT) {
        runDocumentAPI('update', payload);
      }
    }},
  [type]);

  useEffect(() => {
    (async () => {
      if (documentsId) {
        await getDocument();
        await getAttachmentList(attachmentPagination);
        await getHistoryList(historyPagination);
        form.setFieldValue("id", documentsId);
      }
    })();
  }, [documentsId]);
  window.documentsId = documentsId;

  const onDownloadPdfClicked = useCallback(async () => {
    const response = await runDocumentAPI('view-pdf', documentsId)
    download(response);
  }, [documentsId]);

  const onViewPdfClicked = useCallback(async () => {
    const response = await runDocumentAPI('view-pdf', documentsId)
    previewPdf(response);
  }, [documentsId]);

  const onSaveClicked = useCallback(async (blob, id) => {
    const docId = id ||documentsId
    const file = new File([blob], docTitle, {
      type: blob.type,
    })
    await runDocumentAPI('updateDoc', docId, {
      file,
    });
    setOpenDoc(false);
  }, [documentsId]);

  const getDocument = useCallback( async () => {
    if (documentsId) {
      await runDocumentAPI('view', documentsId);
      await runDocumentAPI('action', documentsId);
      // await getAttachmentList(attachmentPagination);
      await getHistoryList(historyPagination);
    }

  }, [documentsId]);

  const getAttachmentList = useCallback(async (pagination) => {
    return runDocumentAPI('attachment-list', documentsId, toQueryString(pagination));
  }, [documentsId]);

  useEffect(() => {
    getDocument();
  }, [auth.post]);

  const getHistoryList = useCallback(async (pagination) => {
    return runDocumentAPI('document-history', documentsId, toQueryString(pagination));
  }, [documentsId]);

  const readOnly = useMemo(() => [TYPE.VIEW].includes(type), [type]);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Documents',
    },
    {
      title: `Document [${type}]`,
    }
  ], [type]);

  return (
    <div className={styles['document']}>
      {contextHolder}
      {modalContextHolder}
      <Typography.Title level={3}>Documents [{type}]</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <br/>
      <Card bordered={false}>
        <Form
          layout="vertical"
          autoComplete="off"
          form={form}
          name="form"
          onFinish={onFinish}
          initialValues={{
            toList: [],
            ccList: [],
            documentTemplateVersion: "",
            preparer: "",
            approver1: "",
            approver2: "",
            despatcher: "",
          }}
        >
          {
            type === TYPE.CREATE ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType="submit">Save</Button>
                  <Button onClick={() => navigate(`/Documents/All`)}>Cancel</Button>
                </Flex>
                <br/>
              </div>
            ) : null
          }
          {
            type === TYPE.EDIT ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType={'submit'}>Save</Button>
                  <Button onClick={() => {
                    navigate(`/Documents/${documentsId}/View`);
                    getDocument();
                  }}>
                    Cancel
                  </Button>
                </Flex>
                <br/>
              </div>
            ) : null
          }
          {
            type === TYPE.VIEW ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} onClick={() => navigate(`/Documents/${documentsId}/Edit`)}
                          hidden={!actions.includes(DOCUMENT_ACTION.UPDATE)}>Edit</Button>
                  <Button type={'primary'} onClick={onDownloadPdfClicked} hidden={!actions.includes(DOCUMENT_ACTION.DOWNLOAD)}>Download</Button>
                  <Button type={'primary'} onClick={onViewPdfClicked} hidden={!actions.includes(DOCUMENT_ACTION.VIEW_FILE)}>View Document</Button>
                  <Button
                    type={'primary'}
                    hidden={!actions.includes(DOCUMENT_ACTION.UPDATE_FILE)}
                    onClick={() => {
                      setDocReadOnly(false);
                      getSfdt();
                    }}>
                    Edit Document
                  </Button>
                </Flex>
                <br/>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} onClick={onSubmitClicked} hidden={!actions.includes(DOCUMENT_ACTION.SUBMIT)}>Submit
                    for Approval</Button>
                  <Button type={'primary'} onClick={onRejectClicked}
                          hidden={!actions.includes(DOCUMENT_ACTION.REJECT)}>Reject</Button>
                  <Button type={'primary'} onClick={onConfirmClicked}
                          hidden={!actions.includes(DOCUMENT_ACTION.CONFIRM)}>Confirm</Button>
                  <Button type={'primary'} onClick={onWithdrawClicked}
                          hidden={!actions.includes(DOCUMENT_ACTION.WITHDRAW)}>Withdraw</Button>
                  <Button type={'primary'} onClick={onDespatchClicked}
                          hidden={!actions.includes(DOCUMENT_ACTION.DESPATCH)}>Despatch</Button>
                  <Button type={'primary'} onClick={() => runDocumentAPI('sign-pdf')}
                          hidden={!actions.includes(DOCUMENT_ACTION.SIGN)}>Sign</Button>
                </Flex>
                <br/>
              </div>
            ) : null
          }
          <Row justify={'center'}>
            <Col span={16}>
              <Row gutter={24}>

                <Col span={24} md={24}>
                  <Text name={['eisCase', 'id']} label={'Case ID'} disabled={true} hidden={true}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'id'} label={'Document ID'} disabled={true} hidden={type === TYPE.CREATE}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'statusLabel'} label={'Status'} disabled={true} hidden={type === TYPE.CREATE}/>
                </Col>
                <Col span={24}>
                  <Dropdown name={['documentTemplateVersion', 'id']} label={'Template'} options={templateOptions}
                            disabled={type !== TYPE.CREATE} required/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={['documentTemplateVersion', 'version']} label={'Template Version'} disabled={true}
                        required/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'docNo'} label={'Document Number'} disabled={true} hidden={type === TYPE.CREATE}/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={['preparer', 'id']} label={'Preparer'} options={preparerOptions} disabled={readOnly}
                            required/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={['approver1', 'id']} label={'Approver 1'} options={approver1Options}
                            disabled={readOnly} hidden={!approver1Options.length} required={approver1Options.length}/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={['despatcher', 'id']} label={'Despatcher'} options={despatcherOptions}
                            disabled={readOnly} required/>
                </Col>
                <Col span={24} md={12}>
                  <Dropdown name={['approver2', 'id']} label={'Approver 2'} options={approver2Options}
                            disabled={readOnly} hidden={!approver2Options.length} required={approver2Options.length}/>
                </Col>
                <Col span={24} md={12}>
                  <Date name={'validFrom'} label={'Valid From'} disabled={readOnly} required/>
                </Col>
                <Col span={24} md={12}>
                  <Date name={'validTo'} label={'Valid To'} disabled={readOnly}/>
                </Col>
                <Col span={24} md={12}>
                  <Date name={'inspectionDate'} mode={'date'} label={'Date of Inspection'} disabled={readOnly} hidden={true}/>
                </Col>
                <Col span={24}>
                  <Dropdown
                    name={'toList'}
                    label={'To List'}
                    disabled={readOnly}
                    mode="tags"
                    options={toOptions}
                    tokenSeparators={[',']}
                    validation={[
                      {
                        validator: (_, val) => {
                          const maxLength = 100;
                          for (const row of val) {
                            if (row) {
                              if (!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(row)) {
                                return Promise.reject(new Error(FIELD_INVALID_EMAIL_MESSAGE));
                              } else if (row.length > maxLength) {
                                return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
                              }
                            }
                          }

                          return Promise.resolve();
                        },
                      }
                    ]}
                  />
                </Col>
                <Col span={24}>
                  <Dropdown
                    name={'ccList'}
                    label={'Cc List'}
                    disabled={readOnly}
                    mode="tags"
                    options={ccOptions}
                    tokenSeparators={[',']}
                    validation={[
                      {
                        validator: (_, val) => {
                          const maxLength = 50;
                          for (const row of val) {
                            if (row) {
                              if (!/^[\w-\.]+@([\w-]+\.)+[\w-]{2,10}$/.test(row)) {
                                return Promise.reject(new Error(FIELD_INVALID_EMAIL_MESSAGE));
                              } else if (row.length > maxLength) {
                                return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
                              }
                            }
                          }

                          return Promise.resolve();
                        },
                      }
                    ]}
                  />
                </Col>
                <Col span={24}>
                  <Textarea name={'remarks'} label={'Remarks'} disabled={readOnly} validation={[validators.maxLengthValidator(250)]}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={['createdBy', 'fullNameEng']} label={'Created By'} disabled={true} hidden={type === TYPE.CREATE}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={['updatedBy', 'fullNameEng']} label={'Last Updated By'} disabled={true} hidden={type === TYPE.CREATE}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'createdTime'} label={'Created Time'} disabled={true} hidden={type === TYPE.CREATE}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'updatedTime'} label={'Last Updated Time'} disabled={true}
                        hidden={type === TYPE.CREATE}/>
                </Col>
              </Row>
            </Col>
          </Row>
          <br/>
          {
            type === TYPE.CREATE ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType="submit">Save</Button>
                  <Button onClick={() => navigate(`/Documents/All`)}>Cancel</Button>
                </Flex>
                <br/>
              </div>
            ) : null
          }
          {
            type === TYPE.EDIT ? (
              <div>
                <Flex wrap="wrap" justify={'center'} gap="small">
                  <Button type={'primary'} htmlType={'submit'}>Save</Button>
                  <Button onClick={() => navigate(`/Documents/${documentsId}/View`)}>Cancel</Button>
                </Flex>
                <br/>
              </div>
            ) : null
          }
        </Form>
      </Card>
      <br/>
      {
        type === TYPE.VIEW ? (
          <div>
            <Card
              title={'Attachment'}
              className={'card-body-nopadding'}
              extra={(
                <Space align="center">
                  <Button type={'primary'} onClick={() => setAddAttachmentModal(true)}>Add Attachment</Button>
                </Space>
              )}
            >
              <ResizeableTable
                size={'big'}
                onChange={attachmentTableOnChange}
                columns={documentColumns}
                dataSource={attachmentData}
                scroll={{
                  x: '100%',
                }}
                pagination={false}
              />
              <br/>
              <Row justify={'end'}>
                <Col>
                  <Pagination
                    total={attachmentPagination.total}
                    pageSizeOptions={defaultPaginationInfo.sizeOptions}
                    onChange={attachmentPaginationOnChange}
                    pageSize={defaultPaginationInfo.pageSize}
                    current={attachmentPagination.page}
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
              title={'Amendment History'}
            >
              <ResizeableTable
                size={'big'}
                onChange={historyTableOnChange}
                columns={amendmentHistoryColumns}
                dataSource={historyData}
                scroll={{
                  x: '100%',
                }}
                pagination={false}
              />
              <br/>
              <Row justify={'end'}>
                <Col>
                  <Pagination
                    total={historyPagination.total}
                    pageSizeOptions={defaultPaginationInfo.sizeOptions}
                    onChange={historyPaginationOnChange}
                    pageSize={defaultPaginationInfo.pageSize}
                    current={historyPagination.page}
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
        ) : <div/>
      }
      <Editor
        open={openDoc}
        title={docTitle}
        documentId={documentsId}
        docReadOnly={docReadOnly}
        onCancel={() => setOpenDoc(false)}
        onOk={(blob, id) => onSaveClicked(blob, id)}
        sfdt={sfdt}
      />
      <SubmitRemarkModal
        open={openSubmitRemarkModal}
        documentId={documentsId}
        approvalList={{
          approver1Options,
          approver2Options,
        }}
        approval={{
          approver1: form.getFieldValue('approver1'),
          approver2: form.getFieldValue('approver2'),
        }}
        onCancelCallback={() => setOpenSubmitRemarkModal(false)}
        onFinishCallback={async () => {
          setOpenSubmitRemarkModal(false);
          await getDocument();
        }}
      />
      <RejectModal
        open={openRejectModal}
        documentId={documentsId}
        onCancelCallback={() => setOpenRejectModal(false)}
        onFinishCallback={async () => {
          setOpenRejectModal(false);
          await getDocument();
        }}
      />
      <WidthdrawModal
        open={openWithdrawModal}
        documentId={documentsId}
        onCancelCallback={() => setOpenWithdrawModal(false)}
        onFinishCallback={async () => {
          setOpenWithdrawModal(false);
          await getDocument();
        }}
      />
      <DespatchModal
        open={openDespatchModal}
        documentId={documentsId}
        onCancelCallback={() => setOpenDespatchModal(false)}
        onFinishCallback={async () => {
          setOpenDespatchModal(false);
          await getDocument();
        }}
      />
      <ConfirmSignCertModal
        open={openConfirmModal}
        documentId={documentsId}
        onCancelCallback={() => setOpenConfirmModal(false)}
        onFinishCallback={async () => {
          setOpenConfirmModal(false);
          await getDocument();
        }}
      />
      <AddAttachmentModal
        open={addAttachmentModal}
        caseId={form.getFieldValue('eisCase')?.id}
        documentId={documentsId}
        onCancelCallback={() => setAddAttachmentModal(false)}
        onFinishCallback={async () => {
          setAddAttachmentModal(false);
          retrieveAttachment();
        }}
      />
    </div>
  )
}


const AddAttachmentModal = ({open, onCancelCallback, onFinishCallback, caseId, documentId}) => {
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();
  const [suppDocOptions, setSuppDocOptions] = useState([]);
  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'attachment-create':
        {
          messageApi.success('Create successfully.');
          form.resetFields();
          onFinishCallback();
          break;
        }
        default:
          break;
      }
    },
    onError: (response, params) => {
      messageApi.error(response?.data?.message);
    },
    onFinally: (params, result, error) => {

    },
  });

  const { runAsync: runCaseAPI } = useRequest(caseAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'supp-doc-list':
        {
          const result  = response.result;
          if (result) {
            const data = result?.data || [];
            setSuppDocOptions(data.flatMap((row) => ({
              value: row.id,
              label: row.file?.fileName,
            })));
          }
          break;
        }
        default:
          break;
      }
    },
    onError: (response, params) => {
      messageApi.error(response?.data?.message);
    },
    onFinally: (params, result, error) => {

    },
  });

  const dropdownPagination = useMemo(() => ({
    total: 0,
    page: 1,
    pageSize: 9999999,
    sortBy: 'file.fileName',
    orderBy: 'ascend',
  }), []);

  const getSuppDocList = useCallback((pagination) => {
    if (caseId) {
      return runCaseAPI('supp-doc-list', caseId, toQueryString(pagination));
    }
  }, [caseId]);

  useEffect(() => {
    if (open) {
      getSuppDocList(dropdownPagination);
    }
  }, [open]);

  const onFinish = async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      await runDocumentAPI('attachment-create', documentId, values)
    }
  };

  const onCancel = () => {
    form.resetFields();
    onCancelCallback();
  };

  return (
    <Modal
      title={'Add Attachment'}
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
        <Dropdown name={'caseSupplementaryDocumentId'} label={'Supplementary Document'} options={suppDocOptions} required/>
      </Form>
    </Modal>
  );
};

const RejectModal = ({open, onCancelCallback, onFinishCallback, documentId}) => {
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();

  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'doc-reject':
        {
          messageApi.success('Status update successfully.');
          form.resetFields();
          onFinishCallback();
          break;
        }
        default:
          break;
      }
    },
    onError: (response, params) => {
      messageApi.error(response?.data?.message);
    },
    onFinally: (params, result, error) => {

    },
  });

  const onFinish = async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      runDocumentAPI('doc-reject', documentId, values)

    }auth
  };

  const onCancel = () => {
    form.resetFields();
    onCancelCallback();
  };

  return (
    <Modal
      title={'Reject'}
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
        <Textarea name={'remarks'} label={'Remarks'}/>
      </Form>
    </Modal>
  );
};

const DespatchModal = ({open, onCancelCallback, onFinishCallback, documentId}) => {
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();

  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'doc-despatch':
        {
          messageApi.success('Status update successfully.');
          form.resetFields();
          onFinishCallback();
          break;
        }
        default:
          break;
      }
    },
    onError: (response, params) => {
      messageApi.error(response?.data?.message);
    },
    onFinally: (params, result, error) => {

    },
  });

  const onFinish = async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      runDocumentAPI('doc-despatch', documentId, values)
    }
  };

  const onCancel = () => {
    form.resetFields();
    onCancelCallback();
  };

  return (
    <Modal
      title={'Despatch'}
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
        <Textarea name={'remarks'} label={'Remarks'}/>
      </Form>
    </Modal>
  );
};

const WidthdrawModal = ({open, onCancelCallback, onFinishCallback, documentId}) => {
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();

  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'doc-withdraw':
        {
          messageApi.success('Status update successfully.');
          form.resetFields();
          onFinishCallback();
          break;
        }
        default:
          break;
      }
    },
    onError: (response, params) => {
      messageApi.error(response?.data?.message);
    },
    onFinally: (params, result, error) => {

    },
  });

  const onFinish = async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      await runDocumentAPI('doc-withdraw', documentId, values)
    }
  };

  const onCancel = () => {
    form.resetFields();
    onCancelCallback();
  };

  return (
    <Modal
      title={'Widthdraw'}
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
        <Textarea name={'remarks'} label={'Remarks'}/>
      </Form>
    </Modal>
  );
};


const ConfirmSignCertModal = ({open, onCancelCallback, onFinishCallback, documentId}) => {
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();

  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'confirm-doc':
        {
          messageApi.success('Sign successfully.');
          form.resetFields();
          onFinishCallback();
          break;
        }
        default:
          break;
      }
    },
    onError: (response, params) => {
      switch (params[0]) {
        case 'get-signing-cert':
        {
          messageApi.error('Cannot detect eIS Local Service, please ensure it is running or contact system administrator for assistance.');
          break;
        }
        default:
          messageApi.error(response?.data?.message);
          break;
      }
    },
    onFinally: (params, result, error) => {

    },
  });

  const onFinish = async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      const cert = await runDocumentAPI('get-signing-cert');

      const pdfBlob = await runDocumentAPI('get-pdf-for-eproof-signing', documentId, {
        publicKeyCert: cert,
      }).then((response) => response.data);

      const signedPdfBlob = await runDocumentAPI('sign-pdf', {
        unsignedPdf:  new File([pdfBlob], '', {
          type: pdfBlob.type,
        }),
        cert,
      }).then((response) => response.data);

      await runDocumentAPI('confirm-doc', documentId, {
        ...values,
        file: new File([signedPdfBlob], '', {
          type: signedPdfBlob.type,
        })
      });

      // await runDocumentAPI('confirm-doc', documentId, {
      //   ...values,
      //   publicKeyCert: cert,
      // });
      // const {
      //   filename,
      //   pdfBlob
      // } = await runDocumentAPI('view-pdf-for-sign', documentId)
      //   .then((response) => ({
      //     filename: getFilename(response),
      //     pdfBlob: response.data
      //   }));
      //
      // const signedPdfBlob = await runDocumentAPI('sign-pdf', {
      //   unsignedPdf: pdfBlob,
      //   cert,
      // })
      //   .then((response) => response.data);
      // await runDocumentAPI('upload-pdf', documentId, {
      //   file: new File([signedPdfBlob], filename, {
      //     type: signedPdfBlob.type,
      //   })
      // });

    }
  };

  const onCancel = () => {
    form.resetFields();
    onCancelCallback();
  };

  return (
    <Modal
      title={'Sign Certificate'}
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
        <Textarea name={'remarks'} label={'Remarks'}/>
      </Form>
    </Modal>
  );
};

const SubmitRemarkModal = ({ open, onCancelCallback, onFinishCallback, documentId, approvalList, currentPost, approval }) => {

  const auth = useAuth();
  const [messageApi, contextHolder] = message.useMessage();
  const [form] = Form.useForm();
  const [approvalOptions, setApprovalOptions] = useState([]);
  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'doc-submit':
        {
          form.resetFields();
          messageApi.success('Status update successfully.');
          onFinishCallback();
          break;
        }
        default:
          break;
      }
    },
    onError: (error, params) => {

    },
    onFinally: (params, result, error) => {
    },
  });

  useEffect(() => {
    if (open) {
      const {
        approver1Options = [],
        approver2Options = [],
      } = approvalList;
      const {
        approver1 = {},
        approver2 = {}
      } = approval;
      setApprovalOptions(approver1Options.filter((row) => row.label === auth.post).length > 0 ? approver2Options : approver1Options);
      form.setFieldValue('submitTo', approver1Options.filter((row) => row.label === auth.post).length > 0 ? approver2.id : approver1.id)
    }
  }, [approvalList, open]);

  const onFinish = useCallback(async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch(() => false);

    if (values) {
      runDocumentAPI('doc-submit', documentId, toQueryString({}, values));
    }
  }, [documentId]);

  const onCancel = useCallback(() => {
    form.resetFields();
    onCancelCallback();
  }, []);

  return (
    <Modal
      title={'Submit for Approval'}
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
        <Dropdown name={'submitTo'} label={'Submit To'} options={approvalOptions} required/>
        <Textarea name={'remarks'} label={'Remarks'}/>
      </Form>
    </Modal>
  );
};