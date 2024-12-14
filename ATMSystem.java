import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;


class BankAccount {
    private String cardNumber;
    private String pin;
    private double balance;
    private List<Transaction> transactionHistory;

    public BankAccount(String cardNumber, String pin, double initialBalance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
}

// Transaction Class (unchanged from previous implementation)
class Transaction {
    private String type;
    private double amount;
    private Date timestamp;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("%s: ₱%.2f at %s", type, amount, sdf.format(timestamp));
    }
}


class SecurityAlgorithms {
    public String encryptPin(String pin) {
   
        StringBuilder encrypted = new StringBuilder();
        for (char c : pin.toCharArray()) {
            encrypted.append((char)(c + 1));
        }
        return encrypted.toString();
    }

    public boolean validatePin(String inputPin, String storedPin) {
        return encryptPin(inputPin).equals(storedPin);
    }

    public int calculateSecurityScore(String pin) {
        // Example security scoring algorithm
        int score = 0;
        if (pin.length() == 4) score += 10;
        if (hasUniqueDigits(pin)) score += 5;
        return score;
    }

    private boolean hasUniqueDigits(String pin) {
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : pin.toCharArray()) {
            uniqueChars.add(c);
        }
        return uniqueChars.size() == pin.length();
    }
}


class TransactionAlgorithms {
    private static final double MAX_WITHDRAWAL = 10000.0;
    private static final double MIN_DEPOSIT = 100.0;
    private static final double MAX_DEPOSIT = 50000.0;

    public boolean validateWithdrawal(double currentBalance, double amount) {
        return amount > 0 && amount <= MAX_WITHDRAWAL && amount <= currentBalance;
    }

    public boolean validateDeposit(double amount) {
        return amount >= MIN_DEPOSIT && amount <= MAX_DEPOSIT;
    }

    public double calculateTransactionFee(double amount) {
        
        if (amount <= 1000) return 10.0;
        if (amount <= 5000) return 15.0;
        return 20.0;
    }
}

// Login Frame
class LoginFrame extends JFrame {
    private JTextField cardNumberField;
    private JPasswordField pinField;
    private Map<String, BankAccount> accounts;
    private SecurityAlgorithms securityAlgo;

