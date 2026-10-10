import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SalaryReport {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    public String format(Employee employee, YearMonth month, List<SalaryRecord> records) {
        Objects.requireNonNull(employee, "employee cannot be null");
        Objects.requireNonNull(month, "month cannot be null");
        Objects.requireNonNull(records, "records cannot be null");

        NumberFormat amountFormat = NumberFormat.getNumberInstance(Locale.US);
        amountFormat.setMinimumFractionDigits(2);
        amountFormat.setMaximumFractionDigits(2);

        StringBuilder report = new StringBuilder();
        report.append("                 EMPLOYEE SALARY STATEMENT\n");
        report.append("============================================================\n");
        report.append("Employee ID: ").append(employee.getEmployeeId()).append('\n');
        report.append("Name:        ").append(employee.getFullName()).append('\n');
        report.append("Department:  ").append(employee.getDepartment()).append('\n');
        report.append("Job title:   ").append(employee.getJobTitle()).append('\n');
        report.append("Period:      ").append(month.format(MONTH_FORMAT)).append("\n\n");
        report.append(String.format(Locale.ROOT, "%-13s %-23s %12s %12s %12s%n",
                "Payment date", "Salary period", "Basic", "Allowances", "Deductions"));
        report.append("--------------------------------------------------------------------------\n");

        BigDecimal totalBasic = BigDecimal.ZERO;
        BigDecimal totalAllowances = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNetPay = BigDecimal.ZERO;

        if (records.isEmpty()) {
            report.append("No salary records found for this month.\n");
        } else {
            for (SalaryRecord record : records) {
                Objects.requireNonNull(record, "records cannot contain null");
                if (record.getEmployeeId() != employee.getEmployeeId()) {
                    throw new IllegalArgumentException(
                            "Salary record belongs to a different employee.");
                }

                String salaryPeriod = record.getPeriodStart().format(DATE_FORMAT)
                        + " to " + record.getPeriodEnd().format(DATE_FORMAT);
                report.append(String.format(Locale.ROOT, "%-13s %-23s %12s %12s %12s%n",
                        record.getPaymentDate().format(DATE_FORMAT),
                        salaryPeriod,
                        amountFormat.format(record.getBasicSalary()),
                        amountFormat.format(record.getAllowances()),
                        amountFormat.format(record.getDeductions())));

                totalBasic = totalBasic.add(record.getBasicSalary());
                totalAllowances = totalAllowances.add(record.getAllowances());
                totalDeductions = totalDeductions.add(record.getDeductions());
                totalNetPay = totalNetPay.add(record.getNetPay());
            }
        }

        report.append("--------------------------------------------------------------------------\n");
        report.append("Total basic salary: ").append(amountFormat.format(totalBasic)).append('\n');
        report.append("Total allowances:   ").append(amountFormat.format(totalAllowances)).append('\n');
        report.append("Total deductions:   ").append(amountFormat.format(totalDeductions)).append('\n');
        report.append("NET PAY:            ").append(amountFormat.format(totalNetPay)).append('\n');
        report.append("============================================================\n");
        return report.toString();
    }
}
