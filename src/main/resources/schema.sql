CREATE SCHEMA IF NOT EXISTS joe;
SET search_path TO joe;

DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS salary;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS position;
DROP TABLE IF EXISTS department;

CREATE TABLE department (
    dept_id SERIAL PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL UNIQUE,
    dept_code VARCHAR(20) NOT NULL UNIQUE,
    manager_name VARCHAR(50),
    phone VARCHAR(20),
    location VARCHAR(50)
);

CREATE TABLE position (
    position_id SERIAL PRIMARY KEY,
    position_name VARCHAR(50) NOT NULL UNIQUE,
    position_code VARCHAR(20) NOT NULL UNIQUE,
    level VARCHAR(20),
    base_salary NUMERIC(10, 2) NOT NULL
        CHECK (base_salary >= 0),
    description VARCHAR(100)
);

CREATE TABLE employee (
    emp_id SERIAL PRIMARY KEY,
    emp_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10)
        CHECK (gender IN ('male', 'female')),
    age INT
        CHECK (age >= 18 AND age <= 65),
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    hire_date DATE NOT NULL,
    dept_id INT NOT NULL,
    position_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'active'
        CHECK (status IN ('active', 'resigned', 'probation')),
    leave_date DATE,
    CONSTRAINT fk_employee_dept
        FOREIGN KEY (dept_id)
        REFERENCES department(dept_id),
    CONSTRAINT fk_employee_position
        FOREIGN KEY (position_id)
        REFERENCES position(position_id)
);

CREATE TABLE attendance (
    attendance_id SERIAL PRIMARY KEY,
    emp_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    attendance_status VARCHAR(20) NOT NULL
        CHECK (attendance_status IN ('present', 'late', 'leave', 'absent')),
    late_minutes INT DEFAULT 0
        CHECK (late_minutes >= 0),
    overtime_hours NUMERIC(5, 2) DEFAULT 0
        CHECK (overtime_hours >= 0),
    remark VARCHAR(200),
    CONSTRAINT uk_emp_attendance_date
        UNIQUE (emp_id, attendance_date),
    CONSTRAINT fk_attendance_emp
        FOREIGN KEY (emp_id)
        REFERENCES employee(emp_id)
);

CREATE TABLE salary (
    salary_id SERIAL PRIMARY KEY,
    emp_id INT NOT NULL,
    salary_month VARCHAR(7) NOT NULL,
    base_salary NUMERIC(10, 2) NOT NULL
        CHECK (base_salary >= 0),
    bonus NUMERIC(10, 2) DEFAULT 0
        CHECK (bonus >= 0),
    deduction NUMERIC(10, 2) DEFAULT 0
        CHECK (deduction >= 0),
    net_salary NUMERIC(10, 2) NOT NULL
        CHECK (net_salary >= 0),
    CONSTRAINT uk_emp_salarymonth
        UNIQUE (emp_id, salary_month),
    CONSTRAINT fk_salary_emp
        FOREIGN KEY (emp_id)
        REFERENCES employee(emp_id)
);
