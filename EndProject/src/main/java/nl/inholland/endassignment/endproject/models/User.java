package nl.inholland.endassignment.endproject.models;


public class User {
    private String username;
    private String password;
    private String role;  //Manager, Sales

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
