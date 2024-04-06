import {
  TYPE
} from "@/config/enum";

const dataMapper = {
  eisCase: {
    mapper: [TYPE.CREATE],
    required: [TYPE.CREATE],
    converter: (fields) => ({
      id: fields.id,
    }),
  },
  id: {
    mapper: [TYPE.EDIT],
    required: [TYPE.EDIT],
  },
  documentTemplateVersion: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [TYPE.CREATE],
    converter: (fields) => ({
      id: fields.id,
    }),
  },
  toList: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (arr) => arr.join(','),
  },
  ccList: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (arr) => arr.join(','),
  },
  preparer: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  approver1: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  approver2: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  despatcher: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  validFrom: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  validTo: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  inspectionDate: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  remarks: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  }
}

export { dataMapper };