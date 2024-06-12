import React from 'react';
import './App.less';
import { BrowserRouter as Router } from "react-router-dom";
import { RenderRouter } from './routes';
import { ConfigProvider } from "antd";
import enUS from "antd/locale/en_US";
import {MainContext} from './context/mainContext';
import theme from "./utils/theme.json";
import AuthProvider from "./context/auth-provider";
import MessageProvider from "./context/message-provider";
import ModalProvider from "./context/modal-provider";

function App() {




  return (
    <div className="App">
      <MainContext.Provider value={{}}>
        <ConfigProvider locale={enUS} size={"small"} theme={theme}>
          <Router>
            <MessageProvider>
              <ModalProvider>
                <AuthProvider>
                  <RenderRouter />
                </AuthProvider>
              </ModalProvider>
            </MessageProvider>
          </Router>
        </ConfigProvider>
      </MainContext.Provider>
    </div>
  );
}

export default App;
