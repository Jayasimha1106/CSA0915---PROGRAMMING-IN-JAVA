import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles File I/O operations using Java Character/Byte Streams.
 * Demonstrates:
 * 1. Java I/O Streams: BufferedReader, BufferedWriter, FileReader, FileWriter
 * 2. Try-with-resources: Automatic resource cleanup and safe error handling
 * 3. File Operations: Write, Read, Append, Search
 */
public class FileManager {
    private String dataDirectory;

    public FileManager(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public FileManager() {
        this("data");
    }

    // ==========================================
    // CUSTOMERS I/O
    // ==========================================

    public void saveCustomers(List<Customer> customers) throws IOException {
        File file = new File(dataDirectory, "customers.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Customer c : customers) {
                // Format: ID|Name|Address|Phone|Email
                writer.write(String.format("%s|%s|%s|%s|%s",
                        c.getCustomerId(), c.getName(), c.getAddress(), c.getPhone(), c.getEmail()));
                writer.newLine();
            }
        }
    }

    public List<Customer> loadCustomers() throws IOException {
        List<Customer> list = new ArrayList<>();
        File file = new File(dataDirectory, "customers.txt");
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length >= 5) {
                    list.add(new Customer(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim()));
                }
            }
        }
        return list;
    }

    public void appendCustomer(Customer c) throws IOException {
        File file = new File(dataDirectory, "customers.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(String.format("%s|%s|%s|%s|%s",
                    c.getCustomerId(), c.getName(), c.getAddress(), c.getPhone(), c.getEmail()));
            writer.newLine();
        }
    }

    // ==========================================
    // ACCOUNTS I/O
    // ==========================================

    public void saveAccounts(List<Account> accounts) throws IOException {
        File file = new File(dataDirectory, "accounts.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Account a : accounts) {
                // Format: AccNum|CustId|Type|Balance|Extra1|Extra2
                if (a instanceof SavingsAccount) {
                    SavingsAccount sa = (SavingsAccount) a;
                    writer.write(String.format("%s|%s|%s|%.2f|%.2f|%.2f",
                            sa.getAccountNumber(), sa.getCustomerId(), sa.getAccountType(),
                            sa.getBalance(), sa.getInterestRate(), sa.getMinimumBalance()));
                } else if (a instanceof CheckingAccount) {
                    CheckingAccount ca = (CheckingAccount) a;
                    writer.write(String.format("%s|%s|%s|%.2f|%.2f|%.2f",
                            ca.getAccountNumber(), ca.getCustomerId(), ca.getAccountType(),
                            ca.getBalance(), ca.getOverdraftLimit(), ca.getTransactionFee()));
                } else {
                    writer.write(String.format("%s|%s|%s|%.2f",
                            a.getAccountNumber(), a.getCustomerId(), a.getAccountType(), a.getBalance()));
                }
                writer.newLine();
            }
        }
    }

    public List<Account> loadAccounts() throws IOException {
        List<Account> list = new ArrayList<>();
        File file = new File(dataDirectory, "accounts.txt");
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length >= 4) {
                    String accNum = p[0].trim();
                    String custId = p[1].trim();
                    String type = p[2].trim();
                    double balance = Double.parseDouble(p[3].trim());

                    if (type.equalsIgnoreCase("Savings")) {
                        double rate = (p.length >= 5) ? Double.parseDouble(p[4].trim()) : 4.5;
                        double minBal = (p.length >= 6) ? Double.parseDouble(p[5].trim()) : 500.0;
                        list.add(new SavingsAccount(accNum, custId, balance, rate, minBal));
                    } else {
                        double overdraft = (p.length >= 5) ? Double.parseDouble(p[4].trim()) : 1000.0;
                        double fee = (p.length >= 6) ? Double.parseDouble(p[5].trim()) : 1.5;
                        list.add(new CheckingAccount(accNum, custId, balance, overdraft, fee));
                    }
                }
            }
        }
        return list;
    }

    // ==========================================
    // TRANSACTIONS I/O
    // ==========================================

    public void saveTransactions(List<Transaction> transactions) throws IOException {
        File file = new File(dataDirectory, "transactions.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Transaction t : transactions) {
                // Format: TxId|AccNum|Type|Amount|Date|Desc
                writer.write(String.format("%s|%s|%s|%.2f|%s|%s",
                        t.getTransactionId(), t.getAccountNumber(), t.getTransactionType(),
                        t.getAmount(), t.getTransactionDate(), t.getDescription()));
                writer.newLine();
            }
        }
    }

    public List<Transaction> loadTransactions() throws IOException {
        List<Transaction> list = new ArrayList<>();
        File file = new File(dataDirectory, "transactions.txt");
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length >= 6) {
                    list.add(new Transaction(p[0].trim(), p[1].trim(), p[2].trim(),
                            Double.parseDouble(p[3].trim()), p[4].trim(), p[5].trim()));
                }
            }
        }
        return list;
    }

    // ==========================================
    // LOANS I/O
    // ==========================================

    public void saveLoans(List<Loan> loans) throws IOException {
        File file = new File(dataDirectory, "loans.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Loan l : loans) {
                // Format: LoanId|CustId|Type|Amount|Rate|Duration|Status
                writer.write(String.format("%s|%s|%s|%.2f|%.2f|%d|%s",
                        l.getLoanId(), l.getCustomerId(), l.getLoanType(),
                        l.getAmount(), l.getInterestRate(), l.getDuration(), l.getStatus()));
                writer.newLine();
            }
        }
    }

    public List<Loan> loadLoans() throws IOException {
        List<Loan> list = new ArrayList<>();
        File file = new File(dataDirectory, "loans.txt");
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length >= 7) {
                    list.add(new Loan(p[0].trim(), p[1].trim(), p[2].trim(),
                            Double.parseDouble(p[3].trim()), Double.parseDouble(p[4].trim()),
                            Integer.parseInt(p[5].trim()), p[6].trim()));
                }
            }
        }
        return list;
    }

    // ==========================================
    // EMPLOYEES I/O
    // ==========================================

    public void saveEmployees(List<Employee> employees) throws IOException {
        File file = new File(dataDirectory, "employees.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Employee e : employees) {
                // Format: EmpId|Name|Dept|Pos|Salary
                writer.write(String.format("%s|%s|%s|%s|%.2f",
                        e.getEmployeeId(), e.getName(), e.getDepartment(), e.getPosition(), e.getSalary()));
                writer.newLine();
            }
        }
    }

    public List<Employee> loadEmployees() throws IOException {
        List<Employee> list = new ArrayList<>();
        File file = new File(dataDirectory, "employees.txt");
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length >= 5) {
                    list.add(new Employee(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), Double.parseDouble(p[4].trim())));
                }
            }
        }
        return list;
    }

    // ==========================================
    // SEARCH STORED RECORDS
    // ==========================================

    public List<String> searchInFile(String filename, String keyword) throws IOException {
        List<String> results = new ArrayList<>();
        File file = new File(dataDirectory, filename);
        if (!file.exists()) return results;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(String.format("Line %d: %s", lineNum, line));
                }
                lineNum++;
            }
        }
        return results;
    }
}
