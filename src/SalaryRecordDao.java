import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SalaryRecordDao {
    private static final String INSERT_SALARY_RECORD =
            "INSERT INTO salary_records "
                    + "(employee_id, period_start, period_end, payment_date, basic_salary, "
                    + "allowances, deductions, net_pay) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_EMPLOYEE_AND_PAYMENT_DATE =
            "SELECT record_id, employee_id, period_start, period_end, payment_date, "
                    + "basic_salary, allowances, deductions "
                    + "FROM salary_records "
                    + "WHERE employee_id = ? AND payment_date BETWEEN ? AND ? "
                    + "ORDER BY payment_date, record_id";

    public int addSalaryRecord(SalaryRecord salaryRecord) throws SQLException {
        Objects.requireNonNull(salaryRecord, "salaryRecord cannot be null");
        if (salaryRecord.getEmployeeId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SALARY_RECORD)) {
            statement.setInt(1, salaryRecord.getEmployeeId());
            statement.setString(2, salaryRecord.getPeriodStart().toString());
            statement.setString(3, salaryRecord.getPeriodEnd().toString());
            statement.setString(4, salaryRecord.getPaymentDate().toString());
            statement.setBigDecimal(5, salaryRecord.getBasicSalary());
            statement.setBigDecimal(6, salaryRecord.getAllowances());
            statement.setBigDecimal(7, salaryRecord.getDeductions());
            statement.setBigDecimal(8, salaryRecord.getNetPay());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Salary record insert did not create exactly one row.");
            }

            try (Statement idStatement = connection.createStatement();
                 ResultSet keys = idStatement.executeQuery("SELECT last_insert_rowid()")) {
                if (!keys.next()) {
                    throw new SQLException("Salary record was inserted, but no generated ID was returned.");
                }
                int recordId = keys.getInt(1);
                salaryRecord.setRecordId(recordId);
                return recordId;
            }
        }
    }

    public List<SalaryRecord> findByEmployeeAndPaymentDateRange(
            int employeeId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive.");
        }
        Objects.requireNonNull(startDate, "startDate cannot be null");
        Objects.requireNonNull(endDate, "endDate cannot be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        List<SalaryRecord> records = new ArrayList<SalaryRecord>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_BY_EMPLOYEE_AND_PAYMENT_DATE)) {
            statement.setInt(1, employeeId);
            statement.setString(2, startDate.toString());
            statement.setString(3, endDate.toString());

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    records.add(new SalaryRecord(
                            results.getInt("record_id"),
                            results.getInt("employee_id"),
                            LocalDate.parse(results.getString("period_start")),
                            LocalDate.parse(results.getString("period_end")),
                            LocalDate.parse(results.getString("payment_date")),
                            results.getBigDecimal("basic_salary"),
                            results.getBigDecimal("allowances"),
                            results.getBigDecimal("deductions")));
                }
            }
        }

        return records;
    }
}
