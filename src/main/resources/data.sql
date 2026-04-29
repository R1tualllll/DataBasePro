SET search_path TO joe;

INSERT INTO department (dept_id, dept_name, dept_code, manager_name, phone, location)
VALUES
    (1, 'Human Resources', 'D001', 'Wang Li', '010-1001001', 'Building A-101'),
    (2, 'Technology', 'D002', 'Zhang Qiang', '010-1001002', 'Building R-302');

INSERT INTO position (position_id, position_name, position_code, level, base_salary, description)
VALUES
    (1, 'HR Specialist', 'P001', 'Junior', 5000.00, 'Recruitment and employee records'),
    (2, 'Java Developer', 'P002', 'Intermediate', 9000.00, 'Backend development and maintenance');

INSERT INTO employee (
    emp_id,
    emp_name,
    gender,
    age,
    phone,
    email,
    hire_date,
    dept_id,
    position_id,
    status,
    leave_date
)
VALUES
    (1, 'Li Hua', 'male', 24, '13800000001', 'lihua@test.com', DATE '2026-03-01', 1, 1, 'active', NULL),
    (2, 'Han Meimei', 'female', 26, '13800000002', 'hanmeimei@test.com', DATE '2026-03-15', 2, 2, 'active', NULL);

INSERT INTO attendance (
    attendance_id,
    emp_id,
    attendance_date,
    attendance_status,
    late_minutes,
    overtime_hours,
    remark
)
VALUES
    (1, 1, DATE '2026-04-20', 'present', 0, 1.50, 'Normal attendance with overtime'),
    (2, 1, DATE '2026-04-21', 'late', 15, 0.00, 'Traffic delay'),
    (3, 2, DATE '2026-04-20', 'present', 0, 2.00, 'Project overtime'),
    (4, 2, DATE '2026-04-21', 'leave', 0, 0.00, 'Personal leave');

INSERT INTO salary (
    salary_id,
    emp_id,
    salary_month,
    base_salary,
    bonus,
    deduction,
    net_salary
)
VALUES
    (1, 1, '2026-04', 5000.00, 300.00, 100.00, 5200.00),
    (2, 2, '2026-04', 9000.00, 800.00, 200.00, 9600.00);

SELECT pg_catalog.setval('joe.department_dept_id_seq', 2, true);
SELECT pg_catalog.setval('joe.position_position_id_seq', 2, true);
SELECT pg_catalog.setval('joe.employee_emp_id_seq', 2, true);
SELECT pg_catalog.setval('joe.attendance_attendance_id_seq', 4, true);
SELECT pg_catalog.setval('joe.salary_salary_id_seq', 2, true);
