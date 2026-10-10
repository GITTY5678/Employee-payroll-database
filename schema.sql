
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS employees (
    employee_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    department TEXT NOT NULL,
    job_title TEXT NOT NULL,
    hire_date TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'Active'
);

CREATE TABLE IF NOT EXISTS salary_records (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER NOT NULL,
    period_start TEXT NOT NULL,
    period_end TEXT NOT NULL,
    payment_date TEXT NOT NULL,
    basic_salary NUMERIC NOT NULL CHECK (basic_salary >= 0),
    allowances NUMERIC NOT NULL DEFAULT 0.00 CHECK (allowances >= 0),
    deductions NUMERIC NOT NULL DEFAULT 0.00 CHECK (deductions >= 0),
    net_pay NUMERIC NOT NULL,
    CHECK (period_end >= period_start),
    CHECK (deductions <= basic_salary + allowances),
    FOREIGN KEY (employee_id) REFERENCES employees (employee_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_salary_employee_payment_date
    ON salary_records (employee_id, payment_date);