package model;

public class Student extends User {
    private String studentID;
    private String department;
    private float cgpa;
    private String projectStatus;

    public Student(String email, String password, String fullName,
                   String studentID, String department, float cgpa) {
        super(email, password, Role.STUDENT, fullName);
        this.studentID = studentID;
        this.department = department;
        this.cgpa = cgpa;
        this.projectStatus = "NONE";
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equals(email) && getPassword().equals(password);
    }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public float getCgpa() { return cgpa; }
    public void setCgpa(float cgpa) { this.cgpa = cgpa; }

    public String getProjectStatus() { return projectStatus; }
    public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }
}
