# Smart Bank Management System

> **CSA09 Programming in Java — Enterprise Banking Application**  
> A complete, fully functional, multi-tiered Java desktop application built using standard Java AWT, Object-Oriented Programming (OOP) paradigms, Java Collections Framework, Multithreading & Synchronization, Java I/O Streams, Object Serialization, and JDBC MySQL persistence.

---

## Table of Contents
1. [Project Overview & Objectives](#project-overview--objectives)
2. [Key Features & Modules](#key-features--modules)
3. [Technology Stack & Requirements](#technology-stack--requirements)
4. [Project Directory Structure](#project-directory-structure)
5. [Architecture & Class Design](#architecture--class-design)
6. [OOP Concepts Demonstrated](#oop-concepts-demonstrated)
7. [Java Collections Framework](#java-collections-framework)
8. [Multithreading & Synchronization Architecture](#multithreading--synchronization-architecture)
9. [File I/O & Object Serialization](#file-io--object-serialization)
10. [Database Design & JDBC Transactions](#database-design--jdbc-transactions)
11. [How to Setup MySQL Database](#how-to-setup-mysql-database)
12. [How to Add MySQL JDBC Driver](#how-to-add-mysql-jdbc-driver)
13. [How to Compile and Run](#how-to-compile-and-run)
14. [Demo Login Credentials](#demo-login-credentials)
15. [User Interface Guide](#user-interface-guide)
16. [Test Cases Summary](#test-cases-summary)

---

## Project Overview & Objectives
The **Smart Bank Management System** is an enterprise-grade desktop banking application developed to strictly satisfy all core requirements of the CSA09 Programming in Java curriculum. It simulates real-world core banking operations including account lifecycle management, high-volume concurrent transactions, risk-controlled lending, payroll administration, audit logging, and dual-layer data persistence (flat files, serialized object graphs, and relational MySQL storage).

---

## Key Features & Modules

- **User Authentication**: Secure AWT login screen with credential validation and password masking (`*`).
- **Customer Management**: Add, search, update, delete, and list bank customers with duplicate ID checks.
- **Account Management**: Create interest-bearing `SavingsAccount` and overdraft-enabled `CheckingAccount` instances demonstrating polymorphism.
- **Deposit & Withdrawal**: Validated banking operations with immediate balance calculation and custom exception handling.
- **Fund Transfer**: Deadlock-free, two-account atomic fund transfers with race condition prevention.
- **Loan Management & EMI Calculator**: Loan portfolio management, status approvals/rejections, and automated Equated Monthly Installment (EMI) calculations.
- **Employee Management**: Bank staff record management with payroll information.
- **Transaction History**: Comprehensive audit ledger filtered by account number or globally.
- **File Storage (I/O Streams)**: Import, export, append, and search flat text records (`customers.txt`, `accounts.txt`, `transactions.txt`, etc.).
- **Serialization**: Deep object graph serialization and restoration to/from `data/bank_data.ser`.
- **JDBC MySQL Integration**: Full CRUD via `PreparedStatement` and atomic multi-step database transactions with rollback support.
- **Concurrent Transactions Simulator**: Live visual stress-testing tool running 6 concurrent worker threads across 2 accounts.

---

## Technology Stack & Requirements

- **Language**: Java SE (JDK 8 / 11 / 17 / 21+)
- **GUI Framework**: Java AWT (Abstract Window Toolkit) — No third-party frameworks
- **Database**: MySQL 8.0+ / MariaDB 10.4+
- **Driver**: MySQL Connector/J (`mysql-connector-j-8.x.x.jar`)
- **Operating System**: Platform independent (Windows, macOS, Linux)
- **Memory**: Minimum 512 MB RAM

---

## Project Directory Structure

```
SmartBankManagementSystem/
├── src/
│   ├── SmartBankManagementSystem.java   # Main AWT GUI Application & Module Controller
│   ├── Bank.java                        # Core Domain Controller & Collections Engine
│   ├── BankOperations.java              # Core Banking Contract Interface
│   ├── Account.java                     # Abstract Base Account Class (implements BankOperations, Serializable)
│   ├── SavingsAccount.java              # Savings Account Subclass (Interest calculation & min balance)
│   ├── CheckingAccount.java             # Checking Account Subclass (Overdraft limit & transaction fees)
│   ├── Customer.java                    # Customer Model (Serializable)
│   ├── Transaction.java                 # Transaction Ledger Model (Serializable)
│   ├── Loan.java                        # Loan Facility Model with EMI calculation (Serializable)
│   ├── Employee.java                    # Employee & Payroll Model (Serializable)
│   ├── DepositThread.java               # Dedicated Normal-Priority Worker Thread for Deposits
│   ├── WithdrawalThread.java            # Dedicated Normal-Priority Worker Thread for Withdrawals
│   ├── TransferThread.java              # Dedicated High-Priority Worker Thread for Atomic Transfers
│   ├── TransactionManager.java          # Concurrency Coordinator (wait/notify & stress test engine)
│   ├── FileManager.java                 # Java I/O Streams Manager (BufferedReader/Writer)
│   ├── SerializationManager.java        # Object Serialization Manager (ObjectOutputStream/InputStream)
│   ├── DatabaseManager.java             # JDBC MySQL Manager with CRUD & Transaction Rollback
│   ├── BankException.java               # Base Custom Banking Exception
│   ├── InvalidAccountException.java     # Exception for missing or duplicate accounts
│   ├── InsufficientBalanceException.java# Exception for overdraft/minimum balance violations
│   ├── InvalidTransactionException.java # Exception for illegal transaction operations
│   ├── InvalidAmountException.java      # Exception for negative or zero amounts
│   └── TestVerificationRunner.java      # Automated Headless Verification Test Suite
├── data/
│   ├── customers.txt                    # Flat file customer storage
│   ├── accounts.txt                     # Flat file account storage
│   ├── transactions.txt                 # Flat file transaction ledger
│   ├── loans.txt                        # Flat file loan portfolio
│   ├── employees.txt                    # Flat file staff directory
│   └── bank_data.ser                    # Binary serialized bank state
├── database/
│   └── database.sql                     # MySQL DDL & DML initialization script
├── bin/                                 # Compiled Java bytecode (.class files)
├── README.md                            # Comprehensive user manual and project overview
├── documentation.md                     # Deep technical and architectural documentation
├── pseudocode.txt                       # Structured algorithmic pseudocode
├── flowchart.md                         # Mermaid visual system flowcharts
└── test_cases.md                        # Complete test suite and verification results
```

---

## OOP Concepts Demonstrated

1. **Encapsulation**: Private fields across all model classes (`Customer`, `Account`, `Loan`, `Employee`, `Transaction`) accessed through standard getters and setters.
2. **Inheritance**: Abstract base class `Account` extended by `SavingsAccount` and `CheckingAccount`.
3. **Abstraction**: Abstract methods `calculateInterest()` and `displayAccountDetails()` defined in `Account` and implemented by subclasses.
4. **Interfaces**: `BankOperations` interface defining strict banking contracts implemented by `Account`.
5. **Method Overloading**: Overloaded methods in `Account` and `BankOperations`:
   - `deposit(double amount)`
   - `deposit(double amount, String description)`
6. **Method Overriding**: `SavingsAccount` and `CheckingAccount` override `calculateInterest()`, `displayAccountDetails()`, and `withdraw()`.
7. **Runtime Polymorphism**: Polymorphic account invocation:
   ```java
   Account account = new SavingsAccount("A1001", "C001", 10000.0, 4.5, 500.0);
   double interest = account.calculateInterest(); // Dynamically binds to SavingsAccount implementation
   ```

---

## Java Collections Framework

- `HashMap<String, Account>`: Constant-time $O(1)$ account lookup by account number.
- `HashMap<String, Customer>`: Customer index indexed by unique Customer ID.
- `ArrayList<Transaction>`: Ordered transaction ledger storing chronological audit records.
- `HashMap<String, Loan>`: Loan registry indexed by Loan ID.
- `HashMap<String, Employee>`: Employee directory indexed by Employee ID.
- **Generics**: Type safety enforced across all collections.
- **Iterators**: Explicit iterator traversals:
   ```java
   Iterator<Transaction> iterator = transactions.iterator();
   while (iterator.hasNext()) {
       Transaction tx = iterator.next();
       // Process audit entry
   }
   ```

---

## Multithreading & Synchronization Architecture

- **Dedicated Worker Threads**:
  - `DepositThread` (Priority `NORM_PRIORITY` = 5)
  - `WithdrawalThread` (Priority `NORM_PRIORITY` = 5)
  - `TransferThread` (Priority `MAX_PRIORITY` = 10)
- **Synchronized Methods & Blocks**:
  - `synchronized void deposit(...)`
  - `synchronized void withdraw(...)`
- **Deadlock-Free Two-Account Synchronization**:
  Transfers lock accounts in a strict, deterministic order based on lexicographical account number comparison:
  ```java
  Account firstLock = this.accountNumber.compareTo(targetAccount.getAccountNumber()) < 0 ? this : targetAccount;
  Account secondLock = this.accountNumber.compareTo(targetAccount.getAccountNumber()) < 0 ? targetAccount : this;
  synchronized (firstLock) {
      synchronized (secondLock) {
          this.withdraw(amount);
          targetAccount.deposit(amount, "Transfer from " + this.accountNumber);
      }
  }
  ```
- **Inter-Thread Communication**:
  Producer-consumer audit queue in `TransactionManager` utilizing `wait()`, `notify()`, and `notifyAll()` with background daemon logging thread at `MIN_PRIORITY`.

---

## File I/O & Object Serialization

- **Text Stream Persistence (`FileManager.java`)**:
  - Uses `BufferedReader`, `BufferedWriter`, `FileReader`, and `FileWriter` with try-with-resources.
  - Pipe-delimited flat file format for easy parsing and text-based keyword search.
- **Deep Object Serialization (`SerializationManager.java`)**:
  - Uses `ObjectOutputStream` and `ObjectInputStream`.
  - Serializes and restores the entire `Bank` instance to/from `data/bank_data.ser`.

---

## Database Design & JDBC Transactions

### Relational Schema (`smart_bank`)
- `customers` (`customer_id` PK, `name`, `address`, `phone`, `email`)
- `accounts` (`account_number` PK, `customer_id` FK, `account_type`, `balance`)
- `transactions` (`transaction_id` PK, `account_number` FK, `transaction_type`, `amount`, `transaction_date`, `description`)
- `loans` (`loan_id` PK, `customer_id` FK, `loan_type`, `amount`, `interest_rate`, `duration`, `status`)
- `employees` (`employee_id` PK, `name`, `department`, `position`, `salary`)

### ACID Transactional Fund Transfer
```java
conn.setAutoCommit(false);
try {
    // 1. Check sender balance
    // 2. Deduct from sender
    // 3. Add to receiver
    // 4. Insert transaction records
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
}
```

---

## How to Setup MySQL Database

1. Start your local MySQL service (e.g. through MySQL Workbench, XAMPP, or command line).
2. Open terminal/PowerShell and run:
   ```bash
   mysql -u root -p < database/database.sql
   ```
3. Verify that database `smart_bank` and all 5 tables are populated with initial seed records.

---

## How to Add MySQL JDBC Driver

If running with MySQL database support, download `mysql-connector-j-8.x.x.jar` and place it in a `lib/` directory or include in your classpath:
```bash
# Compilation with JDBC driver:
javac -d bin -cp "src;lib/*" src/*.java

# Execution with JDBC driver:
java -cp "bin;lib/*" SmartBankManagementSystem
```

*(Note: The system operates completely and seamlessly in standalone in-memory + File I/O + Serialization modes even if MySQL is offline).*

---

## How to Compile and Run

### 1. Compile all Java source files
```powershell
javac -d bin src/*.java
```

### 2. Run Automated Test Verification Suite (Headless)
```powershell
java -cp bin TestVerificationRunner
```

### 3. Launch Desktop GUI Application
```powershell
java -cp bin SmartBankManagementSystem
```

---

## Demo Login Credentials

- **Username**: `admin`
- **Password**: `admin123`

---

## User Interface Guide

1. **Login Screen**: Enter username and password. Click "Show Password" checkbox to inspect entry.
2. **Dashboard**: Navigate across 12 distinct banking modules using the left navigation sidebar.
3. **Customer Module**: Add new customer profiles or search existing ones to auto-fill details.
4. **Account Module**: Choose Savings or Checking account type, configure interest rates/overdrafts, and click "Demonstrate Polymorphism".
5. **Deposit / Withdrawal**: Perform real-time balance updates with validation dialogs.
6. **Fund Transfer**: Execute synchronized transfers between any two accounts.
7. **Loan Management**: Apply for loans, click "Calculate EMI", and approve/reject applications.
8. **File Storage & Serialization**: Export/import text records or save/restore complete binary snapshots.
9. **Concurrency Simulator**: Enter two account numbers and click "RUN CONCURRENCY STRESS TEST" to observe real-time multi-threaded race-free transaction execution.

---

## Test Cases Summary

All 9 automated test suites pass with 100% success rate:
- `[TEST 1]` OOP Inheritance & Runtime Polymorphism: **PASSED**
- `[TEST 2]` Method Overloading (deposit): **PASSED**
- `[TEST 3]` InsufficientBalanceException: **PASSED**
- `[TEST 4]` InvalidAmountException: **PASSED**
- `[TEST 5]` Java Collections & Iterator Traversal: **PASSED**
- `[TEST 6]` Multithreaded Synchronization & Deadlock-Free Transfers: **PASSED**
- `[TEST 7]` File I/O Streams (Read/Write/Search): **PASSED**
- `[TEST 8]` Object Serialization & Deserialization: **PASSED**
- `[TEST 9]` Live Concurrency Stress Test Simulator (6 threads): **PASSED**

---
*Created for CSA09 Programming in Java — Smart Bank Management System.*
