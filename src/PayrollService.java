import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

public class PayrollService {
    private final EmployeeDao employeeDao;
    private final SalaryRecordDao salaryRecordDao;

    public PayrollService() {
        this(new EmployeeDao(), new SalaryRecordDao());
    }

    public PayrollService(EmployeeDao employeeDao, SalaryRecordDao salaryRecordDao) {
        this.employeeDao = Objects.requireNonNull(employeeDao, "employeeDao cannot be null");
        this.salaryRecordDao = Objects.requireNonNull(salaryRecordDao, "salaryRecordDao cannot be null");
    }

    public int addEmployee(Employee employee) throws SQLException {
        validateEmployee(employee);
        return employeeDao.addEmployee(employee);
    }

    public List<Employee> getActiveEmployees() throws SQLException {
        return employeeDao.findActiveEmployees();
    }

    public int recordSalary(SalaryRecord salaryRecord) throws SQLException {
        validateSalaryRecord(salaryRecord);
        return salaryRecordDao.addSalaryRecord(salaryRecord);
    }

    public List<SalaryRecord> getSalaryStatement(
            int employeeId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        return salaryRecordDao.findByEmployeeAndPaymentDateRange(employeeId, startDate, endDate);
    }

    public List<SalaryRecord> getMonthlySalaryStatement(int employeeId, YearMonth month)
            throws SQLException {
        if (month == null) {
            throw new IllegalArgumentException("Month cannot be null.");
        }
        return getSalaryStatement(employeeId, month.atDay(1), month.atEndOfMonth());
    }

    private static void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        requireText(employee.getFullName(), "Employee name");
        requireText(employee.getDepartment(), "Department");
        requireText(employee.getJobTitle(), "Job title");
        if (employee.getHireDate() == null) {
            throw new IllegalArgumentException("Hire date cannot be null.");
        }
        requireText(employee.getStatus(), "Employee status");
    }

    private static void validateSalaryRecord(SalaryRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Salary record cannot be null.");
        }
        if (record.getEmployeeId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive.");
        }
        if (record.getPeriodStart() == null || record.getPeriodEnd() == null) {
            throw new IllegalArgumentException("Salary period dates cannot be null.");
        }
        if (record.getPeriodEnd().isBefore(record.getPeriodStart())) {
            throw new IllegalArgumentException("Period end cannot be before period start.");
        }
        requireNonNegative(record.getBasicSalary(), "Basic salary");
        requireNonNegative(record.getAllowances(), "Allowances");
        requireNonNegative(record.getDeductions(), "Deductions");
        BigDecimal grossPay = record.getBasicSalary().add(record.getAllowances());
        if (record.getDeductions().compareTo(grossPay) > 0) {
            throw new IllegalArgumentException("Deductions cannot exceed gross pay.");
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
    }

    private static void requireNonNegative(BigDecimal amount, String fieldName) {
        if (amount == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
    }
}