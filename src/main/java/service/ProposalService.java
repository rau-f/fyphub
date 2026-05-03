package service;

import model.*;
import repository.DataStore;

public class ProposalService {

    public static Proposal submitProposal(String studentID, String title, String description) {
        float score = PlagiarismService.checkSimilarity(description);

        if (PlagiarismService.isAboveThreshold(score)) {
            throw new IllegalArgumentException(
                    String.format("Plagiarism detected! Similarity score: %.1f%%. Threshold is 20%%.", score));
        }

        Proposal proposal = new Proposal(title, description, studentID);
        proposal.setStatus(ProposalStatus.SUBMITTED);
        proposal.setPlagiarismScore(score);
        DataStore.saveProposal(proposal);
        return proposal;
    }

    public static void assignSupervisor(String proposalID, String supervisorID) {
        Proposal proposal = DataStore.getProposalByID(proposalID);
        if (proposal != null) {
            proposal.setSupervisorID(supervisorID);
            proposal.setStatus(ProposalStatus.UNDER_REVIEW);
            DataStore.updateProposal(proposal);
        }
    }

    public static void approveProposal(String proposalID) {
        Proposal proposal = DataStore.getProposalByID(proposalID);
        if (proposal != null) {
            proposal.setStatus(ProposalStatus.APPROVED);
            DataStore.updateProposal(proposal);
        }
    }

    public static Feedback requestRevision(String proposalID, String content,
                                           float score, String supervisorID) {
        Proposal proposal = DataStore.getProposalByID(proposalID);
        if (proposal != null) {
            proposal.setStatus(ProposalStatus.REVISION_REQUIRED);
            DataStore.updateProposal(proposal);
        }

        Feedback feedback = new Feedback(content, score, supervisorID, proposalID);
        DataStore.saveFeedback(feedback);
        return feedback;
    }
}
