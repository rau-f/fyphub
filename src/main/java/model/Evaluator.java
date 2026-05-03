package model;

public class Evaluator extends User {
    private String evaluatorID;
    private String organization;

    public Evaluator(String email, String password, String fullName,
                     String evaluatorID, String organization) {
        super(email, password, Role.EVALUATOR, fullName);
        this.evaluatorID = evaluatorID;
        this.organization = organization;
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equals(email) && getPassword().equals(password);
    }

    public String getEvaluatorID() { return evaluatorID; }
    public void setEvaluatorID(String evaluatorID) { this.evaluatorID = evaluatorID; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
}
