package nl.inholland.endassignment.endproject.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Showing implements Serializable {
    private static final long serialVersionUID = 1L; // Explicit serialVersionUID
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int totalSeats;
    private Set<Integer> soldSeats; // Track sold seat numbers

    public Showing(String title, LocalDateTime startDateTime, LocalDateTime endDateTime, int totalSeats) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.totalSeats = totalSeats;
        this.soldSeats = new HashSet<>();
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }


    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public Set<Integer> getSoldSeats() {
        return soldSeats;
    }

    public int getAvailableSeats() {
        return totalSeats - soldSeats.size(); // Calculate dynamically
    }

    public void sellSeat(int seatNumber) {
        soldSeats.add(seatNumber);
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}
