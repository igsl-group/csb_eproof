import {
  TYPE
} from "@/config/enum";

const dataMapper = {
  id: {
    mapper: [TYPE.EDIT],
    required: [TYPE.CREATE],
    converter: (value) => value,
  },
  sectionCd: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  licenceType: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  bd: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (value) => value ? value : null,
  },
  bdRef: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  fsdRef: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  bdCaseOfficerName: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  lifipsWorkflowId: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  caseOfficer: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  bdCaseOfficerContact: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  bdCaseOfficerEmail: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  applicationDate: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
    converter: (value) => {
      return value && value.format('YYYY-MM-DD')
    },
  },
  applicantNameEng: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  applicantNameChi: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  corrAddress: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  contactNameEng: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  contactNameChi: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  hkidNo: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  brcNo: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  positionInCompany: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  premisesAddress: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  contactPhoneFax: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  authorizedMobile: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  authorizedEmail: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  },
  contactPhoneOffice: {
    mapper: [TYPE.CREATE, TYPE.EDIT],
    required: [],
  }
}

export { dataMapper };