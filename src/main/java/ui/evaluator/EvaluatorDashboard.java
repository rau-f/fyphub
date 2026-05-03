package ui.evaluator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import repository.DataStore;
import service.EvaluationService;
import ui.LoginView;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluatorDashboard {
    private final Scene scene;
    private final VBox contentBox = new VBox(20);

    public EvaluatorDashboard(Evaluator evaluator, Stage stage) {
        refreshContent(evaluator, stage);
        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");
        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void refreshContent(Evaluator evaluator, Stage stage) {
        contentBox.getChildren().clear();
        Label welcome = new Label("Welcome, " + evaluator.getFullName());
        welcome.getStyleClass().add("welcome-label");
        Label role = new Label("Evaluator Dashboard");
        role.getStyleClass().add("subtitle-label");
        VBox topText = new VBox(4, welcome, role);
        Button refreshBtn = new Button("\u21BB Refresh");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshContent(evaluator, stage));
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-logout");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, topText, spacer, refreshBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label sec1 = new Label("Projects Pending Evaluation");
        sec1.getStyleClass().add("section-header");
        VBox projectsList = new VBox(12);
        List<Project> underEval = DataStore.getProjects().values().stream()
                .filter(p -> p.getStatus() == ProjectStatus.UNDER_EVALUATION
                        || p.getStatus() == ProjectStatus.COMPLETED)
                .collect(Collectors.toList());
        if (underEval.isEmpty()) {
            Label empty = new Label("No projects pending evaluation.");
            empty.getStyleClass().add("info-label");
            empty.setStyle("-fx-font-size: 16px;");
            projectsList.getChildren().add(empty);
        }
        for (Project proj : underEval) {
            boolean alreadySubmitted = EvaluationService.hasEvaluatorSubmitted(
                    proj.getProjectID(), evaluator.getUserID());
            User sup = DataStore.getUserByID(proj.getSupervisorID());
            String supName = sup != null ? sup.getFullName() : "N/A";
            Label title = new Label(proj.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0; -fx-font-size: 16px;");
            Label info = new Label("Supervisor: " + supName +
                    "  |  Status: " + proj.getStatus().toString().replace("_", " "));
            info.getStyleClass().add("info-label");
            VBox card = new VBox(10, title, info);
            card.setPadding(new Insets(16));
            card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 10; -fx-border-color: #2a2a4a; -fx-border-radius: 10;");

            if (alreadySubmitted) {
                Label submitted = new Label("\u2713 Evaluation Submitted");
                submitted.setStyle("-fx-text-fill: #2ed573; -fx-font-weight: bold; -fx-font-size: 14px;");
                card.getChildren().add(submitted);
                if (proj.getStatus() == ProjectStatus.COMPLETED) {
                    List<EvalReport> reports = DataStore.getEvalReportsByProjectID(proj.getProjectID());
                    float avg = 0;
                    for (EvalReport r : reports) avg += r.getMarks();
                    if (!reports.isEmpty()) avg /= reports.size();
                    Label finalScore = new Label(String.format("Final Weighted Score: %.1f", avg));
                    finalScore.getStyleClass().add("score-label");
                    card.getChildren().add(finalScore);
                }
            } else {
                Label marksLbl = new Label("Marks (0-100):");
                marksLbl.getStyleClass().add("info-label");
                Spinner<Integer> marksSpinner = new Spinner<>(0, 100, 50);
                marksSpinner.setPrefWidth(100);
                marksSpinner.setEditable(true);
                TextArea commentsArea = new TextArea();
                commentsArea.setPromptText("Enter evaluation comments...");
                commentsArea.setPrefRowCount(3);
                commentsArea.setWrapText(true);
                Button submitBtn = new Button("Submit Evaluation");
                submitBtn.getStyleClass().add("button-primary");
                submitBtn.setOnAction(ev -> {
                    String comments = commentsArea.getText().trim();
                    if (comments.isEmpty()) { commentsArea.setPromptText("Please enter comments!"); return; }
                    EvaluationService.submitEvalReport(proj.getProjectID(),
                            evaluator.getUserID(), marksSpinner.getValue(), comments);
                    if (EvaluationService.checkAllSubmitted(proj.getProjectID()))
                        EvaluationService.calculateWeightedScore(proj.getProjectID());
                    refreshContent(evaluator, stage);
                });
                HBox marksRow = new HBox(8, marksLbl, marksSpinner);
                marksRow.setAlignment(Pos.CENTER_LEFT);
                card.getChildren().addAll(new Separator(), marksRow, commentsArea, submitBtn);
            }
            projectsList.getChildren().add(card);
        }

        contentBox.getChildren().addAll(topBar, new Separator(), sec1, projectsList);
        contentBox.setPadding(new Insets(32));
    }

    public Scene getScene() { return scene; }
}
