import React, { useMemo, useEffect, useState, useCallback, useRef } from 'react';
import styles from './style/index.module.less';
import { UPDATE_SUCCESS_MESSAGE, PASSWORD_DIFFERENT_MESSAGE } from "@/utils/util";
import { listAPI, loginAPI, documentAPI, licenceTypeAPI, templateAPI } from '@/api/request';
import { useRequest } from 'ahooks';
import PassPasswordRules from "@/hook/passPasswordRules";
import { setUser, getUser } from '@/api/auth';
import {
  TYPE
} from "@/config/enum";
import {
  FIELD_INPUT_EMPTY_MESSAGE,
  FIELD_SELECT_EMPTY_MESSAGE,
  PASSWORD_NOT_MATCH_MESSAGE,
  PASSWORD_NOT_STRONG_MESSAGE
} from "@/utils/util";
import {
  dataMapperRequired,
  dataMapperConvertPayload,
} from "@/utils/data-mapper";
import {
  validators
} from "@/utils/validators";
import {useNavigate, Link} from "react-router-dom";
import {MainContext} from "../../../context/mainContext";
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Input,
  Button,
  Space,
  Tag,
  Popconfirm,
  Row,
  Col,
  Pagination,
  Table
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import { sectionOptions} from "@/config/config";
import {
  HomeOutlined,
  SearchOutlined,
  FilterOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import {
  toQueryString
} from "@/utils/util";
import {dataMapper} from "./data-mapper";

export default function DocumentList(props) {

  const {
    type = 'Todo'
  } = props;

  const {
    setUserInfo
  } = React.useContext(MainContext);

  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [documentData, setDocumentData] = useState([]);
  const keywordRef = useRef(null);
  const [keyword, setKeyword] = useState('');
  const [documentStatusOptions, setDocumentStatusOptions] = useState([]);
  const [password, setPassword] = useState('');
  const passPasswordRules = PassPasswordRules(password);
  const [templateOptions, setTemplateOptions] = useState([]);
  const [open, setOpen] = useState(false);
  const [, setConfirmLoading] = useState(false);
  const [filterCondition, setFilterCondition] = useState(null);
  const [licenceTypeOptions, setLicenceTypeOptions] = useState([]);
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

  const dropdownPagination = useMemo(() => ({
    total: 0,
    page: 1,
    pageSize: 9999999,
    sortBy: 'name',
    orderBy: 'ascend',
  }), []);

  const formData = getUser();
  const title = type === 'Todo' ? 'To-do List (Documents)' : 'Documents';
  const tableTitle = type === 'Todo' ? 'Latest Documents' : 'Documents';
  useEffect(() => {
    let roles = [];
    // formData.userHasRoles.map(item => {
    //   roles.push(item.role.name);
    // })

    // let groups = [];
    // formData.userHasMeetingGroups.map(item => {
    //   groups.push(item.meetingGroup.name);
    // })
    // setData([
    //   {
    //     label: 'Full Name',
    //     value: formData.name,
    //   },
    //   {
    //     label: 'Email',
    //     value: formData.email
    //   },
    //   {
    //     label: 'Phone',
    //     value: formData.phoneNumber
    //   },
    //   {
    //     label: 'Office',
    //     value: formData.department?.departmentName
    //   },
    //   {
    //     label: 'Post Title',
    //     value: formData.post
    //   },
    //   {
    //     label: 'Role',
    //     value: roles.toString().replace(',', ' ')
    //   },
    //   {
    //     label: 'Last Login Date',
    //     value: formData.lastLoginDate,
    //   },
    //   {
    //     label: 'Groups',
    //     // value: groups.toString().replace(',', ' ')
    //     value: (
    //       <div>{ groups.sort().map((group, index) => <div key={index} className={styles['profile-tag']}>{ group }</div>)}</div>
    //     )
    //   },
    // ])
    // form.setFieldsValue(formData)
  }, [sessionStorage.getItem('PMS-User')])

  const { run: runLoginAPI } = useRequest(loginAPI, {
    manual: true,
    onSuccess: (result, params) => {
      switch (params[0]) {
        case 'user':
          setUser(JSON.stringify(result));
          setUserInfo(result)
          // sessionStorage.setItem('permissions', JSON.stringify(result?.permissions));
          // sessionStorage.setItem('PMS-Group', JSON.stringify(result?.userHasMeetingGroups));
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
  const { runAsync: runDocumentAPI } = useRequest(documentAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'list':
          const result  = response.result;
          if (result) {
            setPagination({
              ...pagination,
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

  const { runAsync: runListAPI } = useRequest(listAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'status-list':
          const result  = response.result;
          if (result) {
            const data = result?.data || [];
            setDocumentStatusOptions(data);
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

  useEffect(() => {

  }, []);

  useEffect(() => {
    getDocumentList(pagination);
  }, []);

  useEffect(() => {
    document.getElementById('layout-content')
      .scrollTo(0, 0);
  }, []);

  const getDocumentList = useCallback((pagination = {}, filter = {}) => {
    runDocumentAPI('list', toQueryString(pagination, filter));
  }, []);

  const columns = useMemo(() => [
    {
      title: 'Document ID',
      dataIndex: 'id',
      key: 'id',
      width: 140,
      render: (text) => <Link to={`/Documents/${text}/View`}>{text}</Link>,
      sorter: true,
    },
    {
      title: 'Case ID',
      key: 'eisCase.id',
      width: 100,
      render: (row) => <span>{row.eisCase?.id}</span>,
      sorter: true,
    },
    {
      title: 'FSD Ref.',
      key: 'eisCase.fsdRef',
      width: 150,
      render: (row) => <span>{row.eisCase?.fsdRef}</span>,
      sorter: true,
    },
    {
      title: 'BD Ref.',
      key: 'eisCase.bdRef',
      width: 150,
      render: (row) => <span>{row.eisCase?.bdRef}</span>,
      sorter: true,
    },
    {
      title: 'Section',
      key: 'eisCase.sectionCd',
      width: 100,
      render: (row) => <span>{row.eisCase?.sectionCd}</span>,
      sorter: true,
    },
    {
      title: 'Licence Type',
      key: 'eisCase.licenceType.name',
      width: 140,
      render: (row) => <span>{row.eisCase?.licenceType?.name}</span>,
      sorter: true,
    },
    {
      title: 'Applicant',
      key: 'eisCase.applicantNameEng',
      width: 240,
      render: (row) => <span>{row.eisCase?.applicantNameEng}</span>,
      sorter: true,
    },
    {
      title: 'Template',
      key: 'documentTemplateVersion.documentTemplate.name',
      width: 240,
      render: (row) => <span>{row.documentTemplateVersion?.documentTemplate?.name}</span>,
      sorter: true,
    },
    {
      title: 'Created Date',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 140,
      sorter: true,
    },
    {
      title: 'Status',
      key: 'status',
      width: 200,
      render: (row) => <Tag color="blue" >{row.statusLabel}</Tag>,
      sorter: true,
    },
  ], []);

  const showPopconfirm = async () => {
    setOpen(true);
    await runLicenceTypeAPI('licence-type-list', toQueryString(dropdownPagination));
    await runListAPI('status-list');
  };

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

  const handleOk = useCallback(
    async () => {
      setKeyword('');
      const values = await form
        .validateFields()
        .then((values) => values)
        .catch(() => false);

      if (values) {
        const payload = dataMapperConvertPayload(dataMapper, TYPE.FILTER, values);
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
          await getDocumentList(resetPage);
        } else {
          await getDocumentList(resetPage, finalPayload);
          setFilterCondition(finalPayload);
        }
        setOpen(false);
      }
    },
    [pagination, filterCondition, keyword, resetPagination]
  );

  const handleCancel = () => {
    setOpen(false);
  };

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
    getDocumentList(tempPagination, filterCondition);
  }, [pagination, filterCondition]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getDocumentList(tempPagination, filterCondition);
  }, [pagination, filterCondition]);

  const onSearchClicked = useCallback(async () => {
    if (keywordRef.current) {
      const value = keywordRef.current.input?.value;
      const resetPage = resetPagination();
      setKeyword(value);
      if (value) {
        await getDocumentList(resetPage, { keyword: value });
      } else {
        await getDocumentList(resetPage);
      }
    }
  }, [pagination, filterCondition, keyword, resetPagination]);

  return (
    <div className={styles['todo-case']}>
      <Typography.Title level={3}>{title}</Typography.Title>
      <Breadcrumb>
        <Breadcrumb.Item><HomeOutlined/></Breadcrumb.Item>
        <Breadcrumb.Item>{title}</Breadcrumb.Item>
      </Breadcrumb>
      <br/>
      <Popconfirm
        className={'xxxxx'}
        overlayClassName={'vvvvv'}
        openClassName={'aaaaa'}
        arrow={false}
        placement="bottomLeft"
        icon={null}
        title={(
          <Row justify={'space-between'}>
            <Col>Filter</Col>
            <Col>
              <Button shape="circle" icon={<DeleteOutlined />} onClick={() => form.resetFields()}/>
            </Col>
          </Row>
        )}
        open={open}
        onConfirm={handleOk}
        onCancel={handleCancel}
        cancelText={'Cancel'}
        okText={'Apply Now'}
        cancelButtonProps={{
          size: 'big',
        }}
        okButtonProps={{
          size: 'big',
        }}
        description={(
          <Form
            name="form"
            form={form}
            colon={false}
            labelCol={{span: 10}}
            wrapperCol={{span: 14}}
            autoComplete="off"
            labelAlign={'left'}
            // layout="vertical"
          >
            <br/>
            <Row justify="left">
              <Col span={11}>
                <Text name={'fsdRef'} label={'FSD Ref.'}/>
              </Col>
              <Col span={2}></Col>
              <Col span={11}>
                <Text name={'bdRef'} label={'BD Ref.'}/>
              </Col>
            </Row>
            <Row justify="left">
              <Col span={11}>
                <Dropdown name={'sectionCd'} label={'Section'} options={sectionOptions}/>
              </Col>
              <Col span={2}></Col>
              <Col span={11}>
                <Dropdown name={'licenceTypeId'} label={'Licence Type'} options={licenceTypeOptions}/>
              </Col>
            </Row>
            <Divider/>
            <Row justify="left">
              <Col span={11}>
                <Text name={'template'} label={'Template'}/>
              </Col>
              <Col span={2}/>
              <Col span={11}/>
            </Row>
            <Row justify="left">
              <Col span={11}>
                <Date name={'createdDateFrom'} label={'Created Date'}/>
              </Col>
              <Col span={2} style={{textAlign: 'center'}}>-</Col>
              <Col span={11}>
                <Date style={{width: '214px'}} name={'createdDateFrom'}/>
              </Col>
            </Row>
            <Row justify="left">
              <Col span={11}>
                <Dropdown name={'status'} label={'Status'} options={documentStatusOptions}/>
              </Col>
              <Col span={2}/>
              <Col span={11}/>
            </Row>
          </Form>
        )}
      >
        <Space>
          <Input
            ref={keywordRef}
            placeholder="Search"
            prefix={<SearchOutlined className="site-form-item-icon"/>}
          />
          <Button shape="circle" type={keyword ? 'primary': 'default'} icon={<SearchOutlined/>} onClick={onSearchClicked}/>
          <Button shape="circle" type={filterCondition ? 'primary': 'default'} onClick={showPopconfirm} icon={<FilterOutlined/>}/>

        </Space>
      </Popconfirm>

      <br/>
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
            showTotal={(total) => `Total ${total} item(s)`}
            showQuickJumper
          />
        </Col>
      </Row>
      <br/>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={tableTitle}
      >
        <ResizeableTable
          size={'big'}
          pagination={false}
          onChange={tableOnChange}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={documentData}
        />
        <br/>
        <Row justify={'end'} gutter={[24, 8]}>
          <Col>
            <Pagination
              total={pagination.total}
              pageSizeOptions={defaultPaginationInfo.sizeOptions}
              onChange={paginationOnChange}
              pageSize={defaultPaginationInfo.pageSize}
              current={pagination.page}
              showTotal={(total) => `Total ${total} item(s)`}
              showSizeChanger
              showQuickJumper
            />
          </Col>
          <Col></Col>
        </Row>
        <br/>
      </Card>
      <br/>
    </div>
  )
}
