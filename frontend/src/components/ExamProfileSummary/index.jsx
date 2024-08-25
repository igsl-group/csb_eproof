import React, { useCallback, useEffect, forwardRef, useImperativeHandle } from 'react';
import {useRequest} from "ahooks";
import { examProfileAPI } from '@/api/request';
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {Collapse, Descriptions, Modal, Row, Col, Typography} from 'antd';


const ExamProfileSummary = forwardRef((props, ref) => {
  const modalApi = useModal();
  const messageApi = useMessage();
  const [summary, setSummary] = React.useState({});
  const serialNo = props.serialNo;


  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'examProfileSummaryGet':
        {
          const data = response.data || {};
          setSummary(data);
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


  const updateSummary = useCallback(() => {
    runExamProfileAPI('examProfileSummaryGet', serialNo);
  }, [serialNo]);

  // Expose `fetchSummary` to parent through ref
  useImperativeHandle(ref, () => ({
    updateSummary
  }));

  // // You could fetch the summary immediately when the component is mounted if needed
  // useEffect(() => {
  //   updateSummary();
  // }, []);

  return (
    <fieldset style={{paddingLeft: 30, paddingBottom: 8}}>
    <legend><Typography.Title level={5}>Workflow Summary</Typography.Title></legend>
      <Row style={{color: 'rgba(81, 90, 106, 0.45)'}} gutter={[8, 8]}>
        <Col span={5}>
          <Row gutter={[4, 4]}>
            <Col span={24}>Import Stage</Col>
            <Col span={24}>Total: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.imported || 0}</span></Col>
          </Row>
        </Col>
        <Col span={5}>
          <Row gutter={[4, 4]}>
            <Col span={24}>Generate Stage</Col>
            <Col span={24}>Total: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.generatePdfTotal || 0}</span></Col>
            <Col span={24}>Pending: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.generatePdfPending || 0}</span></Col>
            <Col span={24}>Progress: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.generatePdfInProgress || 0}</span></Col>
            <Col span={24}>Success: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.generatePdfSuccess || 0}</span></Col>
            <Col span={24}>Failed: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.generatePdfFailed || 0}</span></Col>
          </Row>
        </Col>
        <Col span={5}>
          <Row gutter={[4, 4]}>
            <Col span={24}>Sign and Issue Stage</Col>
            <Col span={24}>Total: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.issuedPdfTotal || 0}</span></Col>
            <Col span={24}>Pending: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.issuedPdfPending || 0}</span></Col>
            <Col span={24}>Scheduled: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.issuedPdfInScheduled || 0}</span></Col>
            <Col span={24}>Success: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.issuedPdfSuccess || 0}</span></Col>
            <Col span={24}>Failed: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.issuedPdfFailed || 0}</span></Col>
          </Row>
        </Col>
        <Col span={5}>
          <Row gutter={[4, 4]}>
            <Col span={24}>Notify Stage</Col>
            <Col span={24}>Notify Stage - Pending: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.sendEmailPending || 0}</span></Col>
            <Col span={24}>Notify Stage - Scheduled: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.sendEmailScheduled || 0}</span></Col>
            <Col span={24}>Notify Stage - Success: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.sendEmailSuccess || 0}</span></Col>
            <Col span={24}>Notify Stage - Failed: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.sendEmailFailed || 0}</span></Col>
          </Row>
        </Col>
        <Col span={4}>
          <Row gutter={[4, 4]}>
            <Col span={24}>On-hold Case</Col>
            <Col span={24}>Total: <span style={{ paddingLeft: 8, color: 'rgba(81, 90, 106, 0.88)'}}>{summary.onHoldCaseTotal || 0}</span></Col>
          </Row>
        </Col>
      </Row>
    </fieldset>
  );
});

export default ExamProfileSummary;