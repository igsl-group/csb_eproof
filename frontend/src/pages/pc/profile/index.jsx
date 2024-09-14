import React, {useRef, useCallback, useEffect, useState, useMemo} from 'react';
import styles from './style/index.module.less';
import {
  caseAPI,
  licenceTypeAPI,
  postAPI
} from '@/api/request';
import { useRequest } from 'ahooks';
import PassPasswordRules from "@/hook/passPasswordRules";
import {useNavigate, Link} from "react-router-dom";
import {MainContext} from "../../../context/mainContext";
import { sectionOptions} from "@/config/config";
import {
  dataMapperRequired,
  dataMapperConvertPayload,
} from "@/utils/data-mapper";
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Input,
  Space,
  Tag,
  Popconfirm,
  Row,
  Col,
  Pagination,
  Table,
  Modal
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  FilterOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Button from "@/components/Button";
import Dropdown from "@/components/Dropdown";
import {
  toQueryString
} from "@/utils/util";
import {dataMapper} from "./data-mapper";
import {
  TYPE
} from "@/config/enum";
import {useAuth} from "../../../context/auth-provider";

export default function Profile(props) {

  const {
    type = 'Todo'
  } = props;
  const auth = useAuth();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [caseData, setCaseData] = useState([]);
  const [password, setPassword] = useState('');
  const passPasswordRules = PassPasswordRules(password);
  const keywordRef = useRef(null);
  const [keyword, setKeyword] = useState('');
  const [postOptions, setPostOptions] = useState([]);
  const [open, setOpen] = useState(false);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const title = type === 'Todo' ? 'To-do List (Cases)' : 'Cases';
  const tableTitle = type === 'Todo' ? 'Latest Cases' : 'Cases';
  const [filterCondition, setFilterCondition] = useState(null);
  const [licenceTypeOptions, setLicenceTypeOptions] = useState([]);

  const dropdownPagination = useMemo(() => ({
    total: 0,
    page: 1,
    pageSize: 9999999,
    sortBy: 'name',
    orderBy: 'ascend',
  }), []);

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

  window.setCaseData = setCaseData;
  const { runAsync: runCaseAPI } = useRequest(caseAPI, {
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
            setCaseData(data);
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

  useEffect(() => {
    document.getElementById('layout-content')
      .scrollTo(0, 0);
  }, []);

  useEffect(() => {
    getCaseList(pagination);
  }, []);

  const getCaseList = useCallback((pagination = {}, filter = {}) => {
    runCaseAPI('list', toQueryString(pagination, filter));
  }, []);


  const columns = [
    {
      title: 'Case ID',
      key: 'id',
      width: 100,
      render: (row) => <Link to={`/Cases/${row.id}/View`}>{row.id}</Link>,
      sorter: true,
    },
    {
      title: 'FSD Ref.',
      dataIndex: 'fsdRef',
      key: 'fsdRef',
      width: 150,
      sorter: true,
    },
    {
      title: 'BD Ref.',
      dataIndex: 'bdRef',
      key: 'bdRef',
      width: 150,
      sorter: true,
    },
    {
      title: 'LIFIPS Case No',
      dataIndex: 'lifipsWorkflowId',
      key: 'lifipsWorkflowId',
      width: 170,
      sorter: true,
    },
    {
      title: 'Section',
      dataIndex: 'sectionCd',
      key: 'sectionCd',
      width: 100,
      sorter: true,
    },
    {
      title: 'Licence Type',
      key: 'licenceType.name',
      width: 140,
      render: (row) => <span>{row.licenceType?.name}</span>,
      sorter: true,
    },
    {
      title: 'Applicant',
      dataIndex: 'applicantNameEng',
      key: 'applicantNameEng',
      width: 240,
      sorter: true,
    },
    {
      title: 'Case Officer',
      key: 'caseOfficer.name',
      width: 120,
      render: (row) => <span>{row.caseOfficer?.name}</span>,
      sorter: true,
    },
    {
      title: 'Created Date',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 140,
      sorter: true,
    }
  ];

  const showPopconfirm = async () => {
    setOpen(true);
    await runLicenceTypeAPI('licence-type-list', toQueryString(dropdownPagination));
    await runPostAPI('post-list', toQueryString(dropdownPagination));
  };

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
          await getCaseList(resetPage);
        } else {
          setFilterCondition(finalPayload);
          await getCaseList(resetPage, finalPayload);
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
    getCaseList(tempPagination, filterCondition);
  }, [pagination, filterCondition]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
    getCaseList(tempPagination, filterCondition);
  }, [pagination, filterCondition]);

  const onSearchClicked = useCallback(async () => {
    if (keywordRef.current) {
      const value = keywordRef.current.input?.value;
      const resetPage = resetPagination();
      setKeyword(value);
      if (value) {
        await getCaseList(resetPage, { keyword: value });
      } else {
        await getCaseList(resetPage);
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
                <Text name={'bdRef'} label={'BD Ref.'} />
              </Col>
              <Col span={2}></Col>
              <Col span={11}>
                <Text name={'lifipsWorkflowId'} label={'LIFIPS Case No'}/>
              </Col>
            </Row>
            <Row justify="left">
              <Col span={11}>
                <Dropdown name={'sectionCd'} label={'Section'} options={sectionOptions} />
              </Col>
              <Col span={2}></Col>
              <Col span={11}>
                <Dropdown name={'licenceTypeId'} label={'Licence Type'} options={licenceTypeOptions}/>
              </Col>
            </Row>
            <Row justify="left">
              <Col span={11}>
                <Text name={'fsdRef'} label={'FSD Ref'} />
              </Col>
              <Col span={2}></Col>
              <Col span={11}>
                <Dropdown name={'caseOfficerId'} label={'Case Officer'} options={postOptions}/>
              </Col>
            </Row>
            <Divider />
            <Row justify="left">
              <Col span={11}>
                <Date name={'searchStartCreateDate'} label={'Created Date'} />
              </Col>
              <Col span={2} style={{ textAlign: 'center'}}>-</Col>
              <Col span={11} >
                <Date style={{ width: '159px'}} name={'searchEndCreateDate'} />
              </Col>
            </Row>
            <Row justify="left">
              <Col span={11}>
                <Text name={'status'} label={'Status'} />
              </Col>
              <Col span={2} />
              <Col span={11} />
            </Row>
          </Form>
        )}
      >
        <Space>
          <Input
            ref={keywordRef}
            placeholder="Search"
            prefix={<SearchOutlined className="site-form-item-icon" />}
          />
          <Button shape="circle" type={keyword ? 'primary': 'default'} icon={<SearchOutlined />} onClick={onSearchClicked}/>
          <Button shape="circle" type={filterCondition ? 'primary': 'default'} onClick={showPopconfirm} icon={<FilterOutlined />} />

        </Space>
      </Popconfirm>
      {/*<Space>*/}
      {/*  <Input*/}
      {/*    placeholder="Search"*/}
      {/*    prefix={<SearchOutlined className="site-form-item-icon"/>}*/}
      {/*  />*/}
      {/*  <Button shape="circle" icon={<SearchOutlined/>}/>*/}
      {/*  <Button shape="circle" type={filterCondition ? 'primary': 'default'} onClick={showPopconfirm} icon={<FilterOutlined/>}/>*/}

      {/*</Space>*/}
      <br/>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        {
          type !== 'Todo' ? (
            <Col>
              <Button type="primary" hidden={!auth.section} onClick={() => navigate('/Cases/Create')}>Create</Button>
            </Col>
          ) : null
        }
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
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={caseData}
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
