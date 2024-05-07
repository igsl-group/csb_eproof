## [GET] /cert/import/{examSerialNumber}
### Return a role's list
```typescript
// default query
const sortby = 'id';    // table field name
const orderby = 'asc';  // asc|desc
const limit = 20;
const offset = 0;
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
      name: string,
      description: string,
      permission: {
        id: number,
        name: string,
      }[],
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