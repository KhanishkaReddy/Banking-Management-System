-- ==========================================
-- FULL RESET : BANKING MANAGEMENT SYSTEM
-- ==========================================

DROP DATABASE IF EXISTS banking_management;
CREATE DATABASE banking_management;
USE banking_management;

-- ==========================================
-- CUSTOMER TABLE
-- ==========================================

CREATE TABLE customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(15),
    email VARCHAR(100),
    date_of_birth DATE,
    pan VARCHAR(20)
);

-- ==========================================
-- BRANCH TABLE
-- ==========================================

CREATE TABLE branch (
    branch_id INT PRIMARY KEY,
    branch_name VARCHAR(100),
    location VARCHAR(100)
);

-- ==========================================
-- ACCOUNT TABLE
-- ==========================================

CREATE TABLE account (
    account_no INT PRIMARY KEY,
    account_type VARCHAR(50),
    balance DECIMAL(12,2),
    opened_date DATE,
    status VARCHAR(20),
    customer_id INT,
    branch_id INT,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- ==========================================
-- EMPLOYEE TABLE
-- ==========================================

CREATE TABLE employee (
    employee_id INT PRIMARY KEY,
    name VARCHAR(100),
    role VARCHAR(50),
    branch_id INT,
    FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- ==========================================
-- TRANSACTION TABLE
-- ==========================================

CREATE TABLE transaction_details (
    transaction_id INT PRIMARY KEY,
    amount DECIMAL(12,2),
    transaction_type VARCHAR(50),
    transaction_date DATETIME,
    description VARCHAR(255),
    account_no INT,
    FOREIGN KEY (account_no) REFERENCES account(account_no)
);

-- ==========================================
-- LOAN TABLE
-- ==========================================

CREATE TABLE loan (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    loan_amount DECIMAL(12,2),
    interest_rate DECIMAL(5,2),
    tenure_months INT,
    emi DECIMAL(12,2),
    remaining_amount DECIMAL(12,2),
    status VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- ==========================================
-- LOAN PAYMENT TABLE
-- ==========================================

CREATE TABLE loan_payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    loan_id INT,
    payment_amount DECIMAL(12,2),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
);

-- ==========================================
-- INSERT SAMPLE DATA
-- ==========================================

-- Branch Data
INSERT INTO branch VALUES
(1, 'Main Branch', 'Hyderabad'),
(2, 'City Branch', 'Bangalore');

-- Customer Data
INSERT INTO customer (name, address, phone, email, date_of_birth, pan) VALUES
('Ravi Kumar', 'Hyderabad', '9876543210', 'ravi@gmail.com', '2002-05-14', 'ABCDE1234F'),
('Anita Sharma', 'Bangalore', '9123456789', 'anita@gmail.com', '2001-09-20', 'PQRSX5678K');

-- Account Data
INSERT INTO account VALUES
(5001, 'Savings', 25000.00, '2024-01-10', 'Active', 1, 1),
(5002, 'Savings', 40000.00, '2024-02-15', 'Active', 2, 2);

-- Employee Data
INSERT INTO employee VALUES
(701, 'Suresh', 'Manager', 1),
(702, 'Meena', 'Clerk', 2);

-- Transaction Data
INSERT INTO transaction_details VALUES
(9001, 2000.00, 'Deposit', '2025-01-05 10:30:00', 'Cash Deposit', 5001),
(9002, 1500.00, 'Withdrawal', '2025-01-06 14:00:00', 'ATM Withdrawal', 5001);