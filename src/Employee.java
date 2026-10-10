import java.time.LocalDate;

public class Employee {
    private int employeeId;
    private String fullName;
    private String department;
    private String jobTitle;
    private LocalDate hireDate;
    private String status;

    public Employee(String fullName, String department, String jobTitle,
                    LocalDate hireDate, String status) {
        this.fullName = fullName;
        this.department = department;
        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
        this.status = status;
    }

    public Employee(int employeeId, String fullName, String department, String jobTitle,
                    LocalDate hireDate, String status) {
        this(fullName, department, jobTitle, hireDate, status);
        this.employeeId = employeeId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
