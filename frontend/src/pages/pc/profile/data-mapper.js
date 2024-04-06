import {
  TYPE
} from "@/config/enum";

const dataMapper = {
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
  fsdRef: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  lifipsWorkflowId: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  caseOfficerId: {
    mapper: [TYPE.FILTER],
    required: [],
  },
  searchStartCreateDate: {
    mapper: [TYPE.FILTER],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  searchEndCreateDate: {
    mapper: [TYPE.FILTER],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  status: {
    mapper: [TYPE.FILTER],
    required: [],
  },
}

export { dataMapper };