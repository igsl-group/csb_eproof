## [POST] /examProfile
### Create an exam profile
> - Remark 1: When create an exam profile, set is_freezed to false by default 
> - Remark 2: Serial number should be unique. Check if any existing serial number existed in DB.

```typescript
// request
type request = {
    serial_no: string
    exam_date: date,
    location: string,
    status: Active | Disable
}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {}
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [PUT] /examProfile/freeze/{examProfileSerialNo}
### Freeze an exam profile
> - Set the is_freezed column value of specified exam profile
```typescript
type request = {

}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {}
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```


## [POST] /examProfile/reset/{examProfileSerialNo}
### Reset an exam profile

> - For not yet generated cert under this exam profile: 
>   - Stop processing those certs
> - For already generated cert under this exam profile:
>   - Revoke those results on OGCIO eProof system
```typescript

type path_variable = {
    examProfileSerialNo: string
}
type request = {

}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {}
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [GET] /examProfile/{examProfileSerialNo}
### Get one exam profile's detail
```typescript
type request = {

}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
      serial_no: string,
      exam_date: date,
      location: string,
      announcedTime:datetime
  }
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```


## [GET] /examProfile/list
### Get all exam profile
```typescript
//DEFAULT VALUE
const sortby = 'id';    // table field name
const orderby = 'asc';  // asc|desc
const limit = 20;
const offset = 0;
const keyword = '';

type query_param = {
    sortby: string,
    orderby: string,
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
      serial_no: string,
      exam_date: date
  }[]
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [GET] /examProfile/dropDown
### Get a drop-down list of all exam profile
> - Remark 1: Query the exam profile where is_freezed = false;
```typescript
type request = {

}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
      serial_no: string
  }
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [DELETE] /examProfile/delete/{examProfileSerialNo}
### Delete an exam profile
> - Remark 1: Only allow to delete exam_profile if no related cert_info is found under this profile
```typescript
type request = {

}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
  }
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```