import java.io.Serializable;

/**
 * Customer entity representing a bank client.
 * Demonstrates:
 * 1. Encapsulation: Private fields with public getters and setters
 * 2. Constructors: Default and Parameterized constructors
 * 3. Serialization: Implements Serializable interface
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String email;

    // Default Constructor
    public Customer() {
        this.customerId = "";
        this.name = "";
        this.address = "";
        this.phone = "";
        this.email = "";
    }

    // Parameterized Constructor
    public Customer(String customerId, String name, String address, String phone, String email) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    // Getters and Setters (Encapsulation)
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String displayCustomerDetails() {
        return String.format("Customer ID: %s | Name: %s | Phone: %s | Email: %s | Address: %s",
                customerId, name, phone, email, address);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", customerId, name, phone);
    }
}
