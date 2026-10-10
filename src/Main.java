import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final PayrollService payrollService = new PayrollService();
    private final SalaryReport salaryReport = new SalaryReport();

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return;
        }
        new Main().run();
    }

    private void run() {
        System.out.println("Employee Payroll");

        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = readLine("Choose an option: ");

                switch (choice) {
                    case "1":
                        addEmployee();
                        break;
                    case "2":
                        recordSalary();
                        break;
                    case "3":
                        generateMonthlyReport();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye.");
                        break;
                    default:
                        System.out.println("Please choose 1, 2, 3, or 0.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nInput ended. Goodbye.");
        } finally {
            scanner.close();
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Add employee");
        System.out.println("2. Record salary payment");
        System.out.println("3. Generate monthly salary report");
        System.out.println("0. Exit");
    }

    private void addEmployee() {
        try {
            String name = readRequiredText("Employee full name: ");
            String department = readRequiredText("Department: ");
            String jobTitle = readRequiredText("Job title: ");
            LocalDate hireDate = readDate("Hire date (YYYY-MM-DD): ");

            Employee employee = new Employee(name, department, jobTitle, hireDate, "Active");
            int employeeId = payrollService.addEmployee(employee);
            System.out.println("Employee added with ID " + employeeId + ".");
        } catch (SQLException e) {
            printDatabaseError(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid employee details: " + e.getMessage());
        }
    }

    private void recordSalary() {
        try {
            List<Employee> employees = payrollService.getActiveEmployees();
            if (!showEmployees(employees)) {
                return;
            }

            Employee employee = chooseEmployee(employees);
            if (employee == null) {
                return;
            }

            LocalDate periodStart = readDate("Salary period start (YYYY-MM-DD): ");
            LocalDate periodEnd = readDate("Salary period end (YYYY-MM-DD): ");
            LocalDate paymentDate = readDate("Payment date (YYYY-MM-DD): ");
            BigDecimal basicSalary = readAmount("Basic salary: ");
            BigDecimal allowances = readAmount("Allowances: ");
            BigDecimal deductions = readAmount("Deductions: ");

            SalaryRecord record = new SalaryRecord(
                    employee.getEmployeeId(),
                    periodStart,
                    periodEnd,
                    paymentDate,
                    basicSalary,
                    allowances,
                    deductions);
            int recordId = payrollService.recordSalary(record);
            System.out.println("Salary record saved with ID " + recordId
                    + ". Net pay: " + record.getNetPay().toPlainString());
        } catch (SQLException e) {
            printDatabaseError(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid salary details: " + e.getMessage());
        }
    }

    private void generateMonthlyReport() {
        try {
            List<Employee> employees = payrollService.getActiveEmployees();
            if (!showEmployees(employees)) {
                return;
            }

            Employee employee = chooseEmployee(employees);
            if (employee == null) {
                return;
            }

            YearMonth month = readMonth("Report month (YYYY-MM): ");
            List<SalaryRecord> records =
                    payrollService.getMonthlySalaryStatement(employee.getEmployeeId(), month);
            System.out.println();
            System.out.println(salaryReport.format(employee, month, records));
        } catch (SQLException e) {
            printDatabaseError(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to generate report: " + e.getMessage());
        }
    }

    private boolean showEmployees(List<Employee> employees) {
        if (employees.isEmpty()) {
            System.out.println("No active employees found. Add an employee first.");
            return false;
        }

        System.out.println("Available employees:");
        for (Employee employee : employees) {
            System.out.println(employee.getEmployeeId() + " - " + employee.getFullName());
        }
        return true;
    }

    private Employee chooseEmployee(List<Employee> employees) {
        while (true) {
            String input = readLine("Enter employee ID (or 0 to cancel): ");
            int employeeId;
            try {
                employeeId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid numeric employee ID.");
                continue;
            }

            if (employeeId == 0) {
                return null;
            }

            for (Employee employee : employees) {
                if (employee.getEmployeeId() == employeeId) {
                    return employee;
                }
            }
            System.out.println("That ID is not in the active employee list.");
        }
    }

    private String readRequiredText(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be blank.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Enter a valid date in YYYY-MM-DD format.");
            }
        }
    }

    private YearMonth readMonth(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return YearMonth.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Enter a valid month in YYYY-MM format.");
            }
        }
    }

    private BigDecimal readAmount(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                BigDecimal amount = new BigDecimal(value);
                if (amount.signum() < 0) {
                    System.out.println("Amount cannot be negative.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount, for example 2500.00.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void printDatabaseError(SQLException e) {
        System.out.println("Database operation failed: " + e.getMessage());
    }
}
