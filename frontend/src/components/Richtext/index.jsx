import React, { useRef, useState, useEffect } from 'react';
import { Form, Input } from "antd";
// import { CKEditor } from '@ckeditor/ckeditor5-react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './style/style.css';
// NOTE: Use the editor from source (not a build)!
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

// import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
// import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
// import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
// import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';


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
  const row = props.row || 4;

  useEffect(() => {
    if (ref.current) {
      ref.current.editor.scrollingContainer.style.height = `${row * 32}px`;
      // console.log('kkkkkk',  `${row * 32}px`);
    }
    window.r = ref;
  }, [ref, row]);


  // useEffect(() => {
  //   console.log(props.row )
  //   console.log(ref.current.editor.scrollingContainer.style.height)
  // })

  return (
    <Form.Item
      style={{
        ...getStyle(props),
      }}
      name={props.name}
      label={props.label}
      rules={[
        {required, message: 'Required'},
        ...validation
      ]}
      hidden={hidden}
      normalize={(val) => {
        return /^<[a-z][0-9]{0,1}><br><\/[a-z][0-9]{0,1}>$/.test(val)  ? '' : val
      }}
    >
      <ReactQuill
        readOnly={disabled}
        ref={ref}
        theme="snow"
        // modules={{
        //   toolbar:[
        //     ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
        //     ['blockquote', 'code-block'],
        //     ['link', 'image', 'video', 'formula'],
        //
        //     [{ 'header': 1 }, { 'header': 2 }],               // custom button values
        //     [{ 'list': 'ordered'}, { 'list': 'bullet' }, { 'list': 'check' }],
        //     [{ 'script': 'sub'}, { 'script': 'super' }],      // superscript/subscript
        //     [{ 'indent': '-1'}, { 'indent': '+1' }],          // outdent/indent
        //     [{ 'direction': 'rtl' }],                         // text direction
        //
        //     [{ 'size': ['small', false, 'large', 'huge'] }],  // custom dropdown
        //     [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        //
        //     [{ 'color': [] }, { 'background': [] }],          // dropdown with defaults from theme
        //     [{ 'font': [] }],
        //     [{ 'align': [] }],
        //
        //     ['clean']                                         // remove formatting button
        //   ]
        // }}
        value={value}
        onChange={(val) => setValue(val)}/>
    </Form.Item>

  );
}

export default Richtext;
