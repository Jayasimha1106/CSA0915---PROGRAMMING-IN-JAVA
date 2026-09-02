import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Smart Bank Management System - Main Desktop GUI Application.
 * Built using pure Java AWT (Abstract Window Toolkit) and Delegation Event Model.
 * 
 * Demonstrates:
 * 1. Java AWT GUI: Frame, Panel, Label, TextField, Button, Choice, List, TextArea, Checkbox, Dialog
 * 2. Layout Managers: BorderLayout, GridLayout, FlowLayout, CardLayout
 * 3. Delegation Event Model: ActionListener, ItemListener, WindowAdapter
 * 4. Full Integration: OOP Domain Models, Collections, Multithreading, File I/O, Serialization, JDBC
 */
public class SmartBankManagementSystem {

    private Bank bank;
    private FileManager fileManager;
    private SerializationManager serializationManager;
    private DatabaseManager databaseManager;
    private TransactionManager transactionManager;

    private Frame loginFrame;
    private Frame mainFrame;
    private CardLayout cardLayout;
    private Panel contentPanel;
    private Label statusLabel;

    public static final Color COLOR_PRIMARY = new Color(24, 43, 73);      // Deep Navy
    public static final Color COLOR_SECONDARY = new Color(41, 128, 185); // Blue Accent
    public static final Color COLOR_BG = new Color(245, 247, 250);       // Soft Gray Background
    public static final Color COLOR_CARD_BG = new Color(255, 255, 255);  // White Card
    public static final Color COLOR_TEXT = new Color(33, 37, 41);        // Dark Charcoal
    public static final Color COLOR_SUCCESS = new Color(39, 174, 96);    // Green
    public static final Color COLOR_DANGER = new Color(192, 57, 43);     // Red
    public static final Color COLOR_SIDEBAR = new Color(30, 41, 59);     // Slate Navy

    public SmartBankManagementSystem() {
        bank = new Bank();
        bank.loadSampleData();

        File dataDir = new File("data");
        if (!dataDir.exists()) dataDir.mkdirs();

        fileManager = new FileManager("data");
        serializationManager = new SerializationManager();
        databaseManager = new DatabaseManager();
        transactionManager = new TransactionManager();

        createLoginScreen();
    }

    public static void main(String[] args) {
        new SmartBankManagementSystem();
    }

    private void createLoginScreen() {
        loginFrame = new Frame("Smart Bank Management System - Login");
        loginFrame.setSize(460, 380);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setBackground(COLOR_BG);
        loginFrame.setResizable(false);
        centerWindow(loginFrame);

        Panel headerPanel = new Panel();
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        Label titleLabel = new Label("SMART BANK MANAGEMENT SYSTEM");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerPanel.add(titleLabel);

        Panel centerPanel = new Panel(new GridLayout(4, 2, 12, 15));
        centerPanel.setBackground(COLOR_BG);

        Label lblUser = new Label("Username:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));
        TextField txtUser = new TextField("admin", 15);

        Label lblPass = new Label("Password:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        TextField txtPass = new TextField("admin123", 15);
        txtPass.setEchoChar('*');

        Checkbox chkShowPass = new Checkbox("Show Password");
        chkShowPass.addItemListener(e -> {
            if (chkShowPass.getState()) {
                txtPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('*');
            }
        });

        Label lblHint = new Label("(Demo: admin / admin123)");
        lblHint.setForeground(Color.GRAY);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));

        centerPanel.add(lblUser);
        centerPanel.add(txtUser);
        centerPanel.add(lblPass);
        centerPanel.add(txtPass);
        centerPanel.add(chkShowPass);
        centerPanel.add(lblHint);

        Panel centerWrapper = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centerWrapper.add(centerPanel);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(COLOR_BG);
        Button btnLogin = createStyledButton("Login to System", COLOR_SUCCESS);
        Button btnExit = createStyledButton("Exit", COLOR_DANGER);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = txtPass.getText().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                showDialog(loginFrame, "Validation Error", "Please enter both username and password.", true);
                return;
            }

