package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class EvalReport {
    private String reportID;
    private int marks;
    private String comments;
    private float weightedScore;
    private LocalDateTime submittedAt;
    private String evaluatorID;
    private String projectID;

    public EvalReport(int marks, String comments, String evaluatorID, String projectID) {
        this.reportID = UUID.randomUUID().toString();
        this.marks = marks;
        this.comments = comments;
        this.weightedScore = 0.0f;
        this.submittedAt = LocalDateTime.now();
        this.evaluatorID = evaluatorID;
        this.projectID = projectID;
    }

    public String getReportID() { return reportID; }
    public void setReportID(String reportID) { this.reportID = reportID; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public float getWeightedScore() { return weightedScore; }
    public void setWeightedScore(float weightedScore) { this.weightedScore = weightedScore; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getEvaluatorID() { return evaluatorID; }
    public void setEvaluatorID(String evaluatorID) { this.evaluatorID = evaluatorID; }

    public String getProjectID() { return projectID; }
    public void setProjectID(String projectID) { this.projectID = projectID; }

    public float calculateWeightedScore() {
        this.weightedScore = marks;
        return this.weightedScore;
    }
}
