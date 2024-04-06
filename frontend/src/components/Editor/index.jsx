import React, {useCallback, useEffect, useRef} from 'react';
import { Form, Input, Modal, Col, Row, Space } from "antd";
import '@syncfusion/ej2-base/styles/material.css';
import '@syncfusion/ej2-buttons/styles/material.css';
import '@syncfusion/ej2-inputs/styles/material.css';
import '@syncfusion/ej2-popups/styles/material.css';
import '@syncfusion/ej2-lists/styles/material.css';
import '@syncfusion/ej2-navigations/styles/material.css';
import '@syncfusion/ej2-splitbuttons/styles/material.css';
import '@syncfusion/ej2-dropdowns/styles/material.css';
import "@syncfusion/ej2-documenteditor/styles/material.css";
import { DocumentEditorContainerComponent, Toolbar } from '@syncfusion/ej2-react-documenteditor';
import styles from './style/index.module.less';
import Button from "@/components/Button";
DocumentEditorContainerComponent.Inject(Toolbar);
function Editor (props) {

  const ref = useRef(null);
  const open = props.open || false;
  const documentId = props.documentId || '';
  const isReadOnly = props.docReadOnly || false;
  const title = props.title || '';
  const onOk = props.onOk || (() => {});
  const onCancel = props.onCancel || (() => {});
  const sfdt = props.sfdt || '';

  const save = useCallback(async () => {
    const blob = await ref.current.documentEditor.saveAsBlob('Docx');
    onOk(blob, documentId);
  }, [documentId])

  useEffect(() => {
    if (ref.current && sfdt) {
      try {
        ref.current.documentEditor.open(sfdt);
        ref.current.documentEditor.showRestrictEditingPane(false);
        ref.current.documentEditor.showRestrictEditingPane(false);
        ref.current.documentEditor.isReadOnly = isReadOnly;
      } catch (e) {
        console.error(e);
      }
    }
  }, [sfdt]);

  return (
    <div className={styles['editor']} style={{ display: open ? 'inherit' : 'none'}}>
      <div >
        <div className={styles['editor-header']}>
          <Row justify={'space-between'} align={'middle'}>
            <Col>
              <div className={styles['editor-header-title']}>{isReadOnly ? '[Read Only]' : ''} {title}</div>
            </Col>
            <Col>
              <Space>
                <Button onClick={onCancel}>Close</Button>
                <Button
                  hidden={isReadOnly}
                  onClick={save}
                  type={'primary'}
                >
                  Save
                </Button>
              </Space>
            </Col>
          </Row>
        </div>
        <div className={styles['editor-content']}>
          <DocumentEditorContainerComponent
            ref={ref}
            id="container"
            enableToolbar={true}
            toolbarItems={[
              "Undo",
              "Redo",
              "Find",
            ]}
            showPropertiesPane={!isReadOnly}
            locale="en-US"
            height={'calc(100vh - 62px)'}
          />
        </div>
      </div>
    </div>
  );
}

export default Editor;
