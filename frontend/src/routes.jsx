import {useRoutes, Navigate, useNavigate} from 'react-router-dom';
import ExampleProfileList from '@/pages/exam-profile-list';
import ExampleProfile from '@/pages/exam-profile';
import CertificateManagementValid from '@/pages/cert-management-activate';
import CertificateManagementInvalid from '@/pages/cert-management-deactivate';
import Candidate from '@/pages/cert-management-activate/candidate';
import EmailTemplateList from '@/pages/email-template-list';
import CertTemplateList from '@/pages/cert-template-list';
import ApprovalWorkflow from '@/pages/approval';
import ImportWorkflow from '@/pages/cert-issue-workflow/import';
import GenerateWorkflow from '@/pages/cert-issue-workflow/generate';
import SignAndIssueWorkflow from '@/pages/cert-issue-workflow/issue';
import NotifyWorkflow from '@/pages/cert-issue-workflow/notify';
import HistoricalResultList from '@/pages/historical-result';
import AuditLog from '@/pages/system/audit-log';

import GenerateWorkflowRenew from '@/pages/cert-issue-workflow-renew/generate';
import SignAndIssueWorkflowRenew from '@/pages/cert-issue-workflow-renew/issue';
import NotifyWorkflowRenew from '@/pages/cert-issue-workflow-renew/notify';
import Login from '@/pages/login';
import StatisticalReport from '@/pages/statistical-report';


import RoleList from '@/pages/role-list';
import UserList from '@/pages/user-list';
import Profile from '@/pages/pc/profile';
// import Cases from '@/pages/pc/cases';
// import DocumentList from '@/pages/pc/document-list';
// import Document from '@/pages/pc/document';
// import LicenceTypes from '@/pages/pc/licence-types';
import Unauthorized from '@/pages/pc/unauthorized';
import PublicLayout from '@/pages/pc/layout';
// import Login from '@/pages/pc/login';
import {
    IconElementEqual,
    IconStepBackwardOutlined,
} from "./assets"
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined,
  TeamOutlined,
  UserOutlined,
  MailOutlined,
  AuditOutlined,
  FundViewOutlined,
  HistoryOutlined,
  FileProtectOutlined,
  FileExclamationOutlined,
  BarsOutlined,
  ImportOutlined,
  InteractionOutlined,
  SignatureOutlined,
  SendOutlined,
  UsergroupAddOutlined,
  GroupOutlined,
  FileSyncOutlined,
  FileSearchOutlined
} from '@ant-design/icons';
import {useAuth} from "./context/auth-provider";
import {useEffect} from "react";

const routeList = [
  {
    name: 'Main',
    element: <PublicLayout />,
    children: [
      {
        id: 1,
        pid: 0,
        name: 'Exam Profile',
        icon: <ProfileOutlined />,
        path: '/ExamProfile',
        element: <ExampleProfileList />,
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 2,
        pid: 0,
        name: 'Exam Profile',
        icon: <AreaChartOutlined />,
        path: '/ExamProfile/:serialNo',
        element: <ExampleProfile />,
        ignore: true,
        show: true,
        role: [],
      },
      {
        id: 3,
        pid: 0,
        name: 'Outstanding Tasks',
        icon: <BarsOutlined />,
        path: '/WaitingRevoke',
        element: <ApprovalWorkflow />,
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 4,
        pid: 0,
        name: 'Historical Result',
        icon: <HistoryOutlined />,
        path: '/HistoricalResult',
        element: <HistoricalResultList />,
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 5,
        pid: 0,
        name: 'Statistical Reports',
        icon: <FundViewOutlined />,
        path: '/StatisticalReports',
        element: <StatisticalReport />,
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 6,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Certificate Management</span>),
        path: '/CertificateManagement',
        icon: <FileSearchOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 61,
            pid: 6,
            name: 'Valid',
            path: '/CertificateManagement/Valid',
            icon: <FileProtectOutlined />,
            element: <CertificateManagementValid />,
            show: true,
            role: [],
          },
          {
            id: 62,
            pid: 6,
            name: 'Invalid',
            path: '/CertificateManagement/Invalid',
            element: <CertificateManagementInvalid />,
            icon: <FileExclamationOutlined />,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 7,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Certificate Issuance</span>),
        path: '/Workflow',
        icon: <FileDoneOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 71,
            pid: 7,
            name: 'Import Result (CSV)',
            path: '/Workflow/Import',
            icon: <ImportOutlined />,
            element: <ImportWorkflow />,
            show: true,
            role: [],
          },
          {
            id: 72,
            pid: 7,
            name: 'Generate PDF',
            path: '/Workflow/Generate',
            icon: <InteractionOutlined />,
            element: <GenerateWorkflow />,
            show: true,
            role: [],
          },
          {
            id: 73,
            pid: 7,
            // name: <div style={{textWrap: 'wrap', lineHeight: '16px'}}>Sign and Issue Certificate</div>,
            name: 'Sign and Issue Certificate',
            path: '/Workflow/SignAndIssueCert',
            icon: <SignatureOutlined />,
            element: <SignAndIssueWorkflow />,
            show: true,
            role: [],
          },
          {
            id: 74,
            pid: 7,
            name: 'Notify Candidate',
            path: '/Workflow/Notify',
            icon: <SendOutlined />,
            element: <NotifyWorkflow />,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },

      {
        id: 8,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Certificate Reissuance</span>),
        path: '/WorkflowRenew',
        icon: <FileSyncOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 81,
            pid: 8,
            name: 'Generate PDF',
            path: '/WorkflowRenew/Generate',
            icon: <InteractionOutlined />,
            element: <GenerateWorkflowRenew />,
            show: true,
            role: [],
          },
          {
            id: 82,
            pid: 8,
            name: 'Sign and Issue Certificate',
            path: '/WorkflowRenew/SignAndIssueCert',
            icon: <SignatureOutlined />,
            element: <SignAndIssueWorkflowRenew />,
            show: true,
            role: [],
          },
          {
            id: 83,
            pid: 8,
            name: 'Notify Candidate',
            path: '/WorkflowRenew/Notify',
            icon: <SendOutlined />,
            element: <NotifyWorkflowRenew />,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 9,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>System</span>),
        path: '/System',
        icon: <SettingOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 91,
            pid: 9,
            name: 'Audit Log',
            path: '/System/AuditLog',
            icon: <ProfileOutlined />,
            element: <AuditLog />,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },
      // {
      //   id: 50,
      //   pid: 0,
      //   name: (<span style={{color: 'rgb(104, 111, 125)'}}>Cert. Issue Workflow (Reissue)</span>),
      //   path: '/WorkflowRenew',
      //   icon: <SettingOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
      //   children: [
      //     {
      //       id: 52,
      //       pid: 50,
      //       name: 'Generate PDF',
      //       path: '/WorkflowRenew/Generate',
      //       element: <GenerateWorkflowRenew />,
      //       show: true,
      //       role: [],
      //     },
      //     {
      //       id: 53,
      //       pid: 50,
      //       name: 'Sign and Issue Cert.',
      //       path: '/WorkflowRenew/SignAndIssueCert',
      //       element: <SignAndIssueWorkflowRenew />,
      //       show: true,
      //       role: [],
      //     },
      //   ],
      //   ignore: false,
      //   show: true,
      //   role: [],
      // },




      {
        id: 10,
        pid: 0,
        name: 'Certificate Management',
        icon: <ScheduleOutlined />,
        path: '/CertificateManagement/Valid/Candidate',
        element: <Candidate />,
        ignore: true,
        show: true,
        role: [],
      },
      {
        id: 11,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Template Management</span>),
        path: '/Template',
        icon: <GroupOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 111,
            pid: 11,
            name: 'Email',
            icon: <MailOutlined />,
            path: '/Template/Email',
            element: <EmailTemplateList />,
            ignore: false,
            show: true,
            role: [],
          },
          {
            id: 112,
            pid: 11,
            name: 'Certificate',
            icon: <FileTextOutlined />,
            path: '/Template/Certificate',
            element: <CertTemplateList />,
            ignore: false,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 12,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>User Management</span>),
        path: '/UserManagement',
        icon: <TeamOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 121,
            pid: 12,
            name: 'User',
            icon: <UserOutlined />,
            path: '/UserManagement/User',
            element: <UserList />,
            ignore: false,
            show: true,
            role: [],
          },
          {
            id: 122,
            pid: 12,
            name: 'Role',
            icon: <UsergroupAddOutlined />,
            path: '/UserManagement/Role',
            element: <RoleList />,
            ignore: false,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },


    ],
  },
  // {
  //   path: '/',
  //   element: <Login />,
  // },
  {
    path: '/Unauthorized',
    element: <Unauthorized />,
  },
  {
    path: '/',
    element: <Login />,
  },
  // {
  //   path: '/*',
  //   element: <Navigate to="/Cases/All" />,
  // },
]

export const localRouters = routeList;
export const RenderRouter = () => {
  const auth = useAuth();

  const navigate = useNavigate();

  // useEffect(() => {
  //   auth.getProfile()
  //     .catch((response) => {
  //       if (process.env.NODE_ENV !== 'development') {
  //         navigate(`/`);
  //       }
  //     })
  // }, []);

  const element = useRoutes(routeList);
  return element;
};
