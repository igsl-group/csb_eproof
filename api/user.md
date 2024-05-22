## [GET] /user?limit={number}&offset={number}&orderby={asc|desc}&sort_field={string}
### Return a user's list
```typescript
// default query
const sort_field = 'id';    // table field name
const sort_direction = 'asc';  // asc|desc
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
      dpUserId: string,
      name: string,
      post: string,
      email: string,
      status: string,
      lastLoginDate: string,
      role: {
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

## [GET] /user/{userId}
### Return a User information
```typescript
// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
    id: number,
    name: string,
    post: string,
    email: string,
    status: string,
    lastLoginDate: string,
    role: {
      id: number,
      name: string,
    }[],
    createdBy: string,
    createdDate: "YYYY-YY-DD HH:MM:ss",
    modifiedBy: string,
    modifiedDate: "YYYY-YY-DD HH:MM:ss",
  }
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [Post] /user
### Create a User
> Remark: Auto insert `dp_dept_id` = csb
```typescript
// request
type request = {
  name: string,
  post: string,
  email: string,
  status: Active | Disable,
  role: number[],
}

// response 200
type response = {
  success: true,
  message: 'Success',
  code: 200
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [Patch] /user/{userId}
### Update User

```typescript
// request
// form-data
type request = {
  name: string,
  post: string,
  email: string,
  status: Active | Disable,
  role: number[],
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

## [Delete] /user/{userId}
### Removes a user from the user list

```typescript
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
