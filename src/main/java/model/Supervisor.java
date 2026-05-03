package model;

public class Supervisor extends User {
    private String facultyID;
    private String department;
    private String specialization;

    public Supervisor(String email, String password, String fullName,
                      String facultyID, String department, String specialization) {
        super(email, password, Role.SUPERVISOR, fullName);
        this.facultyID = facultyID;
        this.department = department;
        this.specialization = specialization;
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equals(email) && getPassword().equals(password);
    }

    public String getFacultyID() { return facultyID; }
    public void setFacultyID(String facultyID) { this.facultyID = facultyID; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
