import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeDao {
    private static final String INSERT_EMPLOYEE =
            "INSERT INTO employees (full_name, department, job_title, hire_date, status) "
                    + "VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ACTIVE_EMPLOYEES =
            "SELECT employee_id, full_name, department, job_title, hire_date, status "
                    + "FROM employees WHERE status = ? ORDER BY full_name, employee_id";

    public int addEmployee(Employee employee) throws SQLException {
        Objects.requireNonNull(employee, "employee cannot be null");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_EMPLOYEE)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getDepartment());
            statement.setString(3, employee.getJobTitle());
            statement.setString(4, employee.getHireDate().toString());
            statement.setString(5, employee.getStatus());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Employee insert did not create exactly one row.");
            }

            try (Statement idStatement = connection.createStatement();
                 ResultSet keys = idStatement.executeQuery("SELECT last_insert_rowid()")) {
                if (!keys.next()) {
                    throw new SQLException("Employee was inserted, but no generated ID was returned.");
                }
                int employeeId = keys.getInt(1);
                employee.setEmployeeId(employeeId);
                return employeeId;
            }
        }
    }

    public List<Employee> findActiveEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<Employee>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_EMPLOYEES)) {
            statement.setString(1, "Active");

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    employees.add(new Employee(
                            results.getInt("employee_id"),
                            results.getString("full_name"),
                            results.getString("department"),
                            results.getString("job_title"),
                            LocalDate.parse(results.getString("hire_date")),
                            results.getString("status")));
                }
            }
        }

        return employees;
    }
}
