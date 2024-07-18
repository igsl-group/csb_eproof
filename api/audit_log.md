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
