import React, {
  useEffect,
  useContext,
} from 'react';
import {
  useNavigate,
  useLocation,
} from "react-router-dom";
import {useModal} from "../context/modal-provider";
import {useAuth} from "../context/auth-provider";
const whiteList = [
  '/ExamProfile',
  '/WaitingRevoke',
  '/HistoricalResult',
  '/HistoricalResult',
  '/StatisticalReports',
  '/CertificateManagement',
  '/Workflow',
  '/WorkflowRenew',
  '/System',
  '/Restricted',
  '/Template',
  '/UserManagement',
];

// const ALIVE_TIMER = 1000 * 60 * 5;
const ALIVE_TIMER = 1000 * 60 * 5;

// 2 hours
const IDLE_TIMER = 1000 * 60 * 60 * 2;
let lastMouseMoveTime = Date.now();

export default function useSessionAlive() {

  const modalApi = useModal();
  const auth = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  let idleTimeoutId = null;
  let aliveTimerId = null;
  const restartIdleTimer = () => {
    if (idleTimeoutId) {
      clearTimeout(idleTimeoutId);
    }

    idleTimeoutId = setTimeout(async () => {
      if (aliveTimerId) {
        clearInterval(aliveTimerId)
      }
      console.log('@@@Browser IDLE');
      modalApi.warning({
        title: "Logout",
        content: "Yon are forced logout since you have been idle over 2 hours.",
        okText: "Cancel",
        alignCenter: false
      });
      auth.logOut();


    }, IDLE_TIMER);
  };

  const onMouseMove = () => {
    lastMouseMoveTime = Date.now();
    restartIdleTimer();
  };

  const startSessionAliveTimer = () => {
    aliveTimerId = setInterval(() => {
      if (lastMouseMoveTime + ALIVE_TIMER > Date.now()) {
        console.log('@@@Fetch Session API');
      }
    }, ALIVE_TIMER);
  }

  useEffect(() => {
    // whiteList certain pages
    let preventReset = true;
    for (const path of whiteList) {
      if (pathname.toLowerCase().indexOf(path.toLowerCase()) > -1) {
        preventReset = false;
      }
    }
    if (preventReset) {
      return;
    }

    // initiate timeout
    restartIdleTimer();
    startSessionAliveTimer();

    window.addEventListener('mousemove', onMouseMove);

    return () => {
      if (idleTimeoutId) {
        clearTimeout(idleTimeoutId);
        window.removeEventListener('mousemove', onMouseMove);
      }
      if (aliveTimerId) {
        clearInterval(aliveTimerId)
      }
    };
  }, [pathname]);
}
