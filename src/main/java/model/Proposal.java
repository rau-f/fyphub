package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Proposal {
    private String proposalID;
    private String title;
    private String description;
    private ProposalStatus status;
    private LocalDateTime submittedAt;
    private float plagiarismScore;
    private String studentID;
    private String supervisorID;

    public Proposal(String title, String description, String studentID) {
        this.proposalID = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.status = ProposalStatus.DRAFT;
        this.submittedAt = LocalDateTime.now();
        this.plagiarismScore = 0.0f;
        this.studentID = studentID;
        this.supervisorID = null;
    }

    public String getProposalID() { return proposalID; }
    public void setProposalID(String proposalID) { this.proposalID = proposalID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProposalStatus getStatus() { return status; }
    public void setStatus(ProposalStatus status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public float getPlagiarismScore() { return plagiarismScore; }
    public void setPlagiarismScore(float plagiarismScore) { this.plagiarismScore = plagiarismScore; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getSupervisorID() { return supervisorID; }
    public void setSupervisorID(String supervisorID) { this.supervisorID = supervisorID; }
}
