package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Feedback {
    private String feedbackID;
    private String content;
    private float score;
    private LocalDateTime givenAt;
    private String supervisorID;
    private String proposalID;

    public Feedback(String content, float score, String supervisorID, String proposalID) {
        this.feedbackID = UUID.randomUUID().toString();
        this.content = content;
        this.score = score;
        this.givenAt = LocalDateTime.now();
        this.supervisorID = supervisorID;
        this.proposalID = proposalID;
    }

    public String getFeedbackID() { return feedbackID; }
    public void setFeedbackID(String feedbackID) { this.feedbackID = feedbackID; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    public LocalDateTime getGivenAt() { return givenAt; }
    public void setGivenAt(LocalDateTime givenAt) { this.givenAt = givenAt; }

    public String getSupervisorID() { return supervisorID; }
    public void setSupervisorID(String supervisorID) { this.supervisorID = supervisorID; }

    public String getProposalID() { return proposalID; }
    public void setProposalID(String proposalID) { this.proposalID = proposalID; }
}
