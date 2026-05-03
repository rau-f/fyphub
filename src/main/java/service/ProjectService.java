package service;

import model.*;
import repository.DataStore;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectService {

    public static Project createProject(String proposalID, String coordinatorID) {
        Proposal proposal = DataStore.getProposalByID(proposalID);
        if (proposal == null) {
            throw new IllegalArgumentException("Proposal not found");
        }

        Project project = new Project(
                proposal.getTitle(),
                proposal.getDescription(),
                proposalID,
                proposal.getSupervisorID(),
                coordinatorID
        );
        project.setStatus(ProjectStatus.IN_PROGRESS);
        DataStore.saveProject(project);
        return project;
    }

    public static Milestone addMilestone(String projectID, String title, LocalDateTime deadline) {
        Milestone milestone = new Milestone(title, deadline, projectID);
        DataStore.saveMilestone(milestone);
        return milestone;
    }

    public static void completeMilestone(String milestoneID) {
        Milestone milestone = DataStore.getMilestoneByID(milestoneID);
        if (milestone != null) {
            milestone.setCompleted(true);
            DataStore.updateMilestone(milestone);
            recalculateCompletion(milestone.getProjectID());
        }
    }

    public static float getCompletionPct(String projectID) {
        List<Milestone> milestones = DataStore.getMilestonesByProjectID(projectID);
        if (milestones.isEmpty()) return 0.0f;

        long completed = milestones.stream().filter(Milestone::isCompleted).count();
        return (float) completed / milestones.size() * 100.0f;
    }

    private static void recalculateCompletion(String projectID) {
        float pct = getCompletionPct(projectID);
        List<Milestone> milestones = DataStore.getMilestonesByProjectID(projectID);
        for (Milestone m : milestones) {
            m.setCompletionPct(pct);
            DataStore.updateMilestone(m);
        }
    }
}
