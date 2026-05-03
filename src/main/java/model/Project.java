package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Project {
    private String projectID;
    private String title;
    private String abstractText;
    private ProjectStatus status;
    private LocalDateTime submissionDate;
    private String proposalID;
    private String supervisorID;
    private String coordinatorID;

    public Project(String title, String abstractText, String proposalID,
                   String supervisorID, String coordinatorID) {
        this.projectID = UUID.randomUUID().toString();
        this.title = title;
        this.abstractText = abstractText;
        this.status = ProjectStatus.IN_PROGRESS;
        this.submissionDate = LocalDateTime.now();
        this.proposalID = proposalID;
        this.supervisorID = supervisorID;
        this.coordinatorID = coordinatorID;
    }

    public String getProjectID() { return projectID; }
    public void setProjectID(String projectID) { this.projectID = projectID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public String getProposalID() { return proposalID; }
    public void setProposalID(String proposalID) { this.proposalID = proposalID; }

    public String getSupervisorID() { return supervisorID; }
    public void setSupervisorID(String supervisorID) { this.supervisorID = supervisorID; }

    public String getCoordinatorID() { return coordinatorID; }
    public void setCoordinatorID(String coordinatorID) { this.coordinatorID = coordinatorID; }

    public float getWeightedScore() { return 0.0f; }
}
