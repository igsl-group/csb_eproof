## [GET] /role?limit={number}&offset={number}&orderby={asc|desc}&sort_field={string}
### Return a role's list
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

## [GET] /role/{roleId}
### Return a Role information
```typescript
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

## [Post] /role
### Create a Role
```typescript
// request
type request = {
    name: string,
    description: string,
    permission: {
        id: number
    }[],
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

## [Put] /role/{roleId}
### Update User

```typescript
// request
// form-data
type request = {
    name: string,
    description: string,
    permission: {
        id: number
    }[],
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

## [Delete] /role/{roleId}
### Removes a role from the role list

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
