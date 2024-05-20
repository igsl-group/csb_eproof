## [POST] /cert/search/:type
### Search and return cert list according to the search parameter
> Remark 1: Please create different API for each type. DO NOT USE SAME API WITH DIFFERENT PATH_VARIABLE, as this will be difficult to control whether the user is allowed to call this searching endpoint.

> Remark 2: Create a generic function for searching at service layer. The search parameter will use the param from request, but the status will only use the status passed from controller.


```typescript
// default query
const sortField = 'id';    // table field name
const sortDirection= 'asc';  // asc|desc
const size = 20;
const page = 0;

type query_param = {
  type: string -> IMPORTED | GENERATED | SIGN_ISSUE | NOTIFY | VALID | INVALID
} 

type request = {
  examSerialNo: string,
  canSerialNo: string,
  name: string,
  cname: string,
  email: string
  hkid: string,
  passport: string,
  onHold: boolean,
  onHoldRemark: string
}


// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
    total:  number,
    offset: number,
    limit: number, 
    data: {
      id: number,
      can_serial_no: string,
      exam_date: date,
      name: string,
      cname: string,
      hkid: string,
      passport: string,
      email: string,
      blGrade: string,
      ueGrade: string,
      ucGrade: string,
      atGrade: string,
      remark: string,
      onHold: Boolean,
      onHoldRemark: string
      certPdfId: number
      createdBy: string,
      createdDate: "YYYY-YY-DD HH:MM:ss",
      modifiedBy: string,
      modifiedDate: "YYYY-YY-DD HH:MM:ss",
    }[]
  }
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```


## [GET] /cert/download/:certId
### Download the PDF of given cert ID
> Remark: Remember to set the response header for the downloaded file details, such as file name, file type, etc.

```typescript
type query_param={
  certId: number
}

// response 200
type response= BINARY DATA

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [POST] /cert/batch/dispatch/:examProfileSerialNo
### Dispatch the cert with status = completed and stage = current stage and on hold = false
> Remark 1: Only dispatch cert with on_hold = false
```typescript

// 200

type query_param = {
  currentStage: string -> IMPORTED | GENERATED | SIGN_ISSUE | NOTIFY
}


type response = {
  success: true,
  code: 200,
  message: string,
}

// 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [POST] /cert/batch/import/:examProfileSerialNo
### Upload cert record to a serial number of exam profile

```typescript
type form_date = {
  file: File #The CSV
}

type response = {
  success: true,
  code: 200,
  message: string,
}

```

