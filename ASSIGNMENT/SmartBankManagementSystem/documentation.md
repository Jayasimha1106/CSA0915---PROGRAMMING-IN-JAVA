# Smart Bank Management System - Technical Architecture & Documentation

## 1. System Architecture
The Smart Bank Management System follows a multi-tiered architecture:

```
+-------------------------------------------------------------------------+
|                        PRESENTATION LAYER (GUI)                         |
|  Java AWT Components (Frame, Panel, Label, TextField, Button, Dialog)   |
|  Delegation Event Model (ActionListener, ItemListener, WindowAdapter)   |
+-------------------------------------------------------------------------+
                                    |
+-------------------------------------------------------------------------+
|                         APPLICATION DOMAIN LAYER                        |
|  Bank.java (Collection Engine, Business Validation & In-Memory State)   |
|  TransactionManager.java (Thread Coordination & Producer-Consumer Queue)|
+-------------------------------------------------------------------------+
                                    |
+-------------------------------------------------------------------------+
|                            OBJECT DOMAIN LAYER                          |
|  BankOperations (Interface)                                             |
|  Account.java (Abstract Base Class - Serializable)                      |
|    ├── SavingsAccount.java (Inheritance, Overriding, Polymorphism)      |
|    └── CheckingAccount.java (Inheritance, Overriding, Polymorphism)     |
|  Customer.java, Transaction.java, Loan.java, Employee.java              |
|  Custom Exception Hierarchy (BankException and Subclasses)              |
+-------------------------------------------------------------------------+
                                    |
+-------------------------------------------------------------------------+
|                       CONCURRENCY & WORKER LAYER                        |
|  DepositThread (Priority 5) | WithdrawalThread (Priority 5)             |
|  TransferThread (Priority 10) | AuditLogger Daemon (Priority 1)         |
+-------------------------------------------------------------------------+
                                    |
+-------------------------------------------------------------------------+
|                            PERSISTENCE LAYER                            |
|  1. FileManager.java (Java I/O Streams - Character Stream Flat Files)   |
|  2. SerializationManager.java (ObjectOutputStream/InputStream Binary)   |
|  3. DatabaseManager.java (JDBC 4.0 Driver, PreparedStatement, ACID Tx)  |
+-------------------------------------------------------------------------+
```

---

## 2. Core Class Specifications

### 2.1 Interface: `BankOperations`
- `void deposit(double amount) throws BankException`
- `void deposit(double amount, String description) throws BankException`
- `void withdraw(double amount) throws BankException`
- `void transfer(Account targetAccount, double amount) throws BankException`
- `double checkBalance()`

### 2.2 Abstract Class: `Account`
- Fields: `protected String accountNumber`, `protected String customerId`, `protected String accountType`, `protected double balance`.
- Synchronization:
  - `public synchronized void deposit(...)`
  - `public synchronized void withdraw(...)`
  - `public void transfer(Account targetAccount, double amount)` (implements deadlock-free locking ordering).
- Abstract methods:
  - `public abstract double calculateInterest();`
  - `public abstract String displayAccountDetails();`

### 2.3 Subclass: `SavingsAccount`
- Inherits: `Account`
- Fields: `double interestRate`, `double minimumBalance`
- Overrides:
  - `calculateInterest()`: returns `(balance * interestRate / 100.0)`.
  - `withdraw(double amount)`: ensures `(balance - amount) >= minimumBalance`.
  - `displayAccountDetails()`: returns formatted savings summary.

### 2.4 Subclass: `CheckingAccount`
- Inherits: `Account`
- Fields: `double overdraftLimit`, `double transactionFee`
- Overrides:
  - `calculateInterest()`: returns `0.0`.
  - `withdraw(double amount)`: permits overdraft up to `overdraftLimit` with penalty fee.
  - `displayAccountDetails()`: returns formatted checking summary.

### 2.5 Models: `Customer`, `Transaction`, `Loan`, `Employee`
- Implements: `Serializable`
- Full encapsulation with private fields, default and parameterized constructors, getters, setters, and `toString()` overrides.

---

## 3. Concurrency & Synchronization Model

### 3.1 Deadlock Prevention Strategy
When transferring funds between two accounts, locking order is established deterministically based on account number:
```java
Account firstLock = this.accountNumber.compareTo(targetAccount.getAccountNumber()) < 0 ? this : targetAccount;
Account secondLock = this.accountNumber.compareTo(targetAccount.getAccountNumber()) < 0 ? targetAccount : this;

synchronized (firstLock) {
    synchronized (secondLock) {
        this.withdraw(amount);
        targetAccount.deposit(amount, "Transfer");
    }
}
```
This guarantees that two simultaneous opposing transfers (e.g. A->B and B->A) will always acquire locks in the exact same order (A then B), completely eliminating circular wait deadlocks.

### 3.2 Inter-Thread Communication (`wait()` / `notifyAll()`)
`TransactionManager` maintains a bounded queue (`auditLogQueue`) of capacity 20:
- When queue is full: producer threads call `auditLogQueue.wait()`.
- When an item is pushed: producer calls `auditLogQueue.notifyAll()`.
- Background consumer thread polls queue and notifies waiting producers with `auditLogQueue.notifyAll()`.

---

## 4. Exception Handling Hierarchy

```
java.lang.Throwable
  └── java.lang.Exception
        └── BankException (Base Checked Exception)
              ├── InvalidAccountException (Account not found / duplicate)
              ├── InsufficientBalanceException (Low balance / overdraft exceeded)
              ├── InvalidTransactionException (Same account transfer / invalid state)
              └── InvalidAmountException (Negative or zero amounts)
```

---

## 5. Persistence Subsystems

1. **Flat File Storage**:
   - Format: Pipe-delimited records (`|`)
   - Files: `customers.txt`, `accounts.txt`, `transactions.txt`, `loans.txt`, `employees.txt`
   - Class: `FileManager` with `BufferedReader` / `BufferedWriter`.

2. **Binary Object Serialization**:
   - File: `bank_data.ser`
   - Class: `SerializationManager` with `ObjectOutputStream` / `ObjectInputStream`.
   - Saves and deserializes the entire `Bank` object graph.

3. **Relational Database (JDBC MySQL)**:
   - Database: `smart_bank`
   - Class: `DatabaseManager`
   - Uses `PreparedStatement` to prevent SQL Injection.
   - ACID transactions with `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()`.
