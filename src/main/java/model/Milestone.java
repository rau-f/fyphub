package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Milestone {
    private String milestoneID;
    private String title;
    private LocalDateTime deadline;
    private boolean isCompleted;
    private float completionPct;
    private String projectID;

    public Milestone(String title, LocalDateTime deadline, String projectID) {
        this.milestoneID = UUID.randomUUID().toString();
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = false;
        this.completionPct = 0.0f;
        this.projectID = projectID;
    }

    public String getMilestoneID() { return milestoneID; }
    public void setMilestoneID(String milestoneID) { this.milestoneID = milestoneID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    public float getCompletionPct() { return completionPct; }
    public void setCompletionPct(float completionPct) { this.completionPct = completionPct; }

    public String getProjectID() { return projectID; }
    public void setProjectID(String projectID) { this.projectID = projectID; }

    public boolean checkDeadline() {
        return LocalDateTime.now().isBefore(deadline);
    }
}
