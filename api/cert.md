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

> Remark 1: find all cert info under same :examProfileSerialNo, where cert.on_hold = false and cert.status = SUCCESS

> Remark 2: Follow this sequence to decide what is the next stage: IMPORTED -> GENERATED  -> SIGN_ISSUE -> NOTIFY -> COMPLETED. The stage are all stored in CertStage.java, you may reference to that class.



> Remark 3: If above condition are all checked and passed, you may start dispatch the cert to next stage.

> Remark 4: If request gave stage that are out of those 4 stage listed below, throw error.

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

> Remark 1: Validate each row in CSV first

> Remark 2: Validation condition
> + Passport or HKID, either one must exist
> + HKID must be unique within exam profile serial no (Including excel content and DB record)
> + Passport number must be unique within exam profile serial no (Including excel content and DB record)
> + Email must in valid format ( Can use regex )
> + Exam date in CSV must as same as the exam profile date
> + Letter type must in either P or F character

> Remark 3: Remove "(" and ")" in HKID


> Remark 4: Use the index of header of CSV, instead of using exact index
>>For example, to get HKID in csv row
>> + CSV header: Exam Date, Name in English, HKID
>> + ❌ csvRow.get(2);
>> + ✅ csvRow.get(csvHeader.indexOf(HKID))


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


## [Post] /cert/batch/updateEmail
### Update the email of all cert under the provided hkid in request
> Steps:
> 1. Frontend send request
> 2. Backend got the request, find all VALID cert under same HKID or passport
     > 優先看currentHkid, 如果currentHkid===null, 才看 currentPassport
     > 即使currentHkid 及 currentPassport同時存在, 也只需要看 currentHkid
     > 如果 currentHkid == null 及 currentPassport == null, return 400
> 3. Update the email of those certs
> 4. Save the certs

```typescript
// request
type requestBody = { 
  currentHkid: string,
  currentPassport: string
  newEmail: string
}

// 200
type response = {
  success: true,
  code: 200,
  message: string,
}
```



## [Post] /cert/batch/updatePersonalParticular
### Update the personal particular of all cert under the provided hkid in request
> Steps:
> 1. Frontend send request
> 2. Backend got the request, find all VALID cert under same HKID
> 3. Update the personal information of those certs
> 4. If all validation passed, create a new record in cert_info_renew table. The new values and old values that not related to personal particular should be same. 
> 5. Set the old personal information to existing personal information. Set the new personal information to new inputted personal information.
> 6. Set the cert_info_renew's stage to Renewed, status to Success

```typescript
// request
type requestBody = {
  currentHkid: string,
  newEmail: string,
  newHkid: string,
  newPassport: string,
  remark
}

// 200
type response = {
  success: true,
  code: 200,
  message: string,
}
```

## [Post] /cert/single/updateResult/:certInfoId
### Update the result of a single cert by the cert info id in the path
> Steps:
> 1. Frontend send request
> 2. Backend got the request, find the cert by given certInfoId
> 3. If the cert is not valid, deny the request
> 4. If all validation passed, create a new record in cert_info_renew table. The new values and old values that not related to result should be same. 
> 5. For those 4 exam results, set the old result to existing result. Set the new result to new inputted result.
> 6. Set the cert_info_renew's stage to Renewed, status to Success

```typescript

type query_param = {
  : number
}
// request
type requestBody = {
  newUeGrade: string,
  newUcGrade: string,
  newAtGrade: string,
  newBlnstGrade: string,
  remark: string 
}

// 200
type response = {
  success: true,
  code: 200,
  message: string,
}
```


## [Post] /cert/batch/notify/:examProfileSerialNo
### Notify the cert under same exam profile
> Steps:
> 1. Get all cert under exam the exam profile, which the email not yet sent
> 2. If no email event is found for the cert >> Add a new email_message with rendered HTML using thymeleaf, and add the correspond email_event for the message.
      >> Please create a email_template with key "NOTIFY_CERT", type "EXTERNAL" first
> 3. Else if email event is found for the cert BUT the email not yet sent >> Update scheduled time in email_event of the email_message.
> 4. Else if email event is found AND the email has been sent >> ignore. No action needed.

```typescript

type path_variable = {
  examProfileSerialNo: string
}

// 200
type response = {
  success: true,
  code: 200,
  message: string,
}
```

