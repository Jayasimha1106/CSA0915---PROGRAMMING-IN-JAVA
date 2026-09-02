import java.io.Serializable;

/**
 * Employee entity representing bank staff.
 * Demonstrates Encapsulation, Default and Parameterized Constructors, and Serialization.
 */
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String name;
    private String department;
    private String position;
    private double salary;

    // Default Constructor
    public Employee() {
        this.employeeId = "";
        this.name = "";
        this.department = "";
        this.position = "";
        this.salary = 0.0;
    }

    // Parameterized Constructor
    public Employee(String employeeId, String name, String department, String position, double salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.position = position;
        this.salary = salary;
    }

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return String.format("Emp ID: %s | Name: %s | Dept: %s | Role: %s | Salary: $%.2f",
                employeeId, name, department, position, salary);
    }
}
