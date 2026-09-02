import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles JDBC persistence with MySQL.
 * Demonstrates:
 * 1. JDBC Connectivity (DriverManager, Connection, PreparedStatement, ResultSet)
 * 2. Complete JDBC CRUD operations (INSERT, SELECT, UPDATE, DELETE)
 * 3. Database Transactions: setAutoCommit(false), commit(), rollback() for atomic transfers
 * 4. Exception Handling with SQLException
 */
public class DatabaseManager {
    private String url;
    private String username;
    private String password;

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database);
        this.username = username;
        this.password = password;
    }

    public DatabaseManager() {
        this("localhost", 3306, "smart_bank", "root", "root");
    }

    public void setCredentials(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        try {
            // Attempt to load MySQL Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
                // Driver may be preloaded by JDBC 4.0 Service Provider Mechanism
            }
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Tests database connectivity.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Initializes database tables if they do not exist.
     */
    public void initDatabase() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "customer_id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "address VARCHAR(255), " +
                    "phone VARCHAR(20), " +
                    "email VARCHAR(100))");

            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "account_number VARCHAR(50) PRIMARY KEY, " +
                    "customer_id VARCHAR(50) NOT NULL, " +
                    "account_type VARCHAR(20) NOT NULL, " +
                    "balance DOUBLE NOT NULL, " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id VARCHAR(50) PRIMARY KEY, " +
                    "account_number VARCHAR(50) NOT NULL, " +
                    "transaction_type VARCHAR(30) NOT NULL, " +
                    "amount DOUBLE NOT NULL, " +
                    "transaction_date VARCHAR(50) NOT NULL, " +
                    "description VARCHAR(255))");

            stmt.execute("CREATE TABLE IF NOT EXISTS loans (" +
                    "loan_id VARCHAR(50) PRIMARY KEY, " +
                    "customer_id VARCHAR(50) NOT NULL, " +
                    "loan_type VARCHAR(50) NOT NULL, " +
                    "amount DOUBLE NOT NULL, " +
                    "interest_rate DOUBLE NOT NULL, " +
                    "duration INT NOT NULL, " +
                    "status VARCHAR(30) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS employees (" +
                    "employee_id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "department VARCHAR(100) NOT NULL, " +
                    "position VARCHAR(100) NOT NULL, " +
                    "salary DOUBLE NOT NULL)");
        }
    }

    // ==========================================
    // JDBC TRANSACTIONS (CRITICAL REQUIREMENT)
    // ==========================================

    /**
     * Demonstrates JDBC Transactions with setAutoCommit(false), commit(), and rollback().
     * Guarantees ACID properties for fund transfers across accounts.
     */
    public boolean transferFundsTransactional(String senderAcc, String receiverAcc, double amount, String description)
            throws SQLException, BankException {
        if (senderAcc.equalsIgnoreCase(receiverAcc)) {
            throw new InvalidTransactionException("Cannot transfer funds to the same account.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive.");
        }

        Connection conn = null;
        PreparedStatement checkSender = null;
        PreparedStatement checkReceiver = null;
        PreparedStatement debitStmt = null;
        PreparedStatement creditStmt = null;
        PreparedStatement txSenderStmt = null;
        PreparedStatement txReceiverStmt = null;

        try {
            conn = getConnection();
            // Start Transaction: Disable Auto-Commit
            conn.setAutoCommit(false);

            // 1. Check sender balance
            checkSender = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?");
            checkSender.setString(1, senderAcc);
            ResultSet rsSender = checkSender.executeQuery();
            if (!rsSender.next()) {
                throw new InvalidAccountException("Sender account " + senderAcc + " not found in database.");
            }
            double senderBalance = rsSender.getDouble("balance");
            if (senderBalance < amount) {
                throw new InsufficientBalanceException("Sender has insufficient balance: $" + senderBalance);
            }

            // 2. Check receiver exists
            checkReceiver = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?");
            checkReceiver.setString(1, receiverAcc);
            ResultSet rsReceiver = checkReceiver.executeQuery();
            if (!rsReceiver.next()) {
                throw new InvalidAccountException("Receiver account " + receiverAcc + " not found in database.");
            }

            // 3. Debit Sender
            debitStmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?");
            debitStmt.setDouble(1, amount);
            debitStmt.setString(2, senderAcc);
            debitStmt.executeUpdate();

            // 4. Credit Receiver
            creditStmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
            creditStmt.setDouble(1, amount);
            creditStmt.setString(2, receiverAcc);
            creditStmt.executeUpdate();

            // 5. Insert Sender Transaction Record
            String txId1 = Bank.generateTransactionId();
            String dateNow = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            txSenderStmt = conn.prepareStatement("INSERT INTO transactions VALUES (?, ?, ?, ?, ?, ?)");
            txSenderStmt.setString(1, txId1);
            txSenderStmt.setString(2, senderAcc);
            txSenderStmt.setString(3, "TRANSFER_OUT");
            txSenderStmt.setDouble(4, amount);
            txSenderStmt.setString(5, dateNow);
            txSenderStmt.setString(6, "Transfer to " + receiverAcc + " - " + description);
            txSenderStmt.executeUpdate();

            // 6. Insert Receiver Transaction Record
            String txId2 = Bank.generateTransactionId();
            txReceiverStmt = conn.prepareStatement("INSERT INTO transactions VALUES (?, ?, ?, ?, ?, ?)");
            txReceiverStmt.setString(1, txId2);
            txReceiverStmt.setString(2, receiverAcc);
            txReceiverStmt.setString(3, "TRANSFER_IN");
            txReceiverStmt.setDouble(4, amount);
            txReceiverStmt.setString(5, dateNow);
            txReceiverStmt.setString(6, "Transfer from " + senderAcc + " - " + description);
            txReceiverStmt.executeUpdate();

            // All operations succeeded: COMMIT transaction
            conn.commit();
            return true;

        } catch (Exception e) {
            // Failure occurred: ROLLBACK transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (e instanceof BankException) {
                throw (BankException) e;
            } else if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new BankException("Database transaction failed: " + e.getMessage(), e);
            }
        } finally {
            if (checkSender != null) checkSender.close();
            if (checkReceiver != null) checkReceiver.close();
            if (debitStmt != null) debitStmt.close();
            if (creditStmt != null) creditStmt.close();
            if (txSenderStmt != null) txSenderStmt.close();
            if (txReceiverStmt != null) txReceiverStmt.close();
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    // ==========================================
    // CUSTOMER CRUD
    // ==========================================

    public void insertCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, name, address, phone, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            ps.executeUpdate();
        }
    }

    public void updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name = ?, address = ?, phone = ?, email = ? WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getCustomerId());
            ps.executeUpdate();
        }
    }

    public void deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }
        }
        return list;
    }

    // ==========================================
    // ACCOUNT CRUD
    // ==========================================

    public void insertAccount(Account a) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAccountNumber());
            ps.setString(2, a.getCustomerId());
            ps.setString(3, a.getAccountType());
            ps.setDouble(4, a.getBalance());
            ps.executeUpdate();
        }
    }

    public void updateAccount(Account a) throws SQLException {
        String sql = "UPDATE accounts SET customer_id = ?, account_type = ?, balance = ? WHERE account_number = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCustomerId());
            ps.setString(2, a.getAccountType());
            ps.setDouble(3, a.getBalance());
            ps.setString(4, a.getAccountNumber());
            ps.executeUpdate();
        }
    }

    public void deleteAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
    }

    public List<Account> getAllAccounts() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String num = rs.getString("account_number");
                String cust = rs.getString("customer_id");
                String type = rs.getString("account_type");
                double bal = rs.getDouble("balance");
                if ("Savings".equalsIgnoreCase(type)) {
                    list.add(new SavingsAccount(num, cust, bal, 4.5, 500.0));
                } else {
                    list.add(new CheckingAccount(num, cust, bal, 1000.0, 1.5));
                }
            }
        }
        return list;
    }

    // ==========================================
    // TRANSACTION CRUD
    // ==========================================

    public void insertTransaction(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, transaction_date, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            ps.setString(2, t.getAccountNumber());
            ps.setString(3, t.getTransactionType());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getTransactionDate());
            ps.setString(6, t.getDescription());
            ps.executeUpdate();
        }
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("transaction_date"),
                        rs.getString("description")
                ));
            }
        }
        return list;
    }

    // ==========================================
    // LOAN CRUD
    // ==========================================

    public void insertLoan(Loan l) throws SQLException {
        String sql = "INSERT INTO loans (loan_id, customer_id, loan_type, amount, interest_rate, duration, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getLoanId());
            ps.setString(2, l.getCustomerId());
            ps.setString(3, l.getLoanType());
            ps.setDouble(4, l.getAmount());
            ps.setDouble(5, l.getInterestRate());
            ps.setInt(6, l.getDuration());
            ps.setString(7, l.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateLoan(Loan l) throws SQLException {
        String sql = "UPDATE loans SET customer_id = ?, loan_type = ?, amount = ?, interest_rate = ?, duration = ?, status = ? WHERE loan_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getCustomerId());
            ps.setString(2, l.getLoanType());
            ps.setDouble(3, l.getAmount());
            ps.setDouble(4, l.getInterestRate());
            ps.setInt(5, l.getDuration());
            ps.setString(6, l.getStatus());
            ps.setString(7, l.getLoanId());
            ps.executeUpdate();
        }
    }

    public void deleteLoan(String loanId) throws SQLException {
        String sql = "DELETE FROM loans WHERE loan_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loanId);
            ps.executeUpdate();
        }
    }

    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Loan(
                        rs.getString("loan_id"),
                        rs.getString("customer_id"),
                        rs.getString("loan_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("duration"),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    // ==========================================
    // EMPLOYEE CRUD
    // ==========================================

    public void insertEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (employee_id, name, department, position, salary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getEmployeeId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getDepartment());
            ps.setString(4, e.getPosition());
            ps.setDouble(5, e.getSalary());
            ps.executeUpdate();
        }
    }

    public void updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE employees SET name = ?, department = ?, position = ?, salary = ? WHERE employee_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDepartment());
            ps.setString(3, e.getPosition());
            ps.setDouble(4, e.getSalary());
            ps.setString(5, e.getEmployeeId());
            ps.executeUpdate();
        }
    }

    public void deleteEmployee(String empId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("salary")
                ));
            }
        }
        return list;
    }
}
