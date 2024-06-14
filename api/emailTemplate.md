## [GET] /email/{emailId}
### Get one email
```typescript
type request = {

}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
      key: string,
      subject: string,
      body: string,
      type: string,
      includeEmails: string
  }
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```


## [GET] /email/list
### Get all email
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
      key: string,
      subject: string,
      body: string,
      type: string,
      includeEmails: string
  }[]
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [Patch] /email/{emailId}
### Update email

```typescript
// request
// form-data
type request = {
    key: string,
    subject: string,
    body: string,
    type: string,
    includeEmails: string
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