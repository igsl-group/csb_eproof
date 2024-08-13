## [post] /enquiryResult/csv
1. user upload .csv (e.g. ./support-doc/enquiry.csv)
2.  Remove "(" and ")" in HKID 
3. Remove all space e.g. " A12345 " -> " A12345 "
```csv
HKID/Passport
A123456
A123456(3)    <---- HKID
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
| 1  | 2022-01-21 | Chan Tai Man | A12345 |          | Pass     | Fail     | Pass     | Fail        |
| 2  | 2024-01-21 | Chan Tai Man | A12345 |          | Pass     | Fail     | Pass     | Fail        |
| 3  | 2024-02-21 | Wong Tai Man |        | H12356   |          | Fail     | Pass     |             |

Return CSV:
> Remark Return Exam Date: 21/1/2024
```csv
Exam Date,Name in English,HKID,Passport No.,UE Grade,UC Grade,AT Grade,BLNST Grade
21/1/2024,Chan Tai Man,A12345,,Pass,Fail,Pass,Fail  <---- Case 1 - id = 1
21/1/2022,Chan Tai Man,A12345,,,Pass,,Pass  <---- Case 1 - id = 2
21/1/2024,Wong Tai Man,,H12356,,Fail,Pass,,  <---- Case 1 - id 3
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
