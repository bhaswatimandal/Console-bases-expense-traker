package com.company;

import java.io.*;
import java.text.*;
import java.util.*;

class ExpenseTracker {
    private List<Expense> expenses;
    private final Map<String, List<Expense>> categories;
    private String loadedFileName;
    private int nextExpenseId;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        categories = new HashMap<>();
        loadedFileName = "";
        nextExpenseId = 1;
    }

    public void recordExpense(int year, int month, int day, double amount, String category, String description) {
        Date date = Expense.parseDate(year, month, day);
        if (date != null && !date.after(new Date())) {
            Expense expense = new Expense(nextExpenseId, date, amount, category, description);
            expenses.add(expense);
            categories.computeIfAbsent(category, k -> new ArrayList<>()).add(expense);
            nextExpenseId++;
            System.out.println("Expense recorded successfully.");
        } else {
            System.out.println("Invalid date. Expense not recorded.");
        }
    }

    public void modifyExpense(int expenseId, int year, int month, int day, double amount, String category, String description) {
        Expense expense = getExpenseById(expenseId);
        if (expense != null) {
            Date newDate = Expense.parseDate(year, month, day);
            if (newDate != null && !newDate.after(new Date())) {
                expense.setDate(newDate);
                expense.setAmount(amount);
                categories.get(expense.getCategory()).remove(expense);
                expense.setCategory(category);
                categories.computeIfAbsent(category, k -> new ArrayList<>()).add(expense);
                expense.setDescription(description);
                System.out.println("Expense modified successfully.");
            } else {
                System.out.println("Invalid date. Expense not modified.");
            }
        } else {
            System.out.println("Expense not found.");
        }
    }

    public void deleteExpense(int expenseId) {
        Expense expense = getExpenseById(expenseId);
        if (expense != null) {
            expenses.remove(expense);
            categories.get(expense.getCategory()).remove(expense);
            System.out.println("Expense deleted successfully.");

            // Update the file after deleting the expense
            saveExpensesToFile(loadedFileName);
        } else {
            System.out.println("Expense not found.");
        }
    }

    public void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            for (Expense expense : expenses) {
                System.out.println(expense);
            }
        }
    }

    public void viewExpensesByCategory(String category) {
        if (categories.containsKey(category)) {
            List<Expense> categoryExpenses = categories.get(category);
            if (categoryExpenses.isEmpty()) {
                System.out.println("No expenses recorded in the category: " + category);
            } else {
                for (Expense expense : categoryExpenses) {
                    System.out.println(expense);
                }
            }
        } else {
            System.out.println("Category not found.");
        }
    }

    public void viewExpensesByDateRange(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        Date startDate = Expense.parseDate(startYear, startMonth, startDay);
        Date endDate = Expense.parseDate(endYear, endMonth, endDay);
        if (startDate != null && endDate != null) {
            List<Expense> filteredExpenses = new ArrayList<>();
            for (Expense expense : expenses) {
                Date expenseDate = expense.getDate();
                if (!expenseDate.before(startDate) && !expenseDate.after(endDate)) {
                    filteredExpenses.add(expense);
                }
            }

            if (filteredExpenses.isEmpty()) {
                System.out.println("No expenses recorded within the specified date range.");
            } else {
                for (Expense expense : filteredExpenses) {
                    System.out.println(expense);
                }
            }
        } else {
            System.out.println("Invalid date range. Please enter valid start and end dates.");
        }
    }

    public Expense getExpenseById(int expenseId) {
        for (Expense expense : expenses) {
            if (expense.getId() == expenseId) {
                return expense;
            }
        }
        return null;
    }

    public void generateMonthlyExpenseReport(int month, int year) {
        double totalExpenses = 0.0;
        for (Expense expense : expenses) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expense.getDate());
            int expenseMonth = calendar.get(Calendar.MONTH) + 1; // January is represented as 0
            int expenseYear = calendar.get(Calendar.YEAR);
            if (expenseMonth == month && expenseYear == year) {
                totalExpenses += expense.getAmount();
            }
        }
        System.out.println("Monthly Expense Report for " + month + "/" + year);
        System.out.println("Total Expenses: ₹" + totalExpenses);
    }

    public void generateCategoryWiseExpenseReport() {
        System.out.println("Category-wise Expense Report");
        for (String category : categories.keySet()) {
            double totalExpenses = 0.0;
            List<Expense> categoryExpenses = categories.get(category);
            for (Expense expense : categoryExpenses) {
                totalExpenses += expense.getAmount();
            }
            System.out.println("Category: " + category);
            System.out.println("Total Expenses: ₹" + totalExpenses);
        }
    }

    public void saveExpensesToFile(String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(expenses);
            System.out.println("Expenses saved successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while saving expenses: " + e.getMessage());
        }
    }

    public void loadExpensesFromFile(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            List<Expense> loadedExpenses = (List<Expense>) objectInputStream.readObject();

            // Assign new IDs to loaded expenses to avoid clashes with existing ones
            for (Expense expense : loadedExpenses) {
                expense.setId(nextExpenseId++);
            }

            expenses.addAll(loadedExpenses);
            updateCategoriesMap();
            loadedFileName = fileName; // Update the loaded file name
            System.out.println("Expenses loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error occurred while loading expenses: " + e.getMessage());
        }
    }

    public void deleteLoadedExpenses() {
        if (!loadedFileName.isEmpty()) {
            try {
                File file = new File(loadedFileName);
                if (file.exists()) {
                    file.delete();
                    expenses.removeIf(expense -> expense.getId() >= nextExpenseId);
                    updateCategoriesMap();
                    loadedFileName = "";
                    System.out.println("Loaded expenses deleted successfully.");
                } else {
                    System.out.println("File not found: " + loadedFileName);
                }
            } catch (Exception e) {
                System.out.println("Error occurred while deleting loaded expenses: " + e.getMessage());
            }
        } else {
            System.out.println("No expenses are currently loaded.");
        }
    }

    private void updateCategoriesMap() {
        categories.clear();
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            categories.computeIfAbsent(category, k -> new ArrayList<>()).add(expense);
        }
    }
}

class Expense implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date date;
    private double amount;
    private String category;
    private String description;

    public Expense(int id, Date date, double amount, String category, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Date parseDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(year, month - 1, day);
        try {
            return calendar.getTime();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date. Please enter a valid year, month, and day.");
            return null;
        }
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Date: " + formatDate(date) +
                ", Amount: ₹" + amount +
                ", Category: " + category +
                ", Description: " + description;
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}

class ExpenseTrackerApp {
    public static void main(String[] args) {
        ExpenseTracker expenseTracker = new ExpenseTracker();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Expense Tracker");
            System.out.println("1. Record Expense");
            System.out.println("2. Modify Expense");
            System.out.println("3. Delete Expense");
            System.out.println("4. View All Expenses");
            System.out.println("5. View Expenses by Category");
            System.out.println("6. View Expenses by Date Range");
            System.out.println("7. View Expenses by ExpenseId");
            System.out.println("8. Generate Monthly Expense Report");
            System.out.println("9. Generate Category-wise Expense Report");
            System.out.println("10. Save Expenses to File");
            System.out.println("11. Load Expenses from File");
            System.out.println("12. Delete Loaded Expenses");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    System.out.println("Exiting the application.");
                    System.exit(0);
                    break;
                case 1:
                    System.out.print("Enter the year: ");
                    int year = scanner.nextInt();
                    System.out.print("Enter the month: ");
                    int month = scanner.nextInt();
                    System.out.print("Enter the day: ");
                    int day = scanner.nextInt();
                    System.out.print("Enter the amount: ");
                    double amount = scanner.nextDouble();
                    System.out.print("Enter the category: ");
                    String category = scanner.next();
                    System.out.print("Enter the description: ");
                    scanner.nextLine();
                    String description = scanner.nextLine();
                    expenseTracker.recordExpense(year, month, day, amount, category, description);
                    break;
                case 2:
                    System.out.print("Enter the expense ID: ");
                    int expenseId = scanner.nextInt();
                    System.out.print("Enter the new year: ");
                    year = scanner.nextInt();
                    System.out.print("Enter the new month: ");
                    month = scanner.nextInt();
                    System.out.print("Enter the new day: ");
                    day = scanner.nextInt();
                    System.out.print("Enter the new amount: ");
                    amount = scanner.nextDouble();
                    System.out.print("Enter the new category: ");
                    category = scanner.next();
                    System.out.print("Enter the new description: ");
                    scanner.nextLine();
                    description = scanner.nextLine();
                    expenseTracker.modifyExpense(expenseId, year, month, day, amount, category, description);
                    break;
                case 3:
                    System.out.print("Enter the expense ID: ");
                    expenseId = scanner.nextInt();
                    expenseTracker.deleteExpense(expenseId);
                    break;
                case 4:
                    expenseTracker.viewExpenses();
                    break;
                case 5:
                    System.out.print("Enter the category: ");
                    category = scanner.next();
                    expenseTracker.viewExpensesByCategory(category);
                    break;
                case 6:
                    System.out.print("Enter the start year: ");
                    int startYear = scanner.nextInt();
                    System.out.print("Enter the start month: ");
                    int startMonth = scanner.nextInt();
                    System.out.print("Enter the start day: ");
                    int startDay = scanner.nextInt();
                    System.out.print("Enter the end year: ");
                    int endYear = scanner.nextInt();
                    System.out.print("Enter the end month: ");
                    int endMonth = scanner.nextInt();
                    System.out.print("Enter the end day: ");
                    int endDay = scanner.nextInt();
                    expenseTracker.viewExpensesByDateRange(startYear, startMonth, startDay, endYear, endMonth, endDay);
                    break;
                case 7:
                    System.out.println("Enter the expenseId: ");
                    expenseId = scanner.nextInt();
                    Expense expense = expenseTracker.getExpenseById(expenseId);
                    if (expense != null) {
                        System.out.println(expense);
                    } else {
                        System.out.println("Expense not found.");
                    }
                    break;
                case 8:
                    System.out.print("Enter the month: ");
                    month = scanner.nextInt();
                    System.out.print("Enter the year: ");
                    year = scanner.nextInt();
                    expenseTracker.generateMonthlyExpenseReport(month, year);
                    break;
                case 9:
                    expenseTracker.generateCategoryWiseExpenseReport();
                    break;
                case 10:
                    System.out.print("Enter the file name: ");
                    String saveFileName = scanner.next();
                    expenseTracker.saveExpensesToFile(saveFileName);
                    break;
                case 11:
                    System.out.print("Enter the file name: ");
                    String loadFileName = scanner.next();
                    expenseTracker.loadExpensesFromFile(loadFileName);
                    break;
                case 12:
                    expenseTracker.deleteLoadedExpenses();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            System.out.println();
        }
    }
}
