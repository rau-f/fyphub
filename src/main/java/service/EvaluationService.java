package service;

import model.*;
import repository.DataStore;

import java.util.List;

public class EvaluationService {

    public static EvalReport submitEvalReport(String projectID, String evaluatorID,
                                              int marks, String comments) {
        EvalReport report = new EvalReport(marks, comments, evaluatorID, projectID);
        report.calculateWeightedScore();
        DataStore.saveEvalReport(report);
        return report;
    }

    public static boolean checkAllSubmitted(String projectID) {
        List<Evaluator> allEvaluators = DataStore.getAllEvaluators();
        List<EvalReport> reports = DataStore.getEvalReportsByProjectID(projectID);

        for (Evaluator evaluator : allEvaluators) {
            boolean hasSubmitted = reports.stream()
                    .anyMatch(r -> r.getEvaluatorID().equals(evaluator.getUserID()));
            if (!hasSubmitted) {
                return false;
            }
        }
        return !allEvaluators.isEmpty();
    }

    public static float calculateWeightedScore(String projectID) {
        List<EvalReport> reports = DataStore.getEvalReportsByProjectID(projectID);
        if (reports.isEmpty()) return 0.0f;

        float total = 0;
        for (EvalReport r : reports) {
            total += r.getMarks();
        }
        float avg = total / reports.size();

        Project project = DataStore.getProjectByID(projectID);
        if (project != null) {
            project.setStatus(ProjectStatus.COMPLETED);
            DataStore.updateProject(project);
        }

        return avg;
    }

    public static boolean hasEvaluatorSubmitted(String projectID, String evaluatorID) {
        return DataStore.getEvalReportsByProjectID(projectID).stream()
                .anyMatch(r -> r.getEvaluatorID().equals(evaluatorID));
    }
}
