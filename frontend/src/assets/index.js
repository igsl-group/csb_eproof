import React from "react";
import IconClipboardText from "./clipboard-text.svg";
import IconDownload from "./download.svg";
// import IconElementEqual from "./element-equal.svg";
import IconEraser from "./eraser-1.svg";
import IconFilterSearch from "./filter-search.svg";
import IconFolder from "./folder-2.svg";
import IconHome from "./home-2.svg";
import IconLogo from "./logo_en 1.svg";
import IconSetting from "./setting-2.svg";
import IconTask from "./task.svg";
import IconTaskSquare from "./task-square.svg";
import Icon, { HomeOutlined } from '@ant-design/icons';

const ElementEqualSvg = () => (
  <svg viewBox="0 0 24 24" width="0.75em" height="0.75em" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
    <path d="M19.77 11.25H15.73C13.72 11.25 12.75 10.27 12.75 8.27V4.23C12.75 2.22 13.73 1.25 15.73 1.25H19.77C21.78 1.25 22.75 2.23 22.75 4.23V8.27C22.75 10.27 21.77 11.25 19.77 11.25ZM15.73 2.75C14.55 2.75 14.25 3.05 14.25 4.23V8.27C14.25 9.45 14.55 9.75 15.73 9.75H19.77C20.95 9.75 21.25 9.45 21.25 8.27V4.23C21.25 3.05 20.95 2.75 19.77 2.75H15.73Z" />
    <path d="M8.27 11.25H4.23C2.22 11.25 1.25 10.36 1.25 8.52V3.98C1.25 2.14 2.23 1.25 4.23 1.25H8.27C10.28 1.25 11.25 2.14 11.25 3.98V8.51C11.25 10.36 10.27 11.25 8.27 11.25ZM4.23 2.75C2.89 2.75 2.75 3.13 2.75 3.98V8.51C2.75 9.37 2.89 9.74 4.23 9.74H8.27C9.61 9.74 9.75 9.36 9.75 8.51V3.98C9.75 3.12 9.61 2.75 8.27 2.75H4.23Z" />
    <path d="M8.27 22.75H4.23C2.22 22.75 1.25 21.77 1.25 19.77V15.73C1.25 13.72 2.23 12.75 4.23 12.75H8.27C10.28 12.75 11.25 13.73 11.25 15.73V19.77C11.25 21.77 10.27 22.75 8.27 22.75ZM4.23 14.25C3.05 14.25 2.75 14.55 2.75 15.73V19.77C2.75 20.95 3.05 21.25 4.23 21.25H8.27C9.45 21.25 9.75 20.95 9.75 19.77V15.73C9.75 14.55 9.45 14.25 8.27 14.25H4.23Z"/>
    <path d="M21 16.25H15C14.59 16.25 14.25 15.91 14.25 15.5C14.25 15.09 14.59 14.75 15 14.75H21C21.41 14.75 21.75 15.09 21.75 15.5C21.75 15.91 21.41 16.25 21 16.25Z" />
    <path d="M21 20.25H15C14.59 20.25 14.25 19.91 14.25 19.5C14.25 19.09 14.59 18.75 15 18.75H21C21.41 18.75 21.75 19.09 21.75 19.5C21.75 19.91 21.41 20.25 21 20.25Z" />
  </svg>
)

const StepBackwardOutlined = () => (
  <svg viewBox="0 0 1024 1024" focusable="false" data-icon="step-backward" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M347.6 528.95l383.2 301.02c14.25 11.2 35.2 1.1 35.2-16.95V210.97c0-18.05-20.95-28.14-35.2-16.94L347.6 495.05a21.53 21.53 0 000 33.9M330 864h-64a8 8 0 01-8-8V168a8 8 0 018-8h64a8 8 0 018 8v688a8 8 0 01-8 8"></path></svg>
)

const IconElementEqual = (props) => <Icon component={ElementEqualSvg} {...props} />;
const IconStepBackwardOutlined = (props) => <Icon component={StepBackwardOutlined} {...props} />;

export {
  IconClipboardText,
  IconDownload,
  IconElementEqual,
  IconStepBackwardOutlined,
  IconEraser,
  IconFilterSearch,
  IconFolder,
  IconHome,
  IconLogo,
  IconTask,
  IconSetting,
  IconTaskSquare,
}
