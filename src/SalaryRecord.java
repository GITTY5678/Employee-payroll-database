import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class SalaryRecord {
    private int recordId;
    private int employeeId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate paymentDate;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;

    public SalaryRecord(int employeeId, LocalDate periodStart, LocalDate periodEnd,
                        LocalDate paymentDate, BigDecimal basicSalary,
                        BigDecimal allowances, BigDecimal deductions) {
        this.employeeId = employeeId;
        this.periodStart = Objects.requireNonNull(periodStart, "periodStart cannot be null");
        this.periodEnd = Objects.requireNonNull(periodEnd, "periodEnd cannot be null");
        this.paymentDate = Objects.requireNonNull(paymentDate, "paymentDate cannot be null");
        this.basicSalary = Objects.requireNonNull(basicSalary, "basicSalary cannot be null");
        this.allowances = Objects.requireNonNull(allowances, "allowances cannot be null");
        this.deductions = Objects.requireNonNull(deductions, "deductions cannot be null");
    }

    public SalaryRecord(int recordId, int employeeId, LocalDate periodStart, LocalDate periodEnd,
                        LocalDate paymentDate, BigDecimal basicSalary,
                        BigDecimal allowances, BigDecimal deductions) {
        this(employeeId, periodStart, periodEnd, paymentDate, basicSalary, allowances, deductions);
        this.recordId = recordId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = Objects.requireNonNull(periodStart, "periodStart cannot be null");
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = Objects.requireNonNull(periodEnd, "periodEnd cannot be null");
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = Objects.requireNonNull(paymentDate, "paymentDate cannot be null");
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = Objects.requireNonNull(basicSalary, "basicSalary cannot be null");
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = Objects.requireNonNull(allowances, "allowances cannot be null");
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = Objects.requireNonNull(deductions, "deductions cannot be null");
    }

    public BigDecimal getNetPay() {
        return basicSalary.add(allowances).subtract(deductions);
    }
}
