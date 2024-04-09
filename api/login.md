## [Post] /sign
### Return uid and dp_dept_id as cookies
```typescript
// request
type request = {
  uid: string,
  dpDeptId: string,
}
```

## [Get] /sso
### Return Access Token as cookies and json

### Process
1. Read request Cookies `uid` and `dp_dept_id`
2. Find user table where uid = ${uid} and dpDeptId = ${dp_dept_id} and status = 'Active'
3. If user exist, return a jsonwebtoken as cookies named `eToken` and response body
4. else, return error code 401

> use this key to sign jwt `YPgDN15TCf1z3KMn745eTQ==`
> ```
> Cookies info:
> uid: string
> email: string
> dpDeptId: string
> post: string
> role: {
>   id: number,
>   name: string,
> }[]
> ```

```typescript
// response
type response = {
  success: true,
  message: 'Success',
  code: 200,
  result: {
    accessToken: string
  }
}

// response 401
type response = {
  success: false,
  code: 401,
  message: string,
}
```

## [Get] /logout
### Logout User

### Process
1. remove `eToken` cookies

```typescript
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