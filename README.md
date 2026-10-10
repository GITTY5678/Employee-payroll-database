# Employee Payroll

A small Java console application for managing employees, recording salary
payments, and printing monthly salary reports. It stores its data in a local
SQLite database.

## Requirements

- Java JDK (the project was tested with JDK 21)
- SQLite JDBC driver at `lib/sqlite-jdbc-3.53.4.0.jar`

No MySQL server, account, or credentials are needed.

## Build and run

Run these commands in PowerShell from the project folder:

```powershell
cd C:\java_ps
javac -cp ".\lib\*" -d .\out .\src\*.java
java -cp ".\out;.\lib\*" Main
```

The program creates `employee_payroll.db` in the current working directory
when it starts. It creates missing tables without deleting existing records.

## Menu

1. **Add employee** — enter the employee's name, department, job title, and
   hire date (`YYYY-MM-DD`).
2. **Record salary payment** — choose an active employee and enter the salary
   period, payment date, basic salary, allowances, and deductions.
3. **Generate monthly salary report** — choose an employee and month
   (`YYYY-MM`) to display their salary records and totals.
0. **Exit**

Net pay is calculated as:

```text
basic salary + allowances - deductions
```

## Add presentation data

Run the sample-data program before starting the main application:

```powershell
java -cp ".\out;.\lib\*" SampleData
java -cp ".\out;.\lib\*" Main
```

It adds three demo employees and monthly salary records for the current and
previous month. Existing matching demo employees and records are reused, so
the program can be run again without duplicating them. Demo employee names
are marked with `(Demo)`.

## Project files

```text
src/
  Main.java                 Console menu and user input
  Employee.java             Employee data
  EmployeeDao.java          Employee database operations
  SalaryRecord.java         Salary data and net-pay calculation
  SalaryRecordDao.java      Salary database operations
  PayrollService.java       Input validation and payroll operations
  SalaryReport.java         Monthly report formatting
  DatabaseConnection.java   SQLite connection and table initialization
  SampleData.java           Optional presentation-data setup
lib/
  sqlite-jdbc-3.53.4.0.jar  SQLite JDBC driver
schema.sql                  SQLite table definitions
employee_payroll.db         Local database, created when the app starts
out/                        Compiled Java classes
```

## Database notes

The app creates the `employees` and `salary_records` tables automatically.
Foreign-key enforcement is enabled for each database connection. The
standalone `schema.sql` contains the equivalent SQLite table definitions.

The current console menu supports adding employees, recording salary payments,
and viewing monthly reports. Employee update and delete options are not
implemented.
