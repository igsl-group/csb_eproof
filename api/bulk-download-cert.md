## [GET] /cert/download/bulk
### Bulk download the PDF of given cert ID
> 參考這條API [GET] /cert/download/:certId
> Remark: Remember to set the response header for the downloaded file details, such as file name, file type, etc.
> fileName = cert_download-YYYYMMDDTHHMMSSZ.zip
> e.g. cert-download-20240617T155201Z.zip
```typescript
type requestBody = {
  certIds: [
      1,
      2,
      3,
      4,
      ...
  ]
}

// response 200
type response= BINARY DATA

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```