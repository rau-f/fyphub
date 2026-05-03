package model;

public class Admin extends User {
    private String adminID;

    public Admin(String email, String password, String fullName, String adminID) {
        super(email, password, Role.ADMIN, fullName);
        this.adminID = adminID;
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equals(email) && getPassword().equals(password);
    }

    public String getAdminID() { return adminID; }
    public void setAdminID(String adminID) { this.adminID = adminID; }
}
