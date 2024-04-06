import {
  FIELD_INVALID_EMAIL_MESSAGE,
  FIELD_ACCEPT_ALPHANUMERIC_ONLY_MESSAGE,
  FIELD_INVALID_USERNAME_MESSAGE,
  FIELD_INVALID_PHONE_NUMBER_MESSAGE,
  FIELD_INVALID_DIGIT_MESSAGE,
  FIELD_MAX_LENGTH_MESSAGE,
  FIELD_MIN_LENGTH_MESSAGE,
  FIELD_INVALID_FULL_NAME_MESSAGE,
  FIELD_INVALID_CODE_MESSAGE,
  FIELD_INPUT_EMPTY_MESSAGE
} from "@/utils/util";
import format from "@/utils/string-template";

export const validators = {
  codeValidator: () => {
    const maxLength = 15;
    return {
      validator: (_, val) => {
        if (val) {
          if (!/^[a-zA-Z0-9_-]*$/.test(val)) {
            return Promise.reject(new Error(FIELD_INVALID_CODE_MESSAGE));
          } else if (val.length > maxLength) {
            return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
          }
        }
        return Promise.resolve();
      },
    }
  },
  alphanumericValidator: () => {
    return {
      validator: (_, val) => {
        if (val) {
          if (!/^[\x00-\x7F]*$/.test(val)) {
            return Promise.reject(new Error(FIELD_ACCEPT_ALPHANUMERIC_ONLY_MESSAGE));
          }
        }
        return Promise.resolve();
      },
    }
  },
  minLengthValidator: (max = 8) => {
    return {
      validator: (_, val) => {
        if (val && val.length < max) {
          return Promise.reject(new Error(format(FIELD_MIN_LENGTH_MESSAGE, max)));
        }
        return Promise.resolve();
      }
    }
  },
  maxLengthValidator: (maxLength = 30) => {
    return {
      validator: (_, val) => {
        if (val) {
          if (val.length > maxLength) {
            return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
          }
        }
        return Promise.resolve();
      },
    }
  },
  emailValidator: (maxLength = 100) => {
    return {
      validator: (_, val) => {
        if (!val) {
          return Promise.resolve();
        }

        const emails = typeof val === "string" ? [val] : val;
        for (const email of emails) {
          if (email) {
            if (!/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email)) {
              return Promise.reject(new Error(FIELD_INVALID_EMAIL_MESSAGE));
            } else if (email.length > maxLength) {
              return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
            }
          }
        }
        return Promise.resolve();
        // console.log(val)
        // if (val) {
        //   if (val.length > maxLength) {
        //     return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
        //   } else if (!/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(val)) {
        //     return Promise.reject(new Error(FIELD_INVALID_EMAIL_MESSAGE));
        //   }
        // }
        return Promise.resolve();
      },
    }
  },
  fullNameValidator: () => {
    const maxLength = 30;
    return {
      validator: (_, val) => {
        if (val) {
          if (!/^[\x00-\x7F]*$/.test(val)) {
            return Promise.reject(new Error(FIELD_INVALID_FULL_NAME_MESSAGE));
          } else if (val.length > maxLength) {
            return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
          }
        }
        return Promise.resolve();
      },
    }
  },
  usernameValidator: () => {
    const maxLength = 20;
    return {
      validator: (_, val) => {
        if (val) {
          if (!/^[a-zA-Z0-9-_.@]*$/.test(val)) {
            return Promise.reject(new Error(FIELD_INVALID_USERNAME_MESSAGE));
          } else if (val.length > maxLength) {
            return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
          }
        }
        return Promise.resolve();
      },
    }
  },
  phoneValidator: (props) => {
    const{
      maxLength = 8,
      minLength = 8
    } = props;
    return {
      validator: (_, val) => {
        if (val) {
          if (!/^[0-9]*$/.test(val)) {
            return Promise.reject(new Error(FIELD_INVALID_PHONE_NUMBER_MESSAGE));
          } else if (val.length > maxLength) {
            return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
          } else if (val.length < minLength) {
            return Promise.reject(new Error(format(FIELD_MIN_LENGTH_MESSAGE, minLength)));
          }
        }
        return Promise.resolve();
      },
    }
  },
  digitValidator: (props) => {
    const{
      maxLength = 8,
      minLength = 0
    } = props;

    return {
      validator: (_, val) => {
        if (val) {
          if (!/^[0-9]*$/.test(val)) {
            return Promise.reject(new Error(FIELD_INVALID_DIGIT_MESSAGE));
          } else if (val.length > maxLength) {
            return Promise.reject(new Error(format(FIELD_MAX_LENGTH_MESSAGE, maxLength)));
          } else if (val.length < minLength) {
            return Promise.reject(new Error(format(FIELD_MIN_LENGTH_MESSAGE, minLength)));
          }
        }
        return Promise.resolve();
      },
    }
  }
}