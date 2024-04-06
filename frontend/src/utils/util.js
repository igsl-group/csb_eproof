import queryString from 'query-string';
import dayjs from 'dayjs';

export const API_ERROR_MESSAGE = "System error. Please contact system administrator for help."
export const LOGIN_ERROR_MESSAGE = "The server could not sign you in. Make sure your user name and password are correct, and then try again."
export const FIELD_INPUT_EMPTY_MESSAGE = "Required"
export const FIELD_INVALID_DIGIT_MESSAGE = "Should contain digits only"
export const FIELD_MAX_LENGTH_MESSAGE = "Should not be more than {0} characters"
export const FIELD_MIN_LENGTH_MESSAGE = "Should not be fewer than {0} characters"
export const FIELD_INVALID_FULL_NAME_MESSAGE = "Should contain alphanumeric characters only"
export const FIELD_INVALID_CODE_MESSAGE = "Should contain a-z, A-Z, 0-9 and - _ only"
export const FIELD_INVALID_PHONE_NUMBER_MESSAGE = "Incorrect phone number"
export const FIELD_INVALID_USERNAME_MESSAGE = "Should contain a-z, A-Z, 0-9 and - _ . @ only"
export const FIELD_ACCEPT_ALPHABETS_MESSAGE = "Should contain alphabets characters only"
export const FIELD_ACCEPT_ALPHANUMERIC_ONLY_MESSAGE = "Should contain alphanumeric characters only"
export const FIELD_INVALID_EMAIL_MESSAGE = "Incorrect email format"
export const PASSWORD_NOT_MATCH_MESSAGE = "Passwords don't match"
export const END_TIME_BEFORE_START_TIME_MESSAGE = "The end time should be after the start time."
export const FIELD_SELECT_EMPTY_MESSAGE = "Should have at least 1 selections"
export const DELETE_SUCCESS_MESSAGE = "Delete Success!";
export const COPY_SUCCESS_MESSAGE = "Copy Success!";
export const ADD_SUCCESS_MESSAGE = "Add Success!";
export const UPDATE_SUCCESS_MESSAGE = "Update Success!";
export const OPERATE_SUCCESS_MESSAGE = "Operate Success!";
export const PASSWORD_DIFFERENT_MESSAGE = "The two password entries are inconsistent!";
export const DELETE_CONFIRM_MESSAGE = "Are you sure to delete?";
export const COPY_CONFIRM_MESSAGE = "Are you sure to copy?";
export const APPROVE_CONFIRM_MESSAGE = "Are you sure to approve?";
export const REJECT_CONFIRM_MESSAGE = "Are you sure to reject?";
export const PENDING_CONFIRM_MESSAGE = "Are you sure to pending?";
export const DOWNLOAD_CONFIRM_MESSAGE = "Are you sure to download?";
export const OK = "Yes";
export const CANCEL = "Cancel";
export const EMAIL_SEND_SUCCESS_MESSAGE = "The reset password method is sent to the same email. Please check your mail box.";
export const RESET_PASSWORD_SUCCESS_MESSAGE = "Your password has been changed!";
export const RESET_PASSWORD_NOT_SAME_MESSAGE = "Please type the same password twice!";
export const PASSWORD_ISSUE_MESSAGE = "Select at least three conditions!";
export const PASSWORD_NOT_STRONG_MESSAGE = "Your password is not strong";
export const CHANGE_STATUS_FREEZE_MESSAGE = "Are you sure you want to freeze?";
export const CHANGE_STATUS_UNFREEZE_MESSAGE = "Are you sure you want to unfreeze?";
export const FORCED_LOGOUT_MESSAGE = "Yon are forced logout since you have been idle over 2 hours.";

export const toQueryString = (pagination = {}, others = {}) => {
  let paginationInfo = {};
  if (Object.keys(pagination).length > 0) {
    paginationInfo = {
      page: pagination.page - 1 || 0,
      pageSize: pagination.pageSize || 10,
      orderBy: pagination.sortBy || '',
      isDescending: pagination.orderBy !== "ascend",
    }
  }

  return queryString.stringify({
    ...paginationInfo,
    ...others
  })
}

export const toBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
  })
};

export const download = (response = {}) => {
  const {
    data = "",
    request = {}
  } = response;

  const href = URL.createObjectURL(data);

  const link = document.createElement('a');
  link.href = href;

  let fileName = `${dayjs().format('YYYY-MM-DD')}.pdf`;
  if (request.getResponseHeader('Content-Disposition')) {
    fileName = decodeURI(request.getResponseHeader('Content-Disposition').split('filename=')[1].replaceAll('"', ''))
  }
  link.setAttribute('download', fileName);
  document.body.appendChild(link);
  link.click();

  document.body.removeChild(link);
  URL.revokeObjectURL(href);
}

export const previewPdf = (response = {}) => {
  const {
    data = "",
  } = response;

  const file = new Blob([data], { type: 'application/pdf' });
  const url = URL.createObjectURL(file);
  window.open(url);
}

export const getFilename = (response = {}) => {
  const {
    request = {}
  } = response;

  let fileName = "";
  if (request.getResponseHeader('Content-Disposition')) {
    fileName = decodeURI(request.getResponseHeader('Content-Disposition').split('filename=')[1].replaceAll('"', ''))
  }
  return fileName;
}
