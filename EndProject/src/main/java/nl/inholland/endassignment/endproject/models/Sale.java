package nl.inholland.endassignment.endproject.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Sale implements Serializable {
    private static final long serialVersionUID = 1L; // Explicit serialVersionUID
    private String customerName;
    private String customerEmail; // Added email field
    private Showing showing;  // Reference to the Showing object
    private List<Integer> seatNumbers;
    private LocalDateTime saleDate;

    public Sale(String customerName, String customerEmail, Showing showing, List<Integer> seatNumbers, LocalDateTime saleDate) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.showing = showing;
        this.seatNumbers = seatNumbers;
        this.saleDate = saleDate;
    }

    // Getter for customerName
    public String getCustomerName() {
        return customerName;
    }

    // Getter for customerEmail
    public String getCustomerEmail() {
        return customerEmail;
    }

    // Getter for Showing
    public Showing getShowing() {
        return showing;
    }

    // Getter for seatNumbers
    public List<Integer> getSeatNumbers() {
        return seatNumbers;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }
}
