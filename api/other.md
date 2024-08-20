## [post] /enquiryResult/csv
1. user upload .csv (e.g. ./support-doc/enquiry.csv)
2.  Remove "(" and ")" in HKID 
3. Remove all space e.g. " A12345 " -> " A12345 "
```csv
HKID/Passport
A123456
A 123456(3)    <---- HKID, 把Space 和 ( ) 拿掉
H1234567890
```


3. query condition
### Case 1:
```sql

select * from cert_info table where isValid = ture and isPassed = false
and (hkid IN (xxxxx, xxxxx, .....) OR passport IN (xxxxx, xxxx, ....));
```
Assume return data:

| id | exam_date  | name         | hkid   | passport | ue_grade | uc_grade | at_grade | blnst_grade |
|----|------------|--------------|--------|----------|----------|----------|----------|-------------|
| 1  | 2022-01-21 | Chan Tai Man | A12345 |          | P        | F        | P        | F           |
| 2  | 2024-01-21 | Chan Tai Man | A12345 |          | P        | F        | P        | F           |
| 3  | 2024-02-21 | Wong Tai Man |        | H12356   |          | F        | P        |             |

Return CSV:
> Remark Return Exam Date: 21/1/2024
```csv
Exam Date,Name in English,HKID,Passport No.,UE Grade,UC Grade,AT Grade,BLNST Grade
21/1/2024,Chan Tai Man,A12345,,P,F,P,F  <---- Case 1 - id = 1
21/1/2022,Chan Tai Man,A12345,,,P,,P    <---- Case 1 - id = 2
21/1/2024,Wong Tai Man,,H12356,,F,P,,   <---- Case 1 - id 3
```

### Case: 2
```
select * from combined_historical_result_before_2024 where
and (hkid IN (xxxxx, xxxxx, .....) OR passport IN (xxxxx, xxxx, ....));
```

Assume return data:

| id | exam_date  | name        | hkid   | passport | ue_grade | ue_void | ue_date     | uc_grade | uc_void | uc_date    |
|----|------------|-------------|--------|----------|----------|---------|-------------|----------|---------|------------|
| 1  | 2022-01-21 | Lee Tai Man | C12355 |          | Pass     | true    | 2011-01-01  | Pass     |         | 2015-01-01 |
| 2  | 2024-01-21 | Ip Tai Man  | O99999 |          | Pass     |         | 2011-01-01  | Pass     | true    | 2015-01-01 |
| 3  | 2024-02-21 | Lai Tai Man |        | H12356   | Pass     |         | 2011-01-01  | Pass     |         | 2015-01-01 | 

Return CSV
> Remark Return Exam Date: 21/1/2024
> e.g. id = 1, 如果 ue_void = true, 該 export csv record 不需要顯示 ue_grade 和 ue_date
>  e.g. id = 2, 如果 uc_void = true, 該 export csv record 不需要顯示 uc_grade 和 uc_date
```csv
Exam Date,Name in English,HKID,Passport No.,UE Grade,UR Date   ,UC Grade,UC Date   ,AT Grade,AT Date,BLNST Grade,BLNST Date
21/1/2024,Lee Tai Man,C12355,              ,        ,          ,Pass    ,2025-01-01,        ,       ,           ,           <---- id = 1, UE Grade and Date 不顯示因為 ue_void = true
21/1/2022,Ip Tai Man,O99999                ,Pass    ,2011-01-01,        ,          ,        ,       ,           ,           <---- id = 2, UC Grade and Date 不顯示因為 ue_void = true
21/2/2024,Wong Tai Man,,H12356,            ,Pass    ,2011-01-01,Pass    ,2015-01-01,        ,       ,           ,           <---- id = 3, 全部顯示
```

4. Return exam result result as CSV format
```typescript
type form_data = {
  file: File #The CSV
}

type response = {
  success: true,
  code: 200,
  message: string,
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

## [get] /historicalResult
### Get all historical result

1. look up "Combined_Historical_Result_Before_2024" table
2. 有分頁

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
      name: string,
      hkid: string,
      passport: string,
      email: string,
      blnst_grade: string,
      blnst_date: Date,
      blnst_void: boolean,
      ue_grade: string,
      ue_date: Date,
      ue_void: boolean,
      uc_grade: string,
      uc_grade: Date,
      uc_void: boolean,
      at_grade: string,
      at_date: Date,
      at_void: boolean,
      remark: string
      valid: boolean,
      created_by: string,
      modified_by: string,
      created_date: string,
      modified_date: string,
  }[]
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```


## [post] /historicalResult/{id}/valid
### set valid
```sql
update Combined_Historical_Result_Before_2024
set valid = true,
remark = :remark
where id = :id 
```
```typescript
type request = {
    remark: string,
}


// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [post] /historicalResult/{id}/invalid
### set valid
```sql
update Combined_Historical_Result_Before_2024
set valid = false,
remark = :remark
where id = :id 
```
```typescript
type request = {
    remark: string,
}

// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```

## [post] /historicalResult/{id}/void
### void result by request
```sql
update from Combined_Historical_Result_Before_2024
set
ue_date = today('YYYY-MM-DD'),
ue_void = :isVoid
where id = :id 
```

```typescript
type request = {
    subject: string,    <------ allow 'ue_grade', 'uc_grade', 'at_grade', 'blnst_grade'
    isVoid: boolean,
    remark: string
}


// response 400
type response = {
  success: false,
  code: 400,
  message: string,
}
```