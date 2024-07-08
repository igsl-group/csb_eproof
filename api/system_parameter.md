## [PUT] /systemParam/list
### List system parameter
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
        id: string | number,
        key: string,
        value: string,
        description: string,

    }[]
}

// response 400
type response = {
    success: false,
    code: 400,
    message: string,
}
```

## [PUT] /systemParam/${id}
### Freeze an exam profile
> - Set value only, key and description are **not** allowed to update

```typescript
type request = {
    value: string
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