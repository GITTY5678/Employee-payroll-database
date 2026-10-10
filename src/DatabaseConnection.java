
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // The relative path places employee_payroll.db in the application's working directory.
    private static final String URL = "jdbc:sqlite:employee_payroll.db";

    private static final String CREATE_EMPLOYEES_TABLE =
            "CREATE TABLE IF NOT EXISTS employees ("
                    + "employee_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "full_name TEXT NOT NULL, "
                    + "department TEXT NOT NULL, "
                    + "job_title TEXT NOT NULL, "
                    + "hire_date TEXT NOT NULL, "
                    + "status TEXT NOT NULL DEFAULT 'Active')";
    private static final String CREATE_SALARY_RECORDS_TABLE =
            "CREATE TABLE IF NOT EXISTS salary_records ("
                    + "record_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "employee_id INTEGER NOT NULL, "
                    + "period_start TEXT NOT NULL, "
                    + "period_end TEXT NOT NULL, "
                    + "payment_date TEXT NOT NULL, "
                    + "basic_salary NUMERIC NOT NULL CHECK (basic_salary >= 0), "
                    + "allowances NUMERIC NOT NULL DEFAULT 0.00 CHECK (allowances >= 0), "
                    + "deductions NUMERIC NOT NULL DEFAULT 0.00 CHECK (deductions >= 0), "
                    + "net_pay NUMERIC NOT NULL, "
                    + "CHECK (period_end >= period_start), "
                    + "CHECK (deductions <= basic_salary + allowances), "
                    + "FOREIGN KEY (employee_id) REFERENCES employees (employee_id) "
                    + "ON UPDATE CASCADE ON DELETE RESTRICT)";
    private static final String CREATE_SALARY_PAYMENT_INDEX =
            "CREATE INDEX IF NOT EXISTS idx_salary_employee_payment_date "
                    + "ON salary_records (employee_id, payment_date)";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException closeException) {
                e.addSuppressed(closeException);
            }
            throw e;
        }
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            try {
                statement.execute(CREATE_EMPLOYEES_TABLE);
                statement.execute(CREATE_SALARY_RECORDS_TABLE);
                statement.execute(CREATE_SALARY_PAYMENT_INDEX);
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        }
    }
}