    public LoginFrame(Map<String, BankAccount> accounts, SecurityAlgorithms securityAlgo) {
        this.accounts = accounts;
        this.securityAlgo = securityAlgo;

        setTitle("ATM Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // Card Number
        add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        add(cardNumberField);

        // PIN
        add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        add(pinField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        add(loginButton);

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(exitButton);
    }

    private void performLogin() {
        String cardNumber = cardNumberField.getText();
        String pin = new String(pinField.getPassword());

        BankAccount account = accounts.get(cardNumber);
        if (account != null && securityAlgo.validatePin(pin, account.getPin())) {
            
            ATMSystem atmSystem = new ATMSystem(account);
            atmSystem.setVisible(true);
            this.dispose(); // Close login frame
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid Card Number or PIN.", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        
        cardNumberField.setText("");
        pinField.setText("");
    }
}

// Main ATM System Class
public class ATMSystem extends JFrame {
    // Algorithmic Data Structures
    private TransactionAlgorithms transactionAlgo;
    private SecurityAlgorithms securityAlgo;
    
    // Current account
    private BankAccount currentAccount;
    
    // UI Components
    private JTextArea displayArea;
    private JButton withdrawButton, depositButton, 
                   balanceButton, transactionHistoryButton, 
                   changePinButton, logoutButton;
    
    public ATMSystem(BankAccount account) {
        // Initialize algorithmic components
        transactionAlgo = new TransactionAlgorithms();
        securityAlgo = new SecurityAlgorithms();
        
        // Set current account
        currentAccount = account;
        
      
        setTitle("Advanced ATM Banking System");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);
        
      
        displayArea.setText("Login Successful!\n" +
            "Welcome to the Advanced ATM System.\n" +
            "Security Level: " + securityAlgo.calculateSecurityScore(account.getPin()));
        
        // Action Buttons Panel
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new GridLayout(2, 3));
        
        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdraw();
            }
        });
        actionPanel.add(withdrawButton);
        
        depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deposit();
            }
        });
        actionPanel.add(depositButton);
        
        balanceButton = new JButton("Check Balance");
        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBalance();
            }
        });
        actionPanel.add(balanceButton);
        
        transactionHistoryButton = new JButton("Transaction History");
        transactionHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTransactionHistory();
            }
        });
        actionPanel.add(transactionHistoryButton);
        
        changePinButton = new JButton("Change PIN");
        changePinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePin();
            }
        });
        actionPanel.add(changePinButton);
        
        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        actionPanel.add(logoutButton);
        
        return actionPanel;
    }
    
    private void logout() {
        // Close current ATM system window
        this.dispose();
        
        // Reinitialize login frame
        Map<String, BankAccount> accounts = new HashMap<>();
        accounts.put("1234", new BankAccount("1234", 
            new SecurityAlgorithms().encryptPin("1234"), 1000.0));
        accounts.put("5678", new BankAccount("5678", 
            new SecurityAlgorithms().encryptPin("5678"), 2000.0));
        
        LoginFrame loginFrame = new LoginFrame(accounts, new SecurityAlgorithms());
        loginFrame.setVisible(true);
    }
    
    // All previous methods (withdraw, deposit, checkBalance, etc.) 
    // remain the same as in the original implementation
    
    private void changePin() {
        // Prompt for current PIN
        String currentPinStr = JOptionPane.showInputDialog("Enter current PIN:");
        
        if (currentPinStr == null || currentPinStr.trim().isEmpty()) {
            displayArea.setText("PIN change cancelled.");
            return;
        }

        // Validate current PIN
        if (!securityAlgo.validatePin(currentPinStr, currentAccount.getPin())) {
            displayArea.setText("Incorrect current PIN.");
            return;
        }

        // Prompt for new PIN
        String newPinStr = JOptionPane.showInputDialog("Enter new PIN (4 digits):");
        
        if (newPinStr == null || newPinStr.trim().isEmpty()) {
            displayArea.setText("PIN change cancelled.");
            return;
        }

        // Validate new PIN
        if (!newPinStr.matches("\\d{4}")) {
            displayArea.setText("Invalid PIN. Please enter exactly 4 digits.");
            return;
        }

        // Encrypt and update PIN
        String encryptedNewPin = securityAlgo.encryptPin(newPinStr);
        currentAccount.setPin(encryptedNewPin);

        displayArea.setText("PIN successfully changed.");
    }
    
    private void withdraw() {
        // Prompt for withdrawal amount
        String amountStr = JOptionPane.showInputDialog("Enter withdrawal amount:");
        
        if (amountStr == null || amountStr.trim().isEmpty()) {
            displayArea.setText("Withdrawal cancelled.");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            
            // Validate withdrawal
            if (!transactionAlgo.validateWithdrawal(currentAccount.getBalance(), amount)) {
                displayArea.setText("Invalid withdrawal amount or insufficient funds.");
                return;
            }
            
            // Calculate transaction fee
            double fee = transactionAlgo.calculateTransactionFee(amount);
            
            // Update balance
            currentAccount.setBalance(currentAccount.getBalance() - amount - fee);
            
            // Create transaction record
            Transaction transaction = new Transaction("Withdrawal", amount);
            currentAccount.addTransaction(transaction);
            
            // Display result
            displayArea.setText(String.format("Withdrawal successful.\nAmount: ₱%.2f\nFee: ₱%.2f\nNew Balance: ₱%.2f", 
                amount, fee, currentAccount.getBalance()));
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid amount entered.");
        }
    }
    
    private void deposit() {
        // Prompt for deposit amount
        String amountStr = JOptionPane.showInputDialog("Enter deposit amount:");
        
        if (amountStr == null || amountStr.trim().isEmpty()) {
            displayArea.setText("Deposit cancelled.");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            
            // Validate deposit
            if (!transactionAlgo.validateDeposit(amount)) {
                displayArea.setText("Invalid deposit amount.");
                return;
            }
            
            // Update balance
            currentAccount.setBalance(currentAccount.getBalance() + amount);
            
            // Create transaction record
            Transaction transaction = new Transaction("Deposit", amount);
            currentAccount.addTransaction(transaction);
            
            // Display result
            displayArea.setText(String.format("Deposit successful.\nAmount: ₱%.2f\nNew Balance: ₱%.2f", 
                amount, currentAccount.getBalance()));
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid amount entered.");
        }
    }
    
    private void checkBalance() {
        displayArea.setText("Current Balance: ₱" + currentAccount.getBalance());
    }
    
    private void showTransactionHistory() {
        List<Transaction> transactions = currentAccount.getTransactionHistory();
        
        if (transactions.isEmpty()) {
            displayArea.setText("No transaction history available.");
            return;
        }
        
        StringBuilder history = new StringBuilder("Transaction History:\n");
        for (Transaction transaction : transactions) {
            history.append(transaction.toString()).append("\n");
        }
        
        displayArea.setText(history.toString());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Initialize accounts
                Map<String, BankAccount> accounts = new HashMap<>();
                SecurityAlgorithms securityAlgo = new SecurityAlgorithms();
                
                accounts.put("1234", new BankAccount("1234", 
                    securityAlgo.encryptPin("1234"), 1000.0));
                accounts.put("5678", new BankAccount("5678", 
                    securityAlgo.encryptPin("5678"), 2000.0));
                
                // Create and show login frame
                LoginFrame loginFrame = new LoginFrame(accounts, securityAlgo);
                loginFrame.setVisible(true);
            }
        });
    }
}
