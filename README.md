# Employee Data API Documentation

## Overview

This document provides detailed information about the Employee Data API, 
including available endpoints, database schema updates, and logging details. You can view all services and test them using Swagger UI at: [Swagger UI](http://localhost:8080/api/v1/swagger-ui.html)

## Database

```sql
CREATE TABLE employee_data (
  [timestamp] NVARCHAR(50),
  employer NVARCHAR(255),
  location NVARCHAR(255),
  job_title NVARCHAR(255),
  years_at_employer NVARCHAR(250),
  years_of_experience NVARCHAR(250),
  salary NVARCHAR(255),
  salary_amount NVARCHAR(255),
  signing_bonus NVARCHAR(250),
  annual_bonus NVARCHAR(250),
  annual_stock_value_bonus NVARCHAR(255),
  gender NVARCHAR(150),
  additional_comments NVARCHAR(MAX),
  created_timestamp NVARCHAR(50),
);
```
### Salary Column
Change salary to a numeric format for searchable:

```sql
ALTER TABLE employee_data ADD salary_amount decimal(38,0) DEFAULT 0 NULL;
UPDATE employee_data SET salary_amount = TRY_CAST(salary AS decimal(38,0))
WHERE TRY_CAST(salary AS decimal(38,0)) IS NOT NULL;
```
### Timestamp Column
Since timestamp is a reserved word, a new column created_timestamp is added:

```sql
ALTER TABLE employee_data ADD created_timestamp NVARCHAR(50);
UPDATE employee_data SET created_timestamp = [timestamp];
```

## Logging

### Logging directory
```
D:/log/salary.
```

## API Endpoints

### Filter Employees
#### GET /api/v1/employees/filter
```
Filters employee data based on salary range, job title, and gender.
```
#### Parameters:
- salaryFrom and salaryTo: Allow range queries for salary.
- jobTitle: Filters by the job title.
- gender: Filters by gender.

### Field
#### GET /api/v1/employees/fields
```
Returns only specified fields from employee data.
```
#### Parameters:
- fields: Comma-separated list of fields to include in the response.
#### Conditions:
- Please use createdTimestamp instead of timestamp.
- available field
  - "employer", "location", "jobTitle", "yearsAtEmployer",
    "yearsOfExperience", "salary", "salaryAmount", "signingBonus",
    "annualBonus", "annualStockValueBonus", "gender", "additionalComments",
    "createdTimestamp"

### Sorting
#### GET /api/v1/employees/sorted
```
Sorts employee data based on specified fields and direction.
```
#### Parameters:
- sort: Comma-separated list of fields to sort by.
- sortDirection: Sorting direction (ASC or DESC).
  ####Conditions:
- available field
  - "employer", "location", "jobTitle", "yearsAtEmployer",
    "yearsOfExperience", "salary", "salaryAmount", "signingBonus",
    "annualBonus", "annualStockValueBonus", "gender", "additionalComments",
    "createdTimestamp"

### Advanced Search
#### GET /api/v1/employees/search
```
Combines filtering, field selection, and sorting in a single endpoint.
```
#### Parameters:
- fields: Fields to include in the response.
- sort: Fields to sort by.
- sortDirection: Sorting direction.
- Additional parameters for conditions
  - salaryFrom and salaryTo: Allow range queries for salary.
  - jobTitle: Filters by the job title.
  - gender: Filters by gender.