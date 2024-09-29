import React, { useRef, useState, useEffect } from 'react';
import { Form, Input } from "antd";
// import { CKEditor } from '@ckeditor/ckeditor5-react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './style/style.css';
import Quill from "quill";
import 'quill-mention-react'
import 'quill-mention';
import 'quill-mention/dist/quill.mention.css';
const SizeStyle = Quill.import('attributors/style/size');
SizeStyle.whitelist = ['10px', '12px', '14px', '16px', '18px', '24px', '32px'];
Quill.register(SizeStyle, true);
let mentionDisabled = false;
const data = [
  { id: 1, name: 'application_name' },
  { id: 2, name: 'eproof_document_url' },
  { id: 3, name: 'examination_date' },
  { id: 4, name: 'one_time_password' }
]
window.ref = null;

const getModules = {
  toolbar: [
    [{ size: SizeStyle.whitelist }], // Integrate custom sizes
    ['bold', 'italic', 'underline', 'strike'],
    [{ color: [] }, { background: [] }],
    [{ align: [] }],
    [{ list: 'ordered' }, { list: 'bullet' }],
    ['link'],
    ['clean'],
  ],
  mention: {
    mentionDenotationChars: ['$'],
    source: function (searchTerm, renderList, mentionChar) {
      const values = data.map(item => ({ id: item.id, value: `${item.name}` }));
      const matchedValues = values.filter(item => item.value.toLowerCase().includes(searchTerm.toLowerCase()));
      if (mentionDisabled) {
        renderList(matchedValues, searchTerm);
      }
    },
    onSelect: (data) => {
      if (window.ref.current) {
        const quill = window.ref.current.getEditor(); // Reference from useRef
        const cursorPosition = quill.getSelection().index;
        const value = `{${data.value}}`
        quill.insertText(cursorPosition, value); // Insert selected mention
        quill.setSelection(cursorPosition + value.length);
      }

    }
  }
}

const formats = [
  'size',
  'bold', 'italic', 'underline', 'strike',
  'list', 'bullet',
  'link',
  'color', 'background',
  'align',
];

const getStyle = (props) => {
  const style = {};
  let size = props.size || (props.check && props.check.maxLength) || 15;
  if (props.placeholder && props.placeholder.length > size) {
    size = props.placeholder.length;
  }

  if (size) {
    style.maxWidth = `${(size * 17) + 24}px`;
    style.width = `100%`;
  }

  return style;
};

function Richtext (props) {
  const [value, setValue] = useState('');
  const ref = useRef(null);
  const validation = props.validation || [];
  const required = props.required || false;
  const disabled = props.disabled || false;
  const hidden = props.hidden || false;
  const row = props.row || 10;
  mentionDisabled = props.mentionDisabled || false;

  useEffect(() => {
    if (ref.current) {
      const editorElement = ref.current.getEditor().root;
      const baseHeight = 32; // Base height for a single row
      setTimeout(() => {
        console.log(editorElement.style.height, row, baseHeight)
        // editorElement.style.height = `${row * baseHeight}px`;
      }, 1000);
    }
    window.ref = ref;
  }, [ref, row]);


  // useEffect(() => {
  //   console.log(props.row )
  //   console.log(ref.current.editor.scrollingContainer.style.height)
  // })

  return (
    <Form.Item
      style={{
        ...getStyle(props),
        height: `${row * 32 + 42+2+8+8}px`,
      }}
      name={props.name}
      label={props.label}
      rules={[
        {required, message: 'Required'},
        ...validation
      ]}
      hidden={hidden}
      // normalize={(val) => {
      //   return /^<[a-z][0-9]{0,1}><br><\/[a-z][0-9]{0,1}>$/.test(val)  ? '' : val
      // }}
    >
      <ReactQuill
        readOnly={disabled}
        ref={ref}
        theme="snow"
        modules={getModules}
        value={value}
        formats={formats}
        style={{
          height: `${row * 32}px`, /* Adjust this value as necessary */
          // maxHeight: '500px', /* Max height, can be omitted if not required */
          // overflowY: 'auto',
        }} // Set the desired height here
        onChange={(val) => setValue(val)}/>
    </Form.Item>

  );
}

export default Richtext;
