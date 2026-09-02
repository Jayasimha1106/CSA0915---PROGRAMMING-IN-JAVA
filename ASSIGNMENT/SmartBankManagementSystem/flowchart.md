# Smart Bank Management System - System Flowcharts

## 1. Master Application Flowchart

```mermaid
flowchart TD
    Start([Start Application]) --> LoadData[Initialize In-Memory Collections & Sample Data]
    LoadData --> LoginScreen[Display AWT Login Screen]
    
    LoginScreen --> CheckCredentials{Validate Credentials<br/>admin / admin123}
    CheckCredentials -- Invalid --> LoginError[Show Error Dialog] --> LoginScreen
    CheckCredentials -- Valid --> Dashboard[Display Main Dashboard Frame]
    
    Dashboard --> SelectModule{Select Module from Sidebar}
    
    SelectModule --> ModuleCustomer[Customer Management]
    SelectModule --> ModuleAccount[Account Management]
    SelectModule --> ModuleDeposit[Deposit]
    SelectModule --> ModuleWithdraw[Withdrawal]
    SelectModule --> ModuleTransfer[Fund Transfer]
    SelectModule --> ModuleLoan[Loan Management]
    SelectModule --> ModuleEmployee[Employee Management]
    SelectModule --> ModuleHistory[Transaction History]
    SelectModule --> ModuleFile[File Storage I/O]
    SelectModule --> ModuleSerial[Serialization]
    SelectModule --> ModuleDB[Database / JDBC]
    SelectModule --> ModuleSim[Concurrency Simulator]
    SelectModule --> ExitApp[Logout / Exit]
    
    ModuleCustomer --> ValidateInput[Validate Form Input]
    ModuleAccount --> ValidateInput
    ModuleDeposit --> ValidateInput
    ModuleWithdraw --> ValidateInput
    ModuleTransfer --> ValidateInput
    ModuleLoan --> ValidateInput
    ModuleEmployee --> ValidateInput
    
    ValidateInput --> InputValid{Is Input Valid?}
    InputValid -- No --> ShowValidationError[Show Error Dialog] --> Dashboard
    InputValid -- Yes --> ExecOp[Execute Domain Operation]
    
    ExecOp --> UpdateMem[Update In-Memory Collections<br/>HashMap / ArrayList]
    UpdateMem --> PersistenceChoice{Persistence Mode}
    
    PersistenceChoice --> FlatFile[Write/Append to .txt Files]
    PersistenceChoice --> SerFile[Serialize to bank_data.ser]
    PersistenceChoice --> JdbcSync[PreparedStatement CRUD & ACID Commit]
    
    FlatFile --> ShowResult[Display Result Dialog]
    SerFile --> ShowResult
    JdbcSync --> ShowResult
    
    ShowResult --> Continue{Continue Banking?}
    Continue -- Yes --> Dashboard
    Continue -- No --> ExitApp([Shutdown Threads & Exit])
```

---

## 2. Fund Transfer Flowchart (Multithreading & Synchronization)

```mermaid
flowchart TD
    StartTx([Initiate Transfer]) --> CheckArgs{Sender != Receiver<br/>AND Amount > 0?}
    CheckArgs -- No --> ThrowInvalid[Throw InvalidTransactionException / InvalidAmountException]
    CheckArgs -- Yes --> LockOrdering[Compute Deterministic Lock Order<br/>firstLock = min accNum, secondLock = max accNum]
    
    LockOrdering --> AcquireFirst[Acquire Lock on firstLock Account]
    AcquireFirst --> AcquireSecond[Acquire Lock on secondLock Account]
    
    AcquireSecond --> CheckSenderBal{Check Sender Balance<br/>Sufficient Funds?}
    CheckSenderBal -- No --> ReleaseLocks[Release Locks & Throw InsufficientBalanceException]
    CheckSenderBal -- Yes --> DebitSender[Debit Sender Account]
    
    DebitSender --> CreditReceiver[Credit Receiver Account]
    CreditReceiver --> CreateTx1[Record TRANSFER_OUT Transaction]
    CreateTx1 --> CreateTx2[Record TRANSFER_IN Transaction]
    CreateTx2 --> ReleaseBoth[Release Both Locks]
    ReleaseBoth --> EnqueueAudit[Enqueue Audit Log into TransactionManager]
    EnqueueAudit --> DoneTx([Transfer Complete Successfully])
```

---

## 3. JDBC Transactional Transfer Flowchart (ACID)

```mermaid
flowchart TD
    StartJDBC([Start JDBC Transfer]) --> DisableAutoCommit[conn.setAutoCommit false]
    DisableAutoCommit --> QuerySender[SELECT balance FROM accounts WHERE acc = sender FOR UPDATE]
    QuerySender --> SenderFound{Sender Exists & Balance >= Amount?}
    SenderFound -- No --> Rollback1[conn.rollback] --> ThrowEx[Throw Exception & Close Conn]
    
    SenderFound -- Yes --> DebitSQL[UPDATE accounts SET balance = balance - amt WHERE acc = sender]
    DebitSQL --> CreditSQL[UPDATE accounts SET balance = balance + amt WHERE acc = receiver]
    CreditSQL --> InsertTx1[INSERT INTO transactions SENDER_OUT]
    InsertTx1 --> InsertTx2[INSERT INTO transactions RECEIVER_IN]
    
    InsertTx2 --> AllGood{All Queries Succeeded?}
    AllGood -- Yes --> CommitTx[conn.commit] --> SetAutoTrue[conn.setAutoCommit true] --> Success([Transaction Committed])
    AllGood -- No --> Rollback2[conn.rollback] --> SetAutoTrue --> Failure([Transaction Rolled Back])
```
