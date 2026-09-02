import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Core Bank Management Engine.
 * Demonstrates:
 * 1. Java Collections Framework: HashMap, ArrayList, List, Map
 * 2. Generics: HashMap<String, Account>, ArrayList<Transaction>, etc.
 * 3. Iterators: Explicit Iterator usage for collection iteration.
 * 4. Exception Handling: Throws and catches custom exceptions.
 * 5. Serialization: Implements Serializable.
 */
public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;

    // Collections
    private Map<String, Account> accounts;
    private Map<String, Customer> customers;
    private List<Transaction> transactions;
    private Map<String, Loan> loans;
    private Map<String, Employee> employees;

    public Bank() {
        this.accounts = new HashMap<>();
        this.customers = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.loans = new HashMap<>();
        this.employees = new HashMap<>();
    }

    // ==========================================
    // CUSTOMER MANAGEMENT
    // ==========================================

    public synchronized void addCustomer(Customer customer) throws BankException {
        if (customer == null || customer.getCustomerId() == null || customer.getCustomerId().trim().isEmpty()) {
            throw new BankException("Customer ID cannot be empty.");
        }
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new BankException("Customer name cannot be empty.");
        }
        if (customers.containsKey(customer.getCustomerId().trim())) {
            throw new BankException("Customer ID '" + customer.getCustomerId() + "' already exists.");
        }
        customers.put(customer.getCustomerId().trim(), customer);
    }

    public synchronized Customer getCustomer(String customerId) throws InvalidAccountException {
        if (customerId == null || !customers.containsKey(customerId.trim())) {
            throw new InvalidAccountException("Customer with ID '" + customerId + "' not found.");
        }
        return customers.get(customerId.trim());
    }

    public synchronized void updateCustomer(Customer customer) throws BankException {
        if (customer == null || !customers.containsKey(customer.getCustomerId().trim())) {
            throw new BankException("Cannot update. Customer ID '" + (customer != null ? customer.getCustomerId() : "") + "' does not exist.");
        }
        customers.put(customer.getCustomerId().trim(), customer);
    }

    public synchronized void deleteCustomer(String customerId) throws BankException {
        if (customerId == null || !customers.containsKey(customerId.trim())) {
            throw new BankException("Customer ID '" + customerId + "' does not exist.");
        }
        // Verify no active accounts linked
        Iterator<Account> it = accounts.values().iterator();
        while (it.hasNext()) {
            Account acc = it.next();
            if (acc.getCustomerId().equalsIgnoreCase(customerId.trim())) {
                throw new BankException("Cannot delete customer. Linked active account exists: " + acc.getAccountNumber());
            }
        }
        customers.remove(customerId.trim());
    }

    public synchronized List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        Iterator<Customer> iterator = customers.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    // ==========================================
    // ACCOUNT MANAGEMENT
    // ==========================================

    public synchronized void createAccount(Account account) throws BankException {
        if (account == null || account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            throw new InvalidAccountException("Account number cannot be empty.");
        }
        String accNum = account.getAccountNumber().trim();
        if (accounts.containsKey(accNum)) {
            throw new InvalidAccountException("Account number '" + accNum + "' already exists.");
        }
        if (!customers.containsKey(account.getCustomerId().trim())) {
            throw new BankException("Customer ID '" + account.getCustomerId() + "' does not exist. Please create the customer first.");
        }
        if (account.getBalance() < 0) {
            throw new InvalidAmountException("Initial balance cannot be negative.");
        }
        accounts.put(accNum, account);

        // Record initial deposit transaction if balance > 0
        if (account.getBalance() > 0) {
            recordTransaction(new Transaction(
                    generateTransactionId(),
                    accNum,
                    "DEPOSIT",
                    account.getBalance(),
                    "Initial opening balance deposit"
            ));
        }
    }

    public synchronized Account getAccount(String accountNumber) throws InvalidAccountException {
        if (accountNumber == null || !accounts.containsKey(accountNumber.trim())) {
            throw new InvalidAccountException("Account with number '" + accountNumber + "' not found.");
        }
        return accounts.get(accountNumber.trim());
    }

    public synchronized void updateAccount(Account account) throws BankException {
        if (account == null || !accounts.containsKey(account.getAccountNumber().trim())) {
            throw new InvalidAccountException("Account '" + (account != null ? account.getAccountNumber() : "") + "' does not exist.");
        }
        accounts.put(account.getAccountNumber().trim(), account);
    }

    public synchronized void deleteAccount(String accountNumber) throws BankException {
        if (accountNumber == null || !accounts.containsKey(accountNumber.trim())) {
            throw new InvalidAccountException("Account number '" + accountNumber + "' does not exist.");
        }
        Account acc = accounts.get(accountNumber.trim());
        if (acc.getBalance() > 0) {
            throw new BankException("Cannot delete account with positive balance ($" + acc.getBalance() + "). Please withdraw or transfer funds first.");
        }
        accounts.remove(accountNumber.trim());
    }

    public synchronized List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        Iterator<Account> iterator = accounts.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    // ==========================================
    // TRANSACTION OPERATIONS
    // ==========================================

    public void deposit(String accountNumber, double amount, String description) throws BankException {
        Account account = getAccount(accountNumber);
        account.deposit(amount, description);
        recordTransaction(new Transaction(
                generateTransactionId(),
                account.getAccountNumber(),
                "DEPOSIT",
                amount,
                description
        ));
    }

    public void withdraw(String accountNumber, double amount, String description) throws BankException {
        Account account = getAccount(accountNumber);
        account.withdraw(amount);
        recordTransaction(new Transaction(
                generateTransactionId(),
                account.getAccountNumber(),
                "WITHDRAWAL",
                amount,
                description
        ));
    }

    public void transfer(String senderAccNum, String receiverAccNum, double amount, String description) throws BankException {
        Account sender = getAccount(senderAccNum);
        Account receiver = getAccount(receiverAccNum);
        sender.transfer(receiver, amount);

        String txId1 = generateTransactionId();
        recordTransaction(new Transaction(
                txId1,
                sender.getAccountNumber(),
                "TRANSFER_OUT",
                amount,
                "Transfer to " + receiver.getAccountNumber() + (description != null && !description.isEmpty() ? " - " + description : "")
        ));

        String txId2 = generateTransactionId();
        recordTransaction(new Transaction(
                txId2,
                receiver.getAccountNumber(),
                "TRANSFER_IN",
                amount,
                "Transfer from " + sender.getAccountNumber() + (description != null && !description.isEmpty() ? " - " + description : "")
        ));
    }

    public synchronized void recordTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    public synchronized List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public synchronized List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.getAccountNumber().equalsIgnoreCase(accountNumber.trim())) {
                list.add(tx);
            }
        }
        return list;
    }

    // ==========================================
    // LOAN MANAGEMENT
    // ==========================================

    public synchronized void addLoan(Loan loan) throws BankException {
        if (loan == null || loan.getLoanId() == null || loan.getLoanId().trim().isEmpty()) {
            throw new BankException("Loan ID cannot be empty.");
        }
        String loanId = loan.getLoanId().trim();
        if (loans.containsKey(loanId)) {
            throw new BankException("Loan ID '" + loanId + "' already exists.");
        }
        if (!customers.containsKey(loan.getCustomerId().trim())) {
            throw new BankException("Customer ID '" + loan.getCustomerId() + "' does not exist.");
        }
        if (loan.getAmount() <= 0) {
            throw new InvalidAmountException("Loan amount must be positive.");
        }
        loans.put(loanId, loan);
    }

    public synchronized Loan getLoan(String loanId) throws BankException {
        if (loanId == null || !loans.containsKey(loanId.trim())) {
            throw new BankException("Loan ID '" + loanId + "' not found.");
        }
        return loans.get(loanId.trim());
    }

    public synchronized void updateLoan(Loan loan) throws BankException {
        if (loan == null || !loans.containsKey(loan.getLoanId().trim())) {
            throw new BankException("Loan ID '" + (loan != null ? loan.getLoanId() : "") + "' does not exist.");
        }
        loans.put(loan.getLoanId().trim(), loan);
    }

    public synchronized void deleteLoan(String loanId) throws BankException {
        if (loanId == null || !loans.containsKey(loanId.trim())) {
            throw new BankException("Loan ID '" + loanId + "' does not exist.");
        }
        loans.remove(loanId.trim());
    }

    public synchronized void approveLoan(String loanId) throws BankException {
        Loan loan = getLoan(loanId);
        loan.setStatus("APPROVED");
    }

    public synchronized void rejectLoan(String loanId) throws BankException {
        Loan loan = getLoan(loanId);
        loan.setStatus("REJECTED");
    }

    public synchronized List<Loan> getAllLoans() {
        List<Loan> list = new ArrayList<>();
        Iterator<Loan> iterator = loans.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    // ==========================================
    // EMPLOYEE MANAGEMENT
    // ==========================================

    public synchronized void addEmployee(Employee employee) throws BankException {
        if (employee == null || employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            throw new BankException("Employee ID cannot be empty.");
        }
        String empId = employee.getEmployeeId().trim();
        if (employees.containsKey(empId)) {
            throw new BankException("Employee ID '" + empId + "' already exists.");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new BankException("Employee name cannot be empty.");
        }
        employees.put(empId, employee);
    }

    public synchronized Employee getEmployee(String employeeId) throws BankException {
        if (employeeId == null || !employees.containsKey(employeeId.trim())) {
            throw new BankException("Employee ID '" + employeeId + "' not found.");
        }
        return employees.get(employeeId.trim());
    }

    public synchronized void updateEmployee(Employee employee) throws BankException {
        if (employee == null || !employees.containsKey(employee.getEmployeeId().trim())) {
            throw new BankException("Employee ID '" + (employee != null ? employee.getEmployeeId() : "") + "' does not exist.");
        }
        employees.put(employee.getEmployeeId().trim(), employee);
    }

    public synchronized void deleteEmployee(String employeeId) throws BankException {
        if (employeeId == null || !employees.containsKey(employeeId.trim())) {
            throw new BankException("Employee ID '" + employeeId + "' does not exist.");
        }
        employees.remove(employeeId.trim());
    }

    public synchronized List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        Iterator<Employee> iterator = employees.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    // ==========================================
    // UTILITIES & SAMPLE DATA
    // ==========================================

    public static String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Preloads rich demonstration test data.
     */
    public void loadSampleData() {
        try {
            // Customers
            addCustomer(new Customer("C001", "Arun Kumar", "124 MG Road, Bangalore", "9876543210", "arun@example.com"));
            addCustomer(new Customer("C002", "Priya Sharma", "45 Park Street, Kolkata", "9876543211", "priya@example.com"));
            addCustomer(new Customer("C003", "Rahul Verma", "78 Nehru Nagar, Delhi", "9876543212", "rahul@example.com"));
            addCustomer(new Customer("C004", "Sneha Patel", "12 SV Road, Mumbai", "9876543213", "sneha@example.com"));

            // Accounts
            createAccount(new SavingsAccount("A1001", "C001", 10000.0, 4.5, 500.0));
            createAccount(new CheckingAccount("A1002", "C001", 5000.0, 2000.0, 2.0));
            createAccount(new SavingsAccount("A1003", "C002", 25000.0, 5.0, 1000.0));
            createAccount(new CheckingAccount("A1004", "C003", 15000.0, 3000.0, 1.5));
            createAccount(new SavingsAccount("A1005", "C004", 8000.0, 4.0, 500.0));

            // Loans
            addLoan(new Loan("L501", "C001", "Home", 500000.0, 8.25, 60, "APPROVED"));
            addLoan(new Loan("L502", "C002", "Personal", 50000.0, 10.5, 24, "PENDING"));
            addLoan(new Loan("L503", "C003", "Education", 200000.0, 7.5, 36, "APPROVED"));
            addLoan(new Loan("L504", "C004", "Business", 750000.0, 9.0, 48, "PENDING"));

            // Employees
            addEmployee(new Employee("E101", "Vikram Rathore", "Management", "Branch Manager", 85000.0));
            addEmployee(new Employee("E102", "Ananya Deshmukh", "Operations", "Chief Cashier", 48000.0));
            addEmployee(new Employee("E103", "Karthik Raja", "Customer Support", "Relationship Officer", 42000.0));
            addEmployee(new Employee("E104", "Meera Nair", "Loans & Credit", "Credit Analyst", 52000.0));

        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
}
