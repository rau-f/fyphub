package model;

import java.util.UUID;

public abstract class User {
    private String userID;
    private String email;
    private String password;
    private Role role;
    private String fullName;

    public User(String email, String password, Role role, String fullName) {
        this.userID = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    public abstract boolean login(String email, String password);

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
