-- =====================================================================
-- SMART BANK MANAGEMENT SYSTEM - DATABASE CREATION & SEED SCRIPT
-- Database Engine: MySQL 8.0+ / MariaDB 10.4+
-- =====================================================================

CREATE DATABASE IF NOT EXISTS smart_bank;
USE smart_bank;

-- 1. Customers Table
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Accounts Table
CREATE TABLE accounts (
    account_number VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- 'Savings' or 'Checking'
    balance DOUBLE NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Transactions Table
CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT'
    amount DOUBLE NOT NULL,
    transaction_date VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT fk_account FOREIGN KEY (account_number) 
        REFERENCES accounts(account_number) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Loans Table
CREATE TABLE loans (
    loan_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    loan_type VARCHAR(50) NOT NULL, -- 'Home', 'Personal', 'Education', 'Business'
    amount DOUBLE NOT NULL,
    interest_rate DOUBLE NOT NULL,
    duration INT NOT NULL, -- in months
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED', 'CLOSED'
    CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Employees Table
CREATE TABLE employees (
    employee_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    salary DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- SAMPLE TEST DATA INSERTIONS
-- =====================================================================

-- Customers
INSERT INTO customers (customer_id, name, address, phone, email) VALUES
('C001', 'Arun Kumar', '124 MG Road, Bangalore', '9876543210', 'arun@example.com'),
('C002', 'Priya Sharma', '45 Park Street, Kolkata', '9876543211', 'priya@example.com'),
('C003', 'Rahul Verma', '78 Nehru Nagar, Delhi', '9876543212', 'rahul@example.com'),
('C004', 'Sneha Patel', '12 SV Road, Mumbai', '9876543213', 'sneha@example.com');

-- Accounts
INSERT INTO accounts (account_number, customer_id, account_type, balance) VALUES
('A1001', 'C001', 'Savings', 10000.00),
('A1002', 'C001', 'Checking', 5000.00),
('A1003', 'C002', 'Savings', 25000.00),
('A1004', 'C003', 'Checking', 15000.00),
('A1005', 'C004', 'Savings', 8000.00);

-- Transactions
INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, transaction_date, description) VALUES
('TXN-SEED01', 'A1001', 'DEPOSIT', 10000.00, '2026-09-01 10:00:00', 'Initial opening balance deposit'),
('TXN-SEED02', 'A1002', 'DEPOSIT', 5000.00, '2026-09-01 10:30:00', 'Initial opening balance deposit'),
('TXN-SEED03', 'A1003', 'DEPOSIT', 25000.00, '2026-09-01 11:00:00', 'Initial opening balance deposit'),
('TXN-SEED04', 'A1004', 'DEPOSIT', 15000.00, '2026-09-01 11:15:00', 'Initial opening balance deposit'),
('TXN-SEED05', 'A1005', 'DEPOSIT', 8000.00, '2026-09-01 11:45:00', 'Initial opening balance deposit');

-- Loans
INSERT INTO loans (loan_id, customer_id, loan_type, amount, interest_rate, duration, status) VALUES
('L501', 'C001', 'Home', 500000.00, 8.25, 60, 'APPROVED'),
('L502', 'C002', 'Personal', 50000.00, 10.50, 24, 'PENDING'),
('L503', 'C003', 'Education', 200000.00, 7.50, 36, 'APPROVED'),
('L504', 'C004', 'Business', 750000.00, 9.00, 48, 'PENDING');

-- Employees
INSERT INTO employees (employee_id, name, department, position, salary) VALUES
('E101', 'Vikram Rathore', 'Management', 'Branch Manager', 85000.00),
('E102', 'Ananya Deshmukh', 'Operations', 'Chief Cashier', 48000.00),
('E103', 'Karthik Raja', 'Customer Support', 'Relationship Officer', 42000.00),
('E104', 'Meera Nair', 'Loans & Credit', 'Credit Analyst', 52000.00);
