import { useContext, createContext, useState, useEffect, useCallback } from "react";
import { Button, Modal } from 'antd';


const ModalContext = createContext();

const ModalProvider = ({ children }) => {
  const [modal, contextHolder] = Modal.useModal();

  return (
    <ModalContext.Provider value={{
      confirm: modal.confirm,
      success: modal.success,
      warning: modal.warning,
      info: modal.info,
      error: modal.error,
    }}>
      {contextHolder}
      {children}
    </ModalContext.Provider>
  );

};

export default ModalProvider;

export const useModal = () => {
  return useContext(ModalContext);
};