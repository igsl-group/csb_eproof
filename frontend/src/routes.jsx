import {useRoutes, Navigate, useNavigate} from 'react-router-dom';
import ExampleProfileList from '@/pages/exam-profile-list';
import ExampleProfile from '@/pages/exam-profile';
import CertificateManagementActivate from '@/pages/cert-management-activate';
import CertificateManagementDeactivate from '@/pages/cert-management-deactivate';
import Candidate from '@/pages/cert-management-activate/candidate';
import EmailTemplateList from '@/pages/email-template-list';
import CertTemplateList from '@/pages/cert-template-list';
import AppealWorkflow from '@/pages/appeal-workflow';
import ImportWorkflow from '@/pages/cert-issue-workflow/import';
import GenerateWorkflow from '@/pages/cert-issue-workflow/generate';
import SignAndIssueWorkflow from '@/pages/cert-issue-workflow/issue';
import NotifyWorkflow from '@/pages/cert-issue-workflow/notify';
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
        icon: <AreaChartOutlined />,
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
        id: 20,
        pid: 0,
        name: 'Appeal Workflow',
        icon: <AreaChartOutlined />,
        path: '/AppealWorkflow',
        element: <AppealWorkflow />,
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 3,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Certificate Management</span>),
        path: '/CertificateManagement',
        icon: <SettingOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 4,
            pid: 3,
            name: 'Activate',
            path: '/CertificateManagement/Activate',
            element: <CertificateManagementActivate />,
            show: true,
            role: [],
          },
          {
            id: 5,
            pid: 3,
            name: 'Deactivate',
            path: '/CertificateManagement/Deactivate',
            element: <CertificateManagementDeactivate />,
            show: true,
            role: [],
          },
        ],
        ignore: false,
        show: true,
        role: [],
      },
      {
        id: 30,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Cert. Issue Workflow</span>),
        path: '/Workflow',
        icon: <SettingOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 31,
            pid: 30,
            name: 'Import Result (CSV)',
            path: '/Workflow/Import',
            element: <ImportWorkflow />,
            show: true,
            role: [],
          },
          {
            id: 32,
            pid: 30,
            name: 'Generate PDF',
            path: '/Workflow/Generate',
            element: <GenerateWorkflow />,
            show: true,
            role: [],
          },
          {
            id: 33,
            pid: 30,
            name: 'Sign and Issue Cert.',
            path: '/Workflow/SignAndIssueCert',
            element: <SignAndIssueWorkflow />,
            show: true,
            role: [],
          },
          {
            id: 34,
            pid: 30,
            name: 'Notify Candidate',
            path: '/Workflow/Notify',
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
        id: 6,
        pid: 0,
        name: 'Certificate Management',
        icon: <ScheduleOutlined />,
        path: '/CertificateManagement/Activate/Candidate/:hkid',
        element: <Candidate />,
        ignore: true,
        show: true,
        role: [],
      },
      {
        id: 7,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>Template</span>),
        path: '/Template',
        icon: <SettingOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 8,
            pid: 7,
            name: 'Email',
            icon: <ScheduleOutlined />,
            path: '/Template/Email',
            element: <EmailTemplateList />,
            ignore: false,
            show: true,
            role: [],
          },
          {
            id: 9,
            pid: 7,
            name: 'Certificate',
            icon: <ScheduleOutlined />,
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
        id: 10,
        pid: 0,
        name: (<span style={{color: 'rgb(104, 111, 125)'}}>System</span>),
        path: '/System',
        icon: <SettingOutlined style={{ color: 'rgb(104, 111, 125)' }}/>,
        children: [
          {
            id: 11,
            pid: 10,
            name: 'User',
            icon: <ScheduleOutlined />,
            path: '/System/User',
            element: <UserList />,
            ignore: false,
            show: true,
            role: [],
          },
          {
            id: 12,
            pid: 10,
            name: 'Role',
            icon: <ScheduleOutlined />,
            path: '/System/Role',
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
