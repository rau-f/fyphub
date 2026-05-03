package model;

public class Coordinator extends User {
    private String coordinatorID;
    private String department;

    public Coordinator(String email, String password, String fullName,
                       String coordinatorID, String department) {
        super(email, password, Role.COORDINATOR, fullName);
        this.coordinatorID = coordinatorID;
        this.department = department;
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equals(email) && getPassword().equals(password);
    }

    public String getCoordinatorID() { return coordinatorID; }
    public void setCoordinatorID(String coordinatorID) { this.coordinatorID = coordinatorID; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
