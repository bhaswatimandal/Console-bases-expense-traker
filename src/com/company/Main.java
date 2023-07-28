package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ExpenseTrackerApp {
    public static void main(String[] args) {
        new ExpenseTrackerApp();
    }

    private ExpenseTracker expenseTracker;
    private JFrame frame;
    private JTextField categoryField, amountField, startDateField, endDateField, expenseIdField;
    private JTextArea textArea;
    private SimpleDateFormat dateFormat;


    public ExpenseTrackerApp() {
        expenseTracker = new ExpenseTracker();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        frame = new JFrame("Expense Tracker");
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2, 5, 5));
        categoryField = new JTextField();
        amountField = new JTextField();
        startDateField = new JTextField();
        endDateField = new JTextField();
        expenseIdField = new JTextField();
        JButton addButton = new JButton("Add Expense");
        JButton modifyButton = new JButton("Modify Expense");
        JButton deleteButton = new JButton("Delete Expense");
        JButton viewAllButton = new JButton("View All Expenses");
        JButton viewByCategoryButton = new JButton("View Expenses by Category");
        JButton viewByDateRangeButton = new JButton("View Expenses by Date Range");
        JButton viewByIdButton = new JButton("View Expense by ID");
        JButton generateMonthlyReportButton = new JButton("Generate Monthly Expense Report");
        JButton generateCategoryWiseReportButton = new JButton("Generate Category-wise Expense Report");
        JButton saveToFileButton = new JButton("Save Expenses to File");
        JButton loadFromFileButton = new JButton("Load Expenses from File");
        JButton deleteLoadedButton = new JButton("Delete Loaded Expenses");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyExpense();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteExpense();
            }
        });

        viewAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllExpenses();
            }
        });

        viewByCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewExpensesByCategory();
            }
        });

        viewByDateRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewExpensesByDateRange();
            }
        });

        viewByIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewExpenseById();
            }
        });

        generateMonthlyReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMonthlyReport();
            }
        });

        generateCategoryWiseReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCategoryWiseReport();
            }
        });

        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveExpensesToFile();
            }
        });

        loadFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadExpensesFromFile();
            }
        });

        deleteLoadedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLoadedExpenses();
            }
        });

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        inputPanel.add(endDateField);
        inputPanel.add(new JLabel("Expense ID:"));
        inputPanel.add(expenseIdField);
        inputPanel.add(addButton);
        inputPanel.add(modifyButton);
        inputPanel.add(deleteButton);
        inputPanel.add(viewAllButton);
        inputPanel.add(viewByCategoryButton);
        inputPanel.add(viewByDateRangeButton);
        inputPanel.add(viewByIdButton);
        inputPanel.add(generateMonthlyReportButton);
        inputPanel.add(generateCategoryWiseReportButton);
        inputPanel.add(saveToFileButton);
        inputPanel.add(loadFromFileButton);
        inputPanel.add(deleteLoadedButton);

        textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void addExpense() {
        try {
            String category = categoryField.getText();
            double amount = Double.parseDouble(amountField.getText());
            Date date = dateFormat.parse(startDateField.getText());

            // Validate if the date is greater than the present date
            if (date.after(new Date())) {
                showErrorDialog("Invalid date! Please enter a date on or before the present date.");
                return;
            }

            expenseTracker.recordExpense(category, amount, date);
            updateTextArea();
        } catch (ParseException ex) {
            showErrorDialog("Invalid date format! Use YYYY-MM-DD");
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid amount format! Enter a valid number.");
        }
    }

    private void modifyExpense() {
        try {
            int expenseId = Integer.parseInt(expenseIdField.getText());
            Expense expense = expenseTracker.getExpenseById(expenseId);
            if (expense != null) {
                String newCategory = categoryField.getText();
                double newAmount = Double.parseDouble(amountField.getText());
                Date newDate = dateFormat.parse(startDateField.getText());

                // Validate if the date is greater than the present date
                if (newDate.after(new Date())) {
                    showErrorDialog("Invalid date! Please enter a date on or before the present date.");
                    return;
                }

                expenseTracker.modifyExpense(expenseId, newCategory, newAmount, newDate);
                updateTextArea();
            } else {
                showErrorDialog("Expense with ID " + expenseId + " not found.");
            }
        } catch (ParseException ex) {
            showErrorDialog("Invalid date format! Use YYYY-MM-DD");
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid amount format! Enter a valid number.");
        }
    }
    private void deleteExpense() {
        try {
            int expenseId = Integer.parseInt(expenseIdField.getText());
            expenseTracker.deleteExpense(expenseId);
            updateTextArea();
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid Expense ID format! Enter a valid number.");
        }
    }

    private void viewAllExpenses() {
        List<Expense> expenses = expenseTracker.getAllExpenses();
        updateTextArea(expenses);
    }

    private void viewExpensesByCategory() {
        String category = categoryField.getText();
        List<Expense> expenses = expenseTracker.getExpensesByCategory(category);
        updateTextArea(expenses);
    }

    private void viewExpensesByDateRange() {
        try {
            Date startDate = dateFormat.parse(startDateField.getText());
            Date endDate = dateFormat.parse(endDateField .getText());

            List<Expense> expenses = expenseTracker.getExpensesByDateRange(startDate, endDate);
            updateTextArea(expenses);
        } catch (ParseException ex) {
            showErrorDialog("Invalid date format! Use YYYY-MM-DD");
        }
    }

    private void viewExpenseById() {
        try {
            int expenseId = Integer.parseInt(expenseIdField.getText());
            Expense expense = expenseTracker.getExpenseById(expenseId);
            if (expense != null) {
                updateTextArea(expense);
            } else {
                showErrorDialog("Expense with ID " + expenseId + " not found.");
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid Expense ID format! Enter a valid number.");
        }
    }

    private void generateMonthlyReport() {
        try {
            int month = Integer.parseInt(categoryField.getText());
            int year = Integer.parseInt(amountField.getText());
            expenseTracker.generateMonthlyExpenseReport(month, year);
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid month or year format! Enter valid numbers.");
        }
    }

    private void generateCategoryWiseReport() {
        expenseTracker.generateCategoryWiseExpenseReport();
    }

    private void saveExpensesToFile() {
        String fileName = JOptionPane.showInputDialog(frame, "Enter the file name to save expenses:", "Save Expenses to File", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            expenseTracker.saveExpensesToFile(fileName);
            updateTextArea();
        } else {
            showErrorDialog("Please enter a valid file name.");
        }
    }

    private void loadExpensesFromFile() {
        String fileName = JOptionPane.showInputDialog(frame, "Enter the file name to load expenses:", "Load Expenses from File", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            expenseTracker.loadExpensesFromFile(fileName);
            updateTextArea();
        } else {
            showErrorDialog("Please enter a valid file name.");
        }
    }

    private void deleteLoadedExpenses() {
        int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete loaded expenses?", "Delete Loaded Expenses", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            expenseTracker.deleteLoadedExpenses();
            updateTextArea();
        }
    }

    private void updateTextArea() {
        List<Expense> expenses = expenseTracker.getAllExpenses();
        updateTextArea(expenses);
    }

    private void updateTextArea(List<Expense> expenses) {
        StringBuilder sb = new StringBuilder();
        for (Expense expense : expenses) {
            sb.append(expense).append("\n");
        }
        textArea.setText(sb.toString());
    }

    private void updateTextArea(Expense expense) {
        textArea.setText(expense.toString());
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

class Expense implements Serializable {
    private static final long serialVersionUID = 1L;

    private int expenseId;
    private String category;
    private double amount;
    private Date date;

    public Expense(int expenseId, String category, double amount, Date date) {
        this.expenseId = expenseId;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}



class ExpenseTracker implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Expense> expenses;
    private int nextExpenseId;
    private transient SimpleDateFormat dateFormat; // Transient field for SimpleDateFormat

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        nextExpenseId = 1;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void recordExpense(String category, double amount, Date date) {
        Expense expense = new Expense(nextExpenseId, category, amount, date);
        nextExpenseId++;
        expenses.add(expense);
    }

    public void modifyExpense(int expenseId, String newCategory, double newAmount, Date newDate) {
        Expense expense = getExpenseById(expenseId);
        if (expense != null) {
            expense.setCategory(newCategory);
            expense.setAmount(newAmount);
            expense.setDate(newDate);
        }
    }

    public void deleteExpense(int expenseId) {
        expenses.removeIf(expense -> expense.getExpenseId() == expenseId);
    }

    public List<Expense> getAllExpenses() {
        return expenses;
    }

    public Expense getExpenseById(int expenseId) {
        for (Expense expense : expenses) {
            if (expense.getExpenseId() == expenseId) {
                return expense;
            }
        }
        return null;
    }

    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                result.add(expense);
            }
        }
        return result;
    }

    public List<Expense> getExpensesByDateRange(Date startDate, Date endDate) {
        List<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            Date expenseDate = expense.getDate();
            if (expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
                result.add(expense);
            }
        }
        return result;
    }

    public void generateMonthlyExpenseReport(int month, int year) {
        List<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            Date expenseDate = expense.getDate();
            int expenseMonth = Integer.parseInt(dateFormat.format(expenseDate).split("-")[1]);
            int expenseYear = Integer.parseInt(dateFormat.format(expenseDate).split("-")[0]);
            if (expenseMonth == month && expenseYear == year) {
                result.add(expense);
            }
        }

        System.out.println("Monthly Expense Report for " + month + "-" + year + ":");
        for (Expense expense : result) {
            System.out.println(expense);
        }
    }

    public void generateCategoryWiseExpenseReport() {
        System.out.println("Category-wise Expense Report:");
        for (Expense expense : expenses) {
            System.out.println(expense.getCategory() + ": " + expense.getAmount());
        }
    }

    public void saveExpensesToFile(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(expenses);
            System.out.println("Expenses saved to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save expenses to file: " + fileName);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadExpensesFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                expenses = (List<Expense>) obj;
                System.out.println("Expenses loaded from file: " + fileName);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to load expenses from file: " + fileName);
        }
    }

    public void deleteLoadedExpenses() {
        expenses.clear();
        System.out.println("Loaded expenses deleted.");
    }
}