            if (user.equals("admin") && pass.equals("admin123")) {
                loginFrame.setVisible(false);
                loginFrame.dispose();
                createMainDashboard();
            } else {
                showDialog(loginFrame, "Authentication Failed", "Invalid username or password. (Hint: admin / admin123)", true);
            }
        });

        btnExit.addActionListener(e -> {
            transactionManager.shutdown();
            System.exit(0);
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        loginFrame.add(headerPanel, BorderLayout.NORTH);
        loginFrame.add(centerWrapper, BorderLayout.CENTER);
        loginFrame.add(buttonPanel, BorderLayout.SOUTH);

        loginFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                transactionManager.shutdown();
                System.exit(0);
            }
        });

        loginFrame.setVisible(true);
    }
    private void createMainDashboard() {
        mainFrame = new Frame("Smart Bank Management System - Enterprise Edition");
        mainFrame.setSize(1050, 720);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(COLOR_BG);
        centerWindow(mainFrame);

        Panel topBanner = new Panel(new BorderLayout());
        topBanner.setBackground(COLOR_PRIMARY);
        topBanner.setPreferredSize(new Dimension(1050, 50));

        Label brandLabel = new Label("  SMART BANK MANAGEMENT SYSTEM  |  CSA09 Assignment Edition");
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("Arial", Font.BOLD, 15));

        Label userProfileLabel = new Label("Logged in as: Administrator (Branch 01)  ");
        userProfileLabel.setForeground(new Color(200, 220, 240));
        userProfileLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        topBanner.add(brandLabel, BorderLayout.WEST);
        topBanner.add(userProfileLabel, BorderLayout.EAST);

        Panel sidebar = new Panel(new GridLayout(13, 1, 4, 4));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 600));

        String[] navModules = {
                "Customers",
                "Accounts",
                "Deposit",
                "Withdrawal",
                "Fund Transfer",
                "Loans",
                "Employees",
                "Transactions",
                "File Storage",
                "Serialization",
                "Database / JDBC",
                "Concurrent Simulator",
                "Logout / Exit"
        };

        cardLayout = new CardLayout();
        contentPanel = new Panel(cardLayout);
        contentPanel.setBackground(COLOR_BG);

        contentPanel.add(buildCustomerModule(), "Customers");
        contentPanel.add(buildAccountModule(), "Accounts");
        contentPanel.add(buildDepositModule(), "Deposit");
        contentPanel.add(buildWithdrawalModule(), "Withdrawal");
        contentPanel.add(buildTransferModule(), "Fund Transfer");
        contentPanel.add(buildLoanModule(), "Loans");
        contentPanel.add(buildEmployeeModule(), "Employees");
        contentPanel.add(buildTransactionModule(), "Transactions");
        contentPanel.add(buildFileModule(), "File Storage");
        contentPanel.add(buildSerializationModule(), "Serialization");
        contentPanel.add(buildDatabaseModule(), "Database / JDBC");
        contentPanel.add(buildConcurrencySimulatorModule(), "Concurrent Simulator");

        for (String mod : navModules) {
            Button navBtn = new Button(mod);
            navBtn.setBackground(new Color(45, 55, 72));
            navBtn.setForeground(Color.WHITE);
            navBtn.setFont(new Font("Arial", Font.BOLD, 11));

            navBtn.addActionListener(e -> {
                if (mod.equals("Logout / Exit")) {
                    mainFrame.setVisible(false);
                    mainFrame.dispose();
                    new SmartBankManagementSystem();
                } else {
                    cardLayout.show(contentPanel, mod);
                    setStatus("Active Module: " + mod);
                }
            });
            sidebar.add(navBtn);
        }

        Panel statusBar = new Panel(new BorderLayout());
        statusBar.setBackground(new Color(220, 225, 230));
        statusBar.setPreferredSize(new Dimension(1050, 26));
        statusLabel = new Label(" Ready | Ready for banking operations.");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(statusLabel, BorderLayout.WEST);

        mainFrame.add(topBanner, BorderLayout.NORTH);
        mainFrame.add(sidebar, BorderLayout.WEST);
        mainFrame.add(contentPanel, BorderLayout.CENTER);
        mainFrame.add(statusBar, BorderLayout.SOUTH);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                transactionManager.shutdown();
                System.exit(0);
            }
        });

        mainFrame.setVisible(true);
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(" " + message);
        }
    }

    private Panel buildCustomerModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Customer Management Module", "Create, search, update, and manage bank customer profiles.");
        main.add(titleP, BorderLayout.NORTH);

        Panel bodyP = new Panel(new GridLayout(1, 2, 12, 12));

        Panel formP = new Panel(new BorderLayout());
        formP.setBackground(COLOR_CARD_BG);

        Panel grid = new Panel(new GridLayout(5, 2, 8, 10));
        TextField txtId = new TextField();
        TextField txtName = new TextField();
        TextField txtPhone = new TextField();
        TextField txtEmail = new TextField();
        TextField txtAddress = new TextField();

        grid.add(new Label("Customer ID:"));
        grid.add(txtId);
        grid.add(new Label("Full Name:"));
        grid.add(txtName);
        grid.add(new Label("Phone Number:"));
        grid.add(txtPhone);
        grid.add(new Label("Email Address:"));
        grid.add(txtEmail);
        grid.add(new Label("Residential Address:"));
        grid.add(txtAddress);

        Panel formBtnP = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        Button btnAdd = createStyledButton("Add", COLOR_SUCCESS);
        Button btnSearch = createStyledButton("Search", COLOR_SECONDARY);
        Button btnUpdate = createStyledButton("Update", new Color(211, 84, 0));
        Button btnDelete = createStyledButton("Delete", COLOR_DANGER);
        Button btnClear = createStyledButton("Clear", Color.GRAY);

        formBtnP.add(btnAdd);
        formBtnP.add(btnSearch);
        formBtnP.add(btnUpdate);
        formBtnP.add(btnDelete);
        formBtnP.add(btnClear);

        formP.add(grid, BorderLayout.CENTER);
        formP.add(formBtnP, BorderLayout.SOUTH);

        Panel listP = new Panel(new BorderLayout());
        listP.setBackground(COLOR_CARD_BG);
        Label lblList = new Label("Customer Directory (In-Memory Collections)");
        lblList.setFont(new Font("Arial", Font.BOLD, 12));
        java.awt.List customerList = new java.awt.List(15);
        Button btnRefresh = createStyledButton("Refresh Customer List", COLOR_PRIMARY);

        Runnable refreshList = () -> {
            customerList.removeAll();
            java.util.List<Customer> all = bank.getAllCustomers();
            for (Customer c : all) {
                customerList.add(String.format("%s | %s | %s | %s", c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail()));
            }
        };
        refreshList.run();

        btnRefresh.addActionListener(e -> refreshList.run());

        btnAdd.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String phone = txtPhone.getText().trim();
                String email = txtEmail.getText().trim();
                String addr = txtAddress.getText().trim();

                if (id.isEmpty() || name.isEmpty()) {
                    showDialog(mainFrame, "Input Error", "Customer ID and Name are required fields.", true);
                    return;
                }

                Customer c = new Customer(id, name, addr, phone, email);
                bank.addCustomer(c);
                refreshList.run();
                setStatus("Customer " + id + " added successfully.");
                showDialog(mainFrame, "Success", "Customer " + name + " (" + id + ") created successfully!", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnSearch.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                if (id.isEmpty()) {
                    showDialog(mainFrame, "Search", "Please enter a Customer ID to search.", true);
                    return;
                }
                Customer c = bank.getCustomer(id);
                txtName.setText(c.getName());
                txtAddress.setText(c.getAddress());
                txtPhone.setText(c.getPhone());
                txtEmail.setText(c.getEmail());
                setStatus("Customer found: " + c.getName());
            } catch (Exception ex) {
                showDialog(mainFrame, "Search Result", ex.getMessage(), true);
            }
        });

        btnUpdate.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                if (id.isEmpty()) {
                    showDialog(mainFrame, "Update Error", "Enter Customer ID to update.", true);
                    return;
                }
                Customer c = new Customer(id, txtName.getText().trim(), txtAddress.getText().trim(),
                        txtPhone.getText().trim(), txtEmail.getText().trim());
                bank.updateCustomer(c);
                refreshList.run();
                showDialog(mainFrame, "Success", "Customer updated successfully.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                if (id.isEmpty()) {
                    showDialog(mainFrame, "Delete Error", "Enter Customer ID to delete.", true);
                    return;
                }
                bank.deleteCustomer(id);
                refreshList.run();
                btnClear.getActionListeners()[0].actionPerformed(null);
                showDialog(mainFrame, "Success", "Customer deleted successfully.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnClear.addActionListener(e -> {
            txtId.setText("");
            txtName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtAddress.setText("");
        });

        customerList.addItemListener(e -> {
            String selected = customerList.getSelectedItem();
            if (selected != null) {
                String[] parts = selected.split("\\|");
                if (parts.length > 0) {
                    txtId.setText(parts[0].trim());
                    btnSearch.getActionListeners()[0].actionPerformed(null);
                }
            }
        });

        listP.add(lblList, BorderLayout.NORTH);
        listP.add(customerList, BorderLayout.CENTER);
        listP.add(btnRefresh, BorderLayout.SOUTH);

        bodyP.add(formP);
        bodyP.add(listP);

        main.add(bodyP, BorderLayout.CENTER);
        return main;
    }
    private Panel buildAccountModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Account Management Module", "Create Savings/Checking accounts demonstrating Inheritance & Polymorphism.");
        main.add(titleP, BorderLayout.NORTH);

        Panel bodyP = new Panel(new GridLayout(1, 2, 12, 12));

        Panel formP = new Panel(new BorderLayout());
        formP.setBackground(COLOR_CARD_BG);

        Panel grid = new Panel(new GridLayout(6, 2, 8, 8));
        TextField txtAccNum = new TextField();
        TextField txtCustId = new TextField();
        Choice choiceType = new Choice();
        choiceType.add("Savings");
        choiceType.add("Checking");
        TextField txtBalance = new TextField("1000.00");
        TextField txtExtra1 = new TextField("4.5");
        Label lblExtra1 = new Label("Interest Rate (%):");
        TextField txtExtra2 = new TextField("500.0");
        Label lblExtra2 = new Label("Min Balance ($):");

        choiceType.addItemListener(e -> {
            if ("Savings".equals(choiceType.getSelectedItem())) {
                lblExtra1.setText("Interest Rate (%):");
                txtExtra1.setText("4.5");
                lblExtra2.setText("Min Balance ($):");
                txtExtra2.setText("500.0");
            } else {
                lblExtra1.setText("Overdraft Limit ($):");
                txtExtra1.setText("1000.0");
                lblExtra2.setText("Tx Fee ($):");
                txtExtra2.setText("1.50");
            }
        });

        grid.add(new Label("Account Number:"));
        grid.add(txtAccNum);
        grid.add(new Label("Customer ID:"));
        grid.add(txtCustId);
        grid.add(new Label("Account Type:"));
        grid.add(choiceType);
        grid.add(new Label("Initial Balance ($):"));
        grid.add(txtBalance);
        grid.add(lblExtra1);
        grid.add(txtExtra1);
        grid.add(lblExtra2);
        grid.add(txtExtra2);

        Panel formBtnP = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        Button btnCreate = createStyledButton("Create", COLOR_SUCCESS);
        Button btnSearch = createStyledButton("Search", COLOR_SECONDARY);
        Button btnDelete = createStyledButton("Delete", COLOR_DANGER);
        Button btnPolymorphism = createStyledButton("Demonstrate Polymorphism", COLOR_PRIMARY);
        Button btnClear = createStyledButton("Clear", Color.GRAY);

        formBtnP.add(btnCreate);
        formBtnP.add(btnSearch);
        formBtnP.add(btnDelete);
        formBtnP.add(btnPolymorphism);
        formBtnP.add(btnClear);

        formP.add(grid, BorderLayout.CENTER);
        formP.add(formBtnP, BorderLayout.SOUTH);

        Panel listP = new Panel(new BorderLayout());
        listP.setBackground(COLOR_CARD_BG);
        Label lblList = new Label("Accounts Ledger (HashMap<String, Account>)");
        lblList.setFont(new Font("Arial", Font.BOLD, 12));
        java.awt.List accountList = new java.awt.List(15);
        Button btnRefresh = createStyledButton("Refresh Account List", COLOR_PRIMARY);

        Runnable refreshAccounts = () -> {
            accountList.removeAll();
            java.util.List<Account> all = bank.getAllAccounts();
            for (Account a : all) {
                accountList.add(String.format("%s | Cust: %s | Type: %s | Bal: $%.2f",
                        a.getAccountNumber(), a.getCustomerId(), a.getAccountType(), a.getBalance()));
            }
        };
        refreshAccounts.run();

        btnRefresh.addActionListener(e -> refreshAccounts.run());

        btnCreate.addActionListener(e -> {
            try {
                String accNum = txtAccNum.getText().trim();
                String custId = txtCustId.getText().trim();
                String type = choiceType.getSelectedItem();
                double bal = Double.parseDouble(txtBalance.getText().trim());

                if (accNum.isEmpty() || custId.isEmpty()) {
                    showDialog(mainFrame, "Input Error", "Account Number and Customer ID are required.", true);
                    return;
                }

                Account newAcc;
                if ("Savings".equalsIgnoreCase(type)) {
                    double rate = Double.parseDouble(txtExtra1.getText().trim());
                    double minBal = Double.parseDouble(txtExtra2.getText().trim());
                    newAcc = new SavingsAccount(accNum, custId, bal, rate, minBal);
                } else {
                    double overdraft = Double.parseDouble(txtExtra1.getText().trim());
                    double fee = Double.parseDouble(txtExtra2.getText().trim());
                    newAcc = new CheckingAccount(accNum, custId, bal, overdraft, fee);
                }

                bank.createAccount(newAcc);
                refreshAccounts.run();
                showDialog(mainFrame, "Success", "Account " + accNum + " created successfully!\nType: " + type + "\nBalance: $" + bal, false);
            } catch (NumberFormatException nfe) {
                showDialog(mainFrame, "Input Error", "Please enter valid numeric values for balance and rates.", true);
            } catch (Exception ex) {
                showDialog(mainFrame, "Account Creation Error", ex.getMessage(), true);
            }
        });

        btnSearch.addActionListener(e -> {
            try {
                String accNum = txtAccNum.getText().trim();
                if (accNum.isEmpty()) {
                    showDialog(mainFrame, "Search", "Enter Account Number to search.", true);
                    return;
                }
                Account acc = bank.getAccount(accNum);
                txtCustId.setText(acc.getCustomerId());
                txtBalance.setText(String.format("%.2f", acc.getBalance()));
                choiceType.select(acc.getAccountType());

                if (acc instanceof SavingsAccount) {
                    SavingsAccount sa = (SavingsAccount) acc;
                    lblExtra1.setText("Interest Rate (%):");
                    txtExtra1.setText(String.valueOf(sa.getInterestRate()));
                    lblExtra2.setText("Min Balance ($):");
                    txtExtra2.setText(String.valueOf(sa.getMinimumBalance()));
                } else if (acc instanceof CheckingAccount) {
                    CheckingAccount ca = (CheckingAccount) acc;
                    lblExtra1.setText("Overdraft Limit ($):");
                    txtExtra1.setText(String.valueOf(ca.getOverdraftLimit()));
                    lblExtra2.setText("Tx Fee ($):");
                    txtExtra2.setText(String.valueOf(ca.getTransactionFee()));
                }

                showDialog(mainFrame, "Account Details (Polymorphic)", acc.displayAccountDetails(), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Search Result", ex.getMessage(), true);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                String accNum = txtAccNum.getText().trim();
                if (accNum.isEmpty()) {
                    showDialog(mainFrame, "Delete", "Enter Account Number to delete.", true);
                    return;
                }
                bank.deleteAccount(accNum);
                refreshAccounts.run();
                showDialog(mainFrame, "Success", "Account " + accNum + " deleted successfully.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Delete Error", ex.getMessage(), true);
            }
        });

        btnPolymorphism.addActionListener(e -> {
            try {
                String accNum = txtAccNum.getText().trim();
                if (accNum.isEmpty()) {
                    showDialog(mainFrame, "Polymorphism Demo", "Select or enter an account number first.", true);
                    return;
                }
                Account polymorphicAccount = bank.getAccount(accNum);
                String details = polymorphicAccount.displayAccountDetails();
                double interest = polymorphicAccount.calculateInterest();

                String msg = String.format("Runtime Polymorphism Execution:\nClass Type: %s\nDetails: %s\nCalculated Interest: $%.2f",
                        polymorphicAccount.getClass().getSimpleName(), details, interest);
                showDialog(mainFrame, "Polymorphism Demonstration", msg, false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnClear.addActionListener(e -> {
            txtAccNum.setText("");
            txtCustId.setText("");
            txtBalance.setText("0.00");
        });

        accountList.addItemListener(e -> {
            String sel = accountList.getSelectedItem();
            if (sel != null) {
                String[] p = sel.split("\\|");
                if (p.length > 0) {
                    txtAccNum.setText(p[0].trim());
                    btnSearch.getActionListeners()[0].actionPerformed(null);
                }
            }
        });

        listP.add(lblList, BorderLayout.NORTH);
        listP.add(accountList, BorderLayout.CENTER);
        listP.add(btnRefresh, BorderLayout.SOUTH);

        bodyP.add(formP);
        bodyP.add(listP);

        main.add(bodyP, BorderLayout.CENTER);
        return main;
    }

    private Panel buildDepositModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Deposit Module", "Deposit funds safely with input validation and multithreading options.");
        main.add(titleP, BorderLayout.NORTH);

        Panel card = new Panel(new GridLayout(4, 2, 10, 15));
        card.setBackground(COLOR_CARD_BG);

        TextField txtAcc = new TextField();
        TextField txtAmt = new TextField();
        TextField txtDesc = new TextField("Cash deposit via counter");

        card.add(new Label("Target Account Number:"));
        card.add(txtAcc);
        card.add(new Label("Deposit Amount ($):"));
        card.add(txtAmt);
        card.add(new Label("Transaction Description:"));
        card.add(txtDesc);

        Panel btnP = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        Button btnDeposit = createStyledButton("Execute Deposit", COLOR_SUCCESS);
        Button btnThreadedDeposit = createStyledButton("Execute via DepositThread (Priority 5)", COLOR_PRIMARY);

        btnDeposit.addActionListener(e -> {
            try {
                String acc = txtAcc.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                Account a = bank.getAccount(acc);
                double before = a.getBalance();
                bank.deposit(acc, amt, desc);
                double after = a.getBalance();

                showDialog(mainFrame, "Deposit Successful",
                        String.format("Successfully deposited $%.2f into Account %s.\nBefore: $%.2f | After: $%.2f", amt, acc, before, after), false);
                setStatus("Deposited $" + amt + " to " + acc);
            } catch (NumberFormatException nfe) {
                showDialog(mainFrame, "Invalid Input", "Please enter a valid numeric amount.", true);
            } catch (Exception ex) {
                showDialog(mainFrame, "Deposit Error", ex.getMessage(), true);
            }
        });

        btnThreadedDeposit.addActionListener(e -> {
            try {
                String acc = txtAcc.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                DepositThread dt = new DepositThread(bank, acc, amt, desc);
                dt.start();
                dt.join();

                if (dt.isSuccess()) {
                    showDialog(mainFrame, "Threaded Deposit Complete", dt.getLogResult(), false);
                } else {
                    showDialog(mainFrame, "Threaded Deposit Failed", dt.getLogResult(), true);
                }
            } catch (Exception ex) {
                showDialog(mainFrame, "Thread Error", ex.getMessage(), true);
            }
        });

        btnP.add(btnDeposit);
        btnP.add(btnThreadedDeposit);

        Panel wrapper = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        wrapper.add(card);

        main.add(wrapper, BorderLayout.CENTER);
        main.add(btnP, BorderLayout.SOUTH);
        return main;
    }

    private Panel buildWithdrawalModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Withdrawal Module", "Withdraw funds safely with minimum balance and overdraft checks.");
        main.add(titleP, BorderLayout.NORTH);

        Panel card = new Panel(new GridLayout(4, 2, 10, 15));
        card.setBackground(COLOR_CARD_BG);

        TextField txtAcc = new TextField();
        TextField txtAmt = new TextField();
        TextField txtDesc = new TextField("ATM withdrawal");

        card.add(new Label("Source Account Number:"));
        card.add(txtAcc);
        card.add(new Label("Withdrawal Amount ($):"));
        card.add(txtAmt);
        card.add(new Label("Transaction Description:"));
        card.add(txtDesc);

        Panel btnP = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        Button btnWithdraw = createStyledButton("Execute Withdrawal", COLOR_DANGER);
        Button btnThreadedWithdraw = createStyledButton("Execute via WithdrawalThread (Priority 5)", COLOR_PRIMARY);

        btnWithdraw.addActionListener(e -> {
            try {
                String acc = txtAcc.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                Account a = bank.getAccount(acc);
                double before = a.getBalance();
                bank.withdraw(acc, amt, desc);
                double after = a.getBalance();

                showDialog(mainFrame, "Withdrawal Successful",
                        String.format("Successfully withdrew $%.2f from Account %s.\nBefore: $%.2f | After: $%.2f", amt, acc, before, after), false);
                setStatus("Withdrew $" + amt + " from " + acc);
            } catch (NumberFormatException nfe) {
                showDialog(mainFrame, "Invalid Input", "Please enter a valid numeric amount.", true);
            } catch (InsufficientBalanceException ibe) {
                showDialog(mainFrame, "Insufficient Balance", ibe.getMessage(), true);
            } catch (Exception ex) {
                showDialog(mainFrame, "Withdrawal Error", ex.getMessage(), true);
            }
        });

        btnThreadedWithdraw.addActionListener(e -> {
            try {
                String acc = txtAcc.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                WithdrawalThread wt = new WithdrawalThread(bank, acc, amt, desc);
                wt.start();
                wt.join();

                if (wt.isSuccess()) {
                    showDialog(mainFrame, "Threaded Withdrawal Complete", wt.getLogResult(), false);
                } else {
                    showDialog(mainFrame, "Threaded Withdrawal Failed", wt.getLogResult(), true);
                }
            } catch (Exception ex) {
                showDialog(mainFrame, "Thread Error", ex.getMessage(), true);
            }
        });

        btnP.add(btnWithdraw);
        btnP.add(btnThreadedWithdraw);

        Panel wrapper = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        wrapper.add(card);

        main.add(wrapper, BorderLayout.CENTER);
        main.add(btnP, BorderLayout.SOUTH);
        return main;
    }
    private Panel buildTransferModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Fund Transfer Module", "Atomic transfers with dead-lock safe synchronization and JDBC transactions.");
        main.add(titleP, BorderLayout.NORTH);

        Panel card = new Panel(new GridLayout(5, 2, 10, 15));
        card.setBackground(COLOR_CARD_BG);

        TextField txtSender = new TextField();
        TextField txtReceiver = new TextField();
        TextField txtAmt = new TextField();
        TextField txtDesc = new TextField("Direct account transfer");

        card.add(new Label("Sender Account Number:"));
        card.add(txtSender);
        card.add(new Label("Receiver Account Number:"));
        card.add(txtReceiver);
        card.add(new Label("Transfer Amount ($):"));
        card.add(txtAmt);
        card.add(new Label("Transaction Description:"));
        card.add(txtDesc);

        Panel btnP = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        Button btnTransfer = createStyledButton("Synchronized In-Memory Transfer", COLOR_SUCCESS);
        Button btnThreadedTransfer = createStyledButton("High-Priority TransferThread (Priority 10)", COLOR_PRIMARY);
        Button btnJdbcTransfer = createStyledButton("JDBC Transaction (Commit/Rollback)", new Color(142, 68, 173));

        btnTransfer.addActionListener(e -> {
            try {
                String s = txtSender.getText().trim();
                String r = txtReceiver.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                bank.transfer(s, r, amt, desc);
                showDialog(mainFrame, "Transfer Successful",
                        String.format("Transferred $%.2f from %s to %s successfully!\nSender Bal: $%.2f | Receiver Bal: $%.2f",
                                amt, s, r, bank.getAccount(s).getBalance(), bank.getAccount(r).getBalance()), false);
            } catch (NumberFormatException nfe) {
                showDialog(mainFrame, "Invalid Input", "Please enter a valid numeric amount.", true);
            } catch (Exception ex) {
                showDialog(mainFrame, "Transfer Error", ex.getMessage(), true);
            }
        });

        btnThreadedTransfer.addActionListener(e -> {
            try {
                String s = txtSender.getText().trim();
                String r = txtReceiver.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                TransferThread tt = new TransferThread(bank, s, r, amt, desc);
                tt.start();
                tt.join();

                if (tt.isSuccess()) {
                    showDialog(mainFrame, "Threaded Transfer Complete", tt.getLogResult(), false);
                } else {
                    showDialog(mainFrame, "Threaded Transfer Failed", tt.getLogResult(), true);
                }
            } catch (Exception ex) {
                showDialog(mainFrame, "Thread Error", ex.getMessage(), true);
            }
        });

        btnJdbcTransfer.addActionListener(e -> {
            try {
                String s = txtSender.getText().trim();
                String r = txtReceiver.getText().trim();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                String desc = txtDesc.getText().trim();

                boolean ok = databaseManager.transferFundsTransactional(s, r, amt, desc);
                if (ok) {
                    showDialog(mainFrame, "JDBC Transaction Committed",
                            String.format("ACID Transaction successfully committed in MySQL!\n$%.2f moved from %s to %s", amt, s, r), false);
                }
            } catch (Exception ex) {
                showDialog(mainFrame, "JDBC Transaction Rollback",
                        "Transaction Rolled Back due to error:\n" + ex.getMessage(), true);
            }
        });

        btnP.add(btnTransfer);
        btnP.add(btnThreadedTransfer);
        btnP.add(btnJdbcTransfer);

        Panel wrapper = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        wrapper.add(card);

        main.add(wrapper, BorderLayout.CENTER);
        main.add(btnP, BorderLayout.SOUTH);
        return main;
    }

    private Panel buildLoanModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Loan Management Module", "Create, search, approve/reject loan applications with EMI calculations.");
        main.add(titleP, BorderLayout.NORTH);

        Panel bodyP = new Panel(new GridLayout(1, 2, 12, 12));

        Panel formP = new Panel(new BorderLayout());
        formP.setBackground(COLOR_CARD_BG);

        Panel grid = new Panel(new GridLayout(7, 2, 8, 8));
        TextField txtLoanId = new TextField();
        TextField txtCustId = new TextField();
        Choice choiceType = new Choice();
        choiceType.add("Home");
        choiceType.add("Personal");
        choiceType.add("Education");
        choiceType.add("Business");

        TextField txtAmt = new TextField("100000.00");
        TextField txtRate = new TextField("8.5");
        TextField txtDur = new TextField("24");
        Choice choiceStatus = new Choice();
        choiceStatus.add("PENDING");
        choiceStatus.add("APPROVED");
        choiceStatus.add("REJECTED");
        choiceStatus.add("CLOSED");

        grid.add(new Label("Loan ID:"));
        grid.add(txtLoanId);
        grid.add(new Label("Customer ID:"));
        grid.add(txtCustId);
        grid.add(new Label("Loan Type:"));
        grid.add(choiceType);
        grid.add(new Label("Principal Amount ($):"));
        grid.add(txtAmt);
        grid.add(new Label("Interest Rate (% p.a.):"));
        grid.add(txtRate);
        grid.add(new Label("Duration (Months):"));
        grid.add(txtDur);
        grid.add(new Label("Application Status:"));
        grid.add(choiceStatus);

        Panel formBtnP = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        Button btnAdd = createStyledButton("Apply Loan", COLOR_SUCCESS);
        Button btnSearch = createStyledButton("Search", COLOR_SECONDARY);
        Button btnApprove = createStyledButton("Approve", new Color(39, 174, 96));
        Button btnReject = createStyledButton("Reject", COLOR_DANGER);
        Button btnCalcEmi = createStyledButton("Calculate EMI", COLOR_PRIMARY);
        Button btnDelete = createStyledButton("Delete", Color.GRAY);

        formBtnP.add(btnAdd);
        formBtnP.add(btnSearch);
        formBtnP.add(btnApprove);
        formBtnP.add(btnReject);
        formBtnP.add(btnCalcEmi);
        formBtnP.add(btnDelete);

        formP.add(grid, BorderLayout.CENTER);
        formP.add(formBtnP, BorderLayout.SOUTH);

        Panel listP = new Panel(new BorderLayout());
        listP.setBackground(COLOR_CARD_BG);
        Label lblList = new Label("Loan Portfolio Directory");
        lblList.setFont(new Font("Arial", Font.BOLD, 12));
        java.awt.List loanList = new java.awt.List(15);
        Button btnRefresh = createStyledButton("Refresh Loan List", COLOR_PRIMARY);

        Runnable refreshLoans = () -> {
            loanList.removeAll();
            java.util.List<Loan> all = bank.getAllLoans();
            for (Loan l : all) {
                loanList.add(String.format("%s | Cust: %s | %s | $%.2f | %s",
                        l.getLoanId(), l.getCustomerId(), l.getLoanType(), l.getAmount(), l.getStatus()));
            }
        };
        refreshLoans.run();

        btnRefresh.addActionListener(e -> refreshLoans.run());

        btnAdd.addActionListener(e -> {
            try {
                String lid = txtLoanId.getText().trim();
                String cid = txtCustId.getText().trim();
                String type = choiceType.getSelectedItem();
                double amt = Double.parseDouble(txtAmt.getText().trim());
                double rate = Double.parseDouble(txtRate.getText().trim());
                int dur = Integer.parseInt(txtDur.getText().trim());
                String status = choiceStatus.getSelectedItem();

                Loan loan = new Loan(lid, cid, type, amt, rate, dur, status);
                bank.addLoan(loan);
                refreshLoans.run();
                showDialog(mainFrame, "Loan Created", "Loan application registered!\nMonthly EMI: $" + loan.calculateEmi(), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnSearch.addActionListener(e -> {
            try {
                String lid = txtLoanId.getText().trim();
                if (lid.isEmpty()) {
                    showDialog(mainFrame, "Search", "Enter Loan ID to search.", true);
                    return;
                }
                Loan l = bank.getLoan(lid);
                txtCustId.setText(l.getCustomerId());
                choiceType.select(l.getLoanType());
                txtAmt.setText(String.valueOf(l.getAmount()));
                txtRate.setText(String.valueOf(l.getInterestRate()));
                txtDur.setText(String.valueOf(l.getDuration()));
                choiceStatus.select(l.getStatus());

                showDialog(mainFrame, "Loan Details", l.toString(), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Search Result", ex.getMessage(), true);
            }
        });

        btnApprove.addActionListener(e -> {
            try {
                String lid = txtLoanId.getText().trim();
                bank.approveLoan(lid);
                choiceStatus.select("APPROVED");
                refreshLoans.run();
                showDialog(mainFrame, "Loan Approved", "Loan " + lid + " status changed to APPROVED.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnReject.addActionListener(e -> {
            try {
                String lid = txtLoanId.getText().trim();
                bank.rejectLoan(lid);
                choiceStatus.select("REJECTED");
                refreshLoans.run();
                showDialog(mainFrame, "Loan Rejected", "Loan " + lid + " status changed to REJECTED.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnCalcEmi.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(txtAmt.getText().trim());
                double rate = Double.parseDouble(txtRate.getText().trim());
                int dur = Integer.parseInt(txtDur.getText().trim());

                Loan temp = new Loan("TEMP", "TEMP", "Personal", amt, rate, dur, "CALC");
                double emi = temp.calculateEmi();
                double totalRepayment = emi * dur;
                double totalInterest = totalRepayment - amt;

                showDialog(mainFrame, "EMI Calculator Result",
                        String.format("Principal: $%.2f\nAnnual Rate: %.2f%%\nDuration: %d months\n\nMonthly EMI: $%.2f\nTotal Interest: $%.2f\nTotal Repayment: $%.2f",
                                amt, rate, dur, emi, totalInterest, totalRepayment), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Calculation Error", "Please provide valid numbers for Amount, Rate, and Duration.", true);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                String lid = txtLoanId.getText().trim();
                bank.deleteLoan(lid);
                refreshLoans.run();
                showDialog(mainFrame, "Success", "Loan " + lid + " removed.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        loanList.addItemListener(e -> {
            String sel = loanList.getSelectedItem();
            if (sel != null) {
                String[] p = sel.split("\\|");
                if (p.length > 0) {
                    txtLoanId.setText(p[0].trim());
                    btnSearch.getActionListeners()[0].actionPerformed(null);
                }
            }
        });

        listP.add(lblList, BorderLayout.NORTH);
        listP.add(loanList, BorderLayout.CENTER);
        listP.add(btnRefresh, BorderLayout.SOUTH);

        bodyP.add(formP);
        bodyP.add(listP);

        main.add(bodyP, BorderLayout.CENTER);
        return main;
    }

    private Panel buildEmployeeModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Employee Management Module", "Maintain bank staff records, departments, and payroll details.");
        main.add(titleP, BorderLayout.NORTH);

        Panel bodyP = new Panel(new GridLayout(1, 2, 12, 12));

        Panel formP = new Panel(new BorderLayout());
        formP.setBackground(COLOR_CARD_BG);

        Panel grid = new Panel(new GridLayout(5, 2, 8, 10));
        TextField txtEmpId = new TextField();
        TextField txtName = new TextField();
        TextField txtDept = new TextField();
        TextField txtPos = new TextField();
        TextField txtSalary = new TextField();

        grid.add(new Label("Employee ID:"));
        grid.add(txtEmpId);
        grid.add(new Label("Full Name:"));
        grid.add(txtName);
        grid.add(new Label("Department:"));
        grid.add(txtDept);
        grid.add(new Label("Position / Role:"));
        grid.add(txtPos);
        grid.add(new Label("Monthly Salary ($):"));
        grid.add(txtSalary);

        Panel formBtnP = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        Button btnAdd = createStyledButton("Add", COLOR_SUCCESS);
        Button btnSearch = createStyledButton("Search", COLOR_SECONDARY);
        Button btnUpdate = createStyledButton("Update", new Color(211, 84, 0));
        Button btnDelete = createStyledButton("Delete", COLOR_DANGER);
        Button btnClear = createStyledButton("Clear", Color.GRAY);

        formBtnP.add(btnAdd);
        formBtnP.add(btnSearch);
        formBtnP.add(btnUpdate);
        formBtnP.add(btnDelete);
        formBtnP.add(btnClear);

        formP.add(grid, BorderLayout.CENTER);
        formP.add(formBtnP, BorderLayout.SOUTH);

        Panel listP = new Panel(new BorderLayout());
        listP.setBackground(COLOR_CARD_BG);
        Label lblList = new Label("Staff Directory");
        lblList.setFont(new Font("Arial", Font.BOLD, 12));
        java.awt.List empList = new java.awt.List(15);
        Button btnRefresh = createStyledButton("Refresh Employee List", COLOR_PRIMARY);

        Runnable refreshEmployees = () -> {
            empList.removeAll();
            java.util.List<Employee> all = bank.getAllEmployees();
            for (Employee e : all) {
                empList.add(String.format("%s | %s | %s | %s | $%.2f",
                        e.getEmployeeId(), e.getName(), e.getDepartment(), e.getPosition(), e.getSalary()));
            }
        };
        refreshEmployees.run();

        btnRefresh.addActionListener(e -> refreshEmployees.run());

        btnAdd.addActionListener(e -> {
            try {
                String id = txtEmpId.getText().trim();
                String name = txtName.getText().trim();
                String dept = txtDept.getText().trim();
                String pos = txtPos.getText().trim();
                double sal = Double.parseDouble(txtSalary.getText().trim());

                Employee emp = new Employee(id, name, dept, pos, sal);
                bank.addEmployee(emp);
                refreshEmployees.run();
                showDialog(mainFrame, "Success", "Employee " + name + " added successfully.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnSearch.addActionListener(e -> {
            try {
                String id = txtEmpId.getText().trim();
                if (id.isEmpty()) {
                    showDialog(mainFrame, "Search", "Enter Employee ID.", true);
                    return;
                }
                Employee emp = bank.getEmployee(id);
                txtName.setText(emp.getName());
                txtDept.setText(emp.getDepartment());
                txtPos.setText(emp.getPosition());
                txtSalary.setText(String.format("%.2f", emp.getSalary()));
                showDialog(mainFrame, "Employee Details", emp.toString(), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Search Result", ex.getMessage(), true);
            }
        });

        btnUpdate.addActionListener(e -> {
            try {
                String id = txtEmpId.getText().trim();
                Employee emp = new Employee(id, txtName.getText().trim(), txtDept.getText().trim(),
                        txtPos.getText().trim(), Double.parseDouble(txtSalary.getText().trim()));
                bank.updateEmployee(emp);
                refreshEmployees.run();
                showDialog(mainFrame, "Success", "Employee updated successfully.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                String id = txtEmpId.getText().trim();
                bank.deleteEmployee(id);
                refreshEmployees.run();
                showDialog(mainFrame, "Success", "Employee deleted.", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnClear.addActionListener(e -> {
            txtEmpId.setText("");
            txtName.setText("");
            txtDept.setText("");
            txtPos.setText("");
            txtSalary.setText("");
        });

        empList.addItemListener(e -> {
            String sel = empList.getSelectedItem();
            if (sel != null) {
                String[] p = sel.split("\\|");
                if (p.length > 0) {
                    txtEmpId.setText(p[0].trim());
                    btnSearch.getActionListeners()[0].actionPerformed(null);
                }
            }
        });

        listP.add(lblList, BorderLayout.NORTH);
        listP.add(empList, BorderLayout.CENTER);
        listP.add(btnRefresh, BorderLayout.SOUTH);

        bodyP.add(formP);
        bodyP.add(listP);

        main.add(bodyP, BorderLayout.CENTER);
        return main;
    }
    private Panel buildTransactionModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Transaction History & Audit Ledger", "Comprehensive audit trails using ArrayList<Transaction> and Iterator.");
        main.add(titleP, BorderLayout.NORTH);

        Panel topFilter = new Panel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topFilter.setBackground(COLOR_CARD_BG);
        Label lblAcc = new Label("Filter by Account Number (or leave empty for all):");
        TextField txtFilterAcc = new TextField(12);
        Button btnFilter = createStyledButton("Filter Transactions", COLOR_SECONDARY);
        Button btnViewAll = createStyledButton("View All Transactions", COLOR_PRIMARY);

        topFilter.add(lblAcc);
        topFilter.add(txtFilterAcc);
        topFilter.add(btnFilter);
        topFilter.add(btnViewAll);

        TextArea txArea = new TextArea(20, 90);
        txArea.setEditable(false);
        txArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        Runnable loadTx = () -> {
            txArea.setText("");
            String filter = txtFilterAcc.getText().trim();
            java.util.List<Transaction> list;
            if (filter.isEmpty()) {
                list = bank.getAllTransactions();
            } else {
                list = bank.getTransactionsByAccount(filter);
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-14s | %-12s | %-14s | %-12s | %-20s | %s\n",
                    "TX ID", "ACCOUNT", "TYPE", "AMOUNT ($)", "DATE/TIME", "DESCRIPTION"));
            sb.append("----------------------------------------------------------------------------------------------------\n");
            for (Transaction t : list) {
                sb.append(String.format("%-14s | %-12s | %-14s | %-12.2f | %-20s | %s\n",
                        t.getTransactionId(), t.getAccountNumber(), t.getTransactionType(),
                        t.getAmount(), t.getTransactionDate(), t.getDescription()));
            }
            if (list.isEmpty()) {
                sb.append("No transaction records found.\n");
            }
            txArea.setText(sb.toString());
        };
        loadTx.run();

        btnFilter.addActionListener(e -> loadTx.run());
        btnViewAll.addActionListener(e -> {
            txtFilterAcc.setText("");
            loadTx.run();
        });

        main.add(topFilter, BorderLayout.NORTH);
        main.add(txArea, BorderLayout.CENTER);
        return main;
    }

    private Panel buildFileModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("File Storage Module (Java I/O Streams)", "Demonstrates BufferedReader, BufferedWriter, FileReader, and FileWriter.");
        main.add(titleP, BorderLayout.NORTH);

        Panel controlP = new Panel(new GridLayout(2, 1, 10, 10));
        controlP.setBackground(COLOR_CARD_BG);

        Panel exportP = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Button btnExportAll = createStyledButton("Export All to .txt Files", COLOR_SUCCESS);
        Button btnImportAll = createStyledButton("Import All from .txt Files", COLOR_SECONDARY);

        exportP.add(btnExportAll);
        exportP.add(btnImportAll);

        Panel searchP = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Choice choiceFile = new Choice();
        choiceFile.add("customers.txt");
        choiceFile.add("accounts.txt");
        choiceFile.add("transactions.txt");
        choiceFile.add("loans.txt");
        choiceFile.add("employees.txt");

        TextField txtSearchKey = new TextField(15);
        Button btnSearchFile = createStyledButton("Search in File", COLOR_PRIMARY);

        searchP.add(new Label("Select File:"));
        searchP.add(choiceFile);
        searchP.add(new Label("Keyword:"));
        searchP.add(txtSearchKey);
        searchP.add(btnSearchFile);

        controlP.add(exportP);
        controlP.add(searchP);

        TextArea outputArea = new TextArea(15, 80);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnExportAll.addActionListener(e -> {
            try {
                fileManager.saveCustomers(bank.getAllCustomers());
                fileManager.saveAccounts(bank.getAllAccounts());
                fileManager.saveTransactions(bank.getAllTransactions());
                fileManager.saveLoans(bank.getAllLoans());
                fileManager.saveEmployees(bank.getAllEmployees());

                outputArea.setText("Successfully exported in-memory collections to:\n" +
                        "- data/customers.txt\n" +
                        "- data/accounts.txt\n" +
                        "- data/transactions.txt\n" +
                        "- data/loans.txt\n" +
                        "- data/employees.txt\n");
                showDialog(mainFrame, "Export Success", "All records exported to data/*.txt files successfully!", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Export Error", ex.getMessage(), true);
            }
        });

        btnImportAll.addActionListener(e -> {
            try {
                java.util.List<Customer> custs = fileManager.loadCustomers();
                java.util.List<Account> accs = fileManager.loadAccounts();
                java.util.List<Loan> loans = fileManager.loadLoans();
                java.util.List<Employee> emps = fileManager.loadEmployees();
                java.util.List<Transaction> txs = fileManager.loadTransactions();

                StringBuilder sb = new StringBuilder("Loaded from text files:\n");
                sb.append(String.format("Customers: %d records\n", custs.size()));
                sb.append(String.format("Accounts: %d records\n", accs.size()));
                sb.append(String.format("Loans: %d records\n", loans.size()));
                sb.append(String.format("Employees: %d records\n", emps.size()));
                sb.append(String.format("Transactions: %d records\n", txs.size()));

                outputArea.setText(sb.toString());
                showDialog(mainFrame, "Import Success", sb.toString(), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Import Error", ex.getMessage(), true);
            }
        });

        btnSearchFile.addActionListener(e -> {
            try {
                String fn = choiceFile.getSelectedItem();
                String kw = txtSearchKey.getText().trim();
                if (kw.isEmpty()) {
                    showDialog(mainFrame, "Search Error", "Please enter a keyword to search in " + fn, true);
                    return;
                }

                java.util.List<String> results = fileManager.searchInFile(fn, kw);
                StringBuilder sb = new StringBuilder("Search results in " + fn + " for keyword '" + kw + "':\n");
                for (String r : results) {
                    sb.append(r).append("\n");
                }
                if (results.isEmpty()) {
                    sb.append("No matching records found in file.\n");
                }
                outputArea.setText(sb.toString());
            } catch (Exception ex) {
                showDialog(mainFrame, "Search Error", ex.getMessage(), true);
            }
        });

        main.add(controlP, BorderLayout.NORTH);
        main.add(outputArea, BorderLayout.CENTER);
        return main;
    }

    private Panel buildSerializationModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Object Serialization Module", "Save & restore entire bank object graph via ObjectOutputStream / ObjectInputStream.");
        main.add(titleP, BorderLayout.NORTH);

        Panel card = new Panel(new GridLayout(3, 1, 10, 15));
        card.setBackground(COLOR_CARD_BG);

        Label lblInfo = new Label("Binary Object Serialization target: data/bank_data.ser", Label.CENTER);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 13));

        Panel btnP = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Button btnSaveState = createStyledButton("Serialize & Save Bank State (data/bank_data.ser)", COLOR_SUCCESS);
        Button btnLoadState = createStyledButton("Deserialize & Restore Bank State", COLOR_PRIMARY);

        btnP.add(btnSaveState);
        btnP.add(btnLoadState);

        card.add(lblInfo);
        card.add(btnP);

        TextArea serLogArea = new TextArea(12, 80);
        serLogArea.setEditable(false);
        serLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnSaveState.addActionListener(e -> {
            try {
                serializationManager.saveBankState(bank, "data/bank_data.ser");
                serLogArea.setText("Successfully serialized Bank object graph to 'data/bank_data.ser'.\n" +
                        "Customers Saved: " + bank.getAllCustomers().size() + "\n" +
                        "Accounts Saved: " + bank.getAllAccounts().size() + "\n" +
                        "Loans Saved: " + bank.getAllLoans().size() + "\n" +
                        "Employees Saved: " + bank.getAllEmployees().size() + "\n" +
                        "Transactions Saved: " + bank.getAllTransactions().size() + "\n");
                showDialog(mainFrame, "Serialization Success", "Bank state serialized and saved to 'data/bank_data.ser'!", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Serialization Error", ex.getMessage(), true);
            }
        });

        btnLoadState.addActionListener(e -> {
            try {
                Bank restored = serializationManager.loadBankState("data/bank_data.ser");
                this.bank = restored;
                serLogArea.setText("Successfully deserialized Bank object graph from 'data/bank_data.ser'!\n" +
                        "Restored Customers: " + bank.getAllCustomers().size() + "\n" +
                        "Restored Accounts: " + bank.getAllAccounts().size() + "\n" +
                        "Restored Loans: " + bank.getAllLoans().size() + "\n" +
                        "Restored Employees: " + bank.getAllEmployees().size() + "\n" +
                        "Restored Transactions: " + bank.getAllTransactions().size() + "\n");
                showDialog(mainFrame, "Deserialization Success", "Bank state restored into memory successfully!", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Deserialization Error", ex.getMessage(), true);
            }
        });

        main.add(card, BorderLayout.NORTH);
        main.add(serLogArea, BorderLayout.CENTER);
        return main;
    }
    private Panel buildDatabaseModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("JDBC & MySQL Database Management", "Perform complete CRUD and ACID Transactions using PreparedStatement.");
        main.add(titleP, BorderLayout.NORTH);

        Panel configP = new Panel(new GridLayout(3, 4, 8, 8));
        configP.setBackground(COLOR_CARD_BG);

        TextField txtDbHost = new TextField("localhost");
        TextField txtDbPort = new TextField("3306");
        TextField txtDbName = new TextField("smart_bank");
        TextField txtDbUser = new TextField("root");
        TextField txtDbPass = new TextField("root");
        txtDbPass.setEchoChar('*');

        configP.add(new Label("Host:"));
        configP.add(txtDbHost);
        configP.add(new Label("Port:"));
        configP.add(txtDbPort);
        configP.add(new Label("Database:"));
        configP.add(txtDbName);
        configP.add(new Label("Username:"));
        configP.add(txtDbUser);
        configP.add(new Label("Password:"));
        configP.add(txtDbPass);

        Panel btnP = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        Button btnTestConn = createStyledButton("Test Connection", COLOR_SECONDARY);
        Button btnInitTables = createStyledButton("Init Schema / Tables", COLOR_PRIMARY);
        Button btnPushToDb = createStyledButton("Sync Collections -> MySQL", COLOR_SUCCESS);
        Button btnPullFromDb = createStyledButton("Load MySQL -> Memory", new Color(142, 68, 173));

        btnP.add(btnTestConn);
        btnP.add(btnInitTables);
        btnP.add(btnPushToDb);
        btnP.add(btnPullFromDb);

        Panel topWrapper = new Panel(new BorderLayout());
        topWrapper.add(configP, BorderLayout.CENTER);
        topWrapper.add(btnP, BorderLayout.SOUTH);

        TextArea dbLogArea = new TextArea(14, 80);
        dbLogArea.setEditable(false);
        dbLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnTestConn.addActionListener(e -> {
            try {
                databaseManager = new DatabaseManager(
                        txtDbHost.getText().trim(),
                        Integer.parseInt(txtDbPort.getText().trim()),
                        txtDbName.getText().trim(),
                        txtDbUser.getText().trim(),
                        txtDbPass.getText().trim()
                );
                boolean ok = databaseManager.testConnection();
                if (ok) {
                    dbLogArea.setText("Connection test SUCCESSFUL! MySQL server is reachable.\n");
                    showDialog(mainFrame, "Connection Success", "Connected to MySQL successfully!", false);
                } else {
                    dbLogArea.setText("Connection test FAILED. Please ensure MySQL server is running on port " + txtDbPort.getText() + ".\n");
                    showDialog(mainFrame, "Connection Failed", "Unable to connect to MySQL database.\nMake sure MySQL is running with database 'smart_bank'.", true);
                }
            } catch (Exception ex) {
                dbLogArea.setText("Connection Error: " + ex.getMessage() + "\n");
                showDialog(mainFrame, "Error", ex.getMessage(), true);
            }
        });

        btnInitTables.addActionListener(e -> {
            try {
                databaseManager = new DatabaseManager(
                        txtDbHost.getText().trim(),
                        Integer.parseInt(txtDbPort.getText().trim()),
                        txtDbName.getText().trim(),
                        txtDbUser.getText().trim(),
                        txtDbPass.getText().trim()
                );
                databaseManager.initDatabase();
                dbLogArea.setText("Database tables verified / created:\n- customers\n- accounts\n- transactions\n- loans\n- employees\n");
                showDialog(mainFrame, "Schema Initialized", "All required database tables created successfully!", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Database Error", ex.getMessage(), true);
            }
        });

        btnPushToDb.addActionListener(e -> {
            try {
                databaseManager = new DatabaseManager(
                        txtDbHost.getText().trim(),
                        Integer.parseInt(txtDbPort.getText().trim()),
                        txtDbName.getText().trim(),
                        txtDbUser.getText().trim(),
                        txtDbPass.getText().trim()
                );
                for (Customer c : bank.getAllCustomers()) {
                    try { databaseManager.insertCustomer(c); } catch (Exception ignored) { databaseManager.updateCustomer(c); }
                }
                for (Account a : bank.getAllAccounts()) {
                    try { databaseManager.insertAccount(a); } catch (Exception ignored) { databaseManager.updateAccount(a); }
                }
                for (Loan l : bank.getAllLoans()) {
                    try { databaseManager.insertLoan(l); } catch (Exception ignored) { databaseManager.updateLoan(l); }
                }
                for (Employee emp : bank.getAllEmployees()) {
                    try { databaseManager.insertEmployee(emp); } catch (Exception ignored) { databaseManager.updateEmployee(emp); }
                }
                dbLogArea.setText("Successfully synchronized in-memory records to MySQL database tables.\n");
                showDialog(mainFrame, "Sync Success", "Data synced to MySQL database successfully!", false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Sync Error", ex.getMessage(), true);
            }
        });

        btnPullFromDb.addActionListener(e -> {
            try {
                databaseManager = new DatabaseManager(
                        txtDbHost.getText().trim(),
                        Integer.parseInt(txtDbPort.getText().trim()),
                        txtDbName.getText().trim(),
                        txtDbUser.getText().trim(),
                        txtDbPass.getText().trim()
                );
                java.util.List<Customer> cList = databaseManager.getAllCustomers();
                java.util.List<Account> aList = databaseManager.getAllAccounts();
                java.util.List<Loan> lList = databaseManager.getAllLoans();
                java.util.List<Employee> eList = databaseManager.getAllEmployees();

                StringBuilder sb = new StringBuilder("Records fetched from MySQL:\n");
                sb.append("Customers: ").append(cList.size()).append("\n");
                sb.append("Accounts: ").append(aList.size()).append("\n");
                sb.append("Loans: ").append(lList.size()).append("\n");
                sb.append("Employees: ").append(eList.size()).append("\n");

                dbLogArea.setText(sb.toString());
                showDialog(mainFrame, "Load Success", sb.toString(), false);
            } catch (Exception ex) {
                showDialog(mainFrame, "Load Error", ex.getMessage(), true);
            }
        });

        main.add(topWrapper, BorderLayout.NORTH);
        main.add(dbLogArea, BorderLayout.CENTER);
        return main;
    }

    private Panel buildConcurrencySimulatorModule() {
        Panel main = new Panel(new BorderLayout(10, 10));
        main.setBackground(COLOR_BG);

        Panel titleP = createModuleHeader("Multithreading & Concurrency Stress Test Simulator",
                "Simulates 6 concurrent operations with thread priorities (MAX/NORM/MIN) and synchronization.");
        main.add(titleP, BorderLayout.NORTH);

        Panel topCard = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        topCard.setBackground(COLOR_CARD_BG);

        Label lblAcc1 = new Label("Account 1:");
        TextField txtSimAcc1 = new TextField("A1001", 10);
        Label lblAcc2 = new Label("Account 2:");
        TextField txtSimAcc2 = new TextField("A1002", 10);
        Button btnRunSim = createStyledButton("RUN CONCURRENCY STRESS TEST", COLOR_SUCCESS);

        topCard.add(lblAcc1);
        topCard.add(txtSimAcc1);
        topCard.add(lblAcc2);
        topCard.add(txtSimAcc2);
        topCard.add(btnRunSim);

        TextArea simOutput = new TextArea(18, 90);
        simOutput.setEditable(false);
        simOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));

        btnRunSim.addActionListener(e -> {
            String acc1 = txtSimAcc1.getText().trim();
            String acc2 = txtSimAcc2.getText().trim();

            simOutput.setText("Running multithreaded simulation...\nPlease wait...\n");
            new Thread(() -> {
                String report = transactionManager.runConcurrencyStressTest(bank, acc1, acc2);
                simOutput.setText(report);
            }).start();
        });

        main.add(topCard, BorderLayout.NORTH);
        main.add(simOutput, BorderLayout.CENTER);
        return main;
    }

    private Panel createModuleHeader(String title, String subtitle) {
        Panel p = new Panel(new BorderLayout());
        p.setBackground(COLOR_CARD_BG);
        p.setPreferredSize(new Dimension(800, 50));

        Label lblTitle = new Label("  " + title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_PRIMARY);

        Label lblSub = new Label("  " + subtitle);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(Color.GRAY);

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblSub, BorderLayout.SOUTH);
        return p;
    }

    private Button createStyledButton(String text, Color bg) {
        Button btn = new Button(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        return btn;
    }

    private void centerWindow(Window window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - window.getWidth()) / 2;
        int y = (screen.height - window.getHeight()) / 2;
        window.setLocation(x, y);
    }

    public static void showDialog(Frame parent, String title, String message, boolean isError) {
        Dialog dialog = new Dialog(parent, title, true);
        dialog.setSize(440, 240);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(COLOR_BG);
        dialog.setResizable(false);

        Panel head = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        head.setBackground(isError ? COLOR_DANGER : COLOR_PRIMARY);
        Label lblHead = new Label(isError ? "[!] " + title.toUpperCase() : "[OK] " + title.toUpperCase());
        lblHead.setForeground(Color.WHITE);
        lblHead.setFont(new Font("Arial", Font.BOLD, 12));
        head.add(lblHead);

        TextArea txt = new TextArea(message, 5, 35, TextArea.SCROLLBARS_VERTICAL_ONLY);
        txt.setEditable(false);
        txt.setBackground(Color.WHITE);
        txt.setFont(new Font("Arial", Font.PLAIN, 12));

        Panel centerP = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centerP.add(txt);

        Panel btnP = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        Button btnOk = new Button("OK");
        btnOk.setBackground(COLOR_PRIMARY);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Arial", Font.BOLD, 11));
        btnOk.addActionListener(e -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
        btnP.add(btnOk);

        dialog.add(head, BorderLayout.NORTH);
        dialog.add(centerP, BorderLayout.CENTER);
        dialog.add(btnP, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        Point pLoc = parent.getLocation();
        Dimension pSize = parent.getSize();
        dialog.setLocation(pLoc.x + (pSize.width - 440) / 2, pLoc.y + (pSize.height - 240) / 2);

        dialog.setVisible(true);
    }
}
