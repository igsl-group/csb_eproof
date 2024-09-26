## [GET] /auditLog/list
### Get all auditLog
```typescript
//DEFAULT VALUE
const sort_field = 'id';    // table field name
const sort_direction= 'asc';  // asc|desc
const limit = 20;
const offset = 0;
const keyword = '';

type query_param = {
    sort_field: string,
    sort_direction: string,
    limit: number,
    offset: number,
    keyword: string
}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
      id: string,
      created_by: string,
      ip_address: string,
      computer_information: string,
      log_details: string,
      log_action: string,
  }[]
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [GET] /emailLog/list?limit={number}&offset={number}&orderby={asc|desc}&sort_field={string}
### Return a email message's list
```typescript
//DEFAULT VALUE
const sort_field = 'id';    // table field name
const sort_direction= 'asc';  // asc|desc
const limit = 20;
const offset = 0;
const keyword = '';
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
      subject: string,
      email_to: string,
      body: string,
      created_date: datetime, // YYYY-MM-DD HH:MM:SS
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

## [GET] /batchEmailLog/list?limit={number}&offset={number}&orderby={asc|desc}&sort_field={string}
### Return a gcis_batch email list
```typescript
//DEFAULT VALUE
const sort_field = 'id';    // table field name
const sort_direction= 'asc';  // asc|desc
const limit = 20;
const offset = 0;
const keyword = '';
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

        // < gcis_batch_email.id <-> cert_info.gcis_batch_email_id 注意, 可能要group by?
        examProfileSerialNo: string, 
        
        created_date: datetime,
        batchUploadRefNum: string,
        batch_upload_status: string,
        scheduleJobId: string,
        scheduleJobStatus: string,
        scheduleEstEndTime: datetime, // string to YYYY-MM-DD HH:MM:SS
        scheduleEstStartTime: datetime, // string to YYYY-MM-DD HH:MM:SS
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