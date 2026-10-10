import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class SampleData {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();

            PayrollService payrollService = new PayrollService();
            YearMonth thisMonth = YearMonth.now();

            addSampleEmployee(payrollService,
                    new Employee("Aarav Sharma (Demo)", "Finance", "Accountant",
                            LocalDate.of(2021, 3, 15), "Active"),
                    new BigDecimal("52000"), new BigDecimal("4500"),
                    new BigDecimal("3200"), thisMonth);
            addSampleEmployee(payrollService,
                    new Employee("Priya Nair (Demo)", "Human Resources", "HR Manager",
                            LocalDate.of(2020, 7, 1), "Active"),
                    new BigDecimal("68000"), new BigDecimal("6000"),
                    new BigDecimal("5200"), thisMonth);
            addSampleEmployee(payrollService,
                    new Employee("Rohan Patel (Demo)", "Engineering", "Software Engineer",
                            LocalDate.of(2022, 1, 10), "Active"),
                    new BigDecimal("75000"), new BigDecimal("8000"),
                    new BigDecimal("6500"), thisMonth);

            System.out.println("Sample payroll data is ready.");
        } catch (SQLException e) {
            System.err.println("Could not add sample data: " + e.getMessage());
        }
    }

    private static void addSampleEmployee(
            PayrollService payrollService,
            Employee sample,
            BigDecimal basicSalary,
            BigDecimal allowances,
            BigDecimal deductions,
            YearMonth thisMonth) throws SQLException {
        int employeeId = findEmployeeId(payrollService.getActiveEmployees(), sample.getFullName());

        if (employeeId == 0) {
            employeeId = payrollService.addEmployee(sample);
        }

        addSalaryIfMissing(payrollService, employeeId, basicSalary,
                allowances, deductions, thisMonth.minusMonths(1));
        addSalaryIfMissing(payrollService, employeeId, basicSalary,
                allowances, deductions, thisMonth);
    }

    private static int findEmployeeId(List<Employee> employees, String fullName) {
        for (Employee employee : employees) {
            if (employee.getFullName().equals(fullName)) {
                return employee.getEmployeeId();
            }
        }
        return 0;
    }

    private static void addSalaryIfMissing(
            PayrollService payrollService,
            int employeeId,
            BigDecimal basicSalary,
            BigDecimal allowances,
            BigDecimal deductions,
            YearMonth paymentMonth) throws SQLException {
        if (!payrollService.getMonthlySalaryStatement(employeeId, paymentMonth).isEmpty()) {
            return;
        }

        YearMonth salaryMonth = paymentMonth.minusMonths(1);
        SalaryRecord record = new SalaryRecord(
                employeeId,
                salaryMonth.atDay(1),
                salaryMonth.atEndOfMonth(),
                paymentMonth.atDay(1),
                basicSalary,
                allowances,
                deductions);
        payrollService.recordSalary(record);
    }
}
