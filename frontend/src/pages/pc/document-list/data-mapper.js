import {
  TYPE
} from "@/config/enum";

const dataMapper = {
  fsdRef: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  bdRef: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  sectionCd: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  licenceTypeId: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  template: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  createdDateFrom: {
    mapper: [TYPE.FILTER],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  createdDateTo: {
    mapper: [TYPE.FILTER],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  status: {
    mapper: [TYPE.CREATE, TYPE.FILTER],
    required: [],

  },
}

export { dataMapper };