## Permission list

- CERT_IMPORT
- CERT_DISPATCH
- CERT_REVOKE


## [GET] /permission/dropDown
```typescript
type response = {
    success: true,
    message: 'Success',
    code: 200,
    result: {
        id: number,
        name: string,
        key: string
    }
}
```