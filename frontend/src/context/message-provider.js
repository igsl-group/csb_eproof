import { useContext, createContext, useState, useEffect, useCallback } from "react";
import { Button, message } from 'antd';


const MessageContext = createContext();

const MessageProvider = ({ children }) => {
  const [messageApi, contextHolder] = message.useMessage({
    duration: 5
  });

  return (
    <MessageContext.Provider value={{
      success: messageApi.success,
      warning: messageApi.warning,
      info: messageApi.info,
      error: messageApi.error,
    }}>
      {contextHolder}
      {children}
    </MessageContext.Provider>
  );

};

export default MessageProvider;

export const useMessage = () => {
  return useContext(MessageContext);
};