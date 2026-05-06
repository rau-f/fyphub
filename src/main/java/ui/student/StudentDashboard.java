package ui.student;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import repository.DataStore;
import ui.ViewAnimations;
import service.ProjectService;
import ui.LoginView;
import java.util.List;

public class StudentDashboard {
    private final Scene scene;
    private final VBox contentBox = new VBox(16);

    public StudentDashboard(Student student, Stage stage) {
        refreshContent(student, stage);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");

        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void refreshContent(Student student, Stage stage) {
        contentBox.getChildren().clear();

        Label welcomeLabel = new Label("Welcome, " + student.getFullName());
        welcomeLabel.getStyleClass().add("welcome-label");
        Label roleLabel = new Label("Student Dashboard");
        roleLabel.getStyleClass().add("subtitle-label");
        VBox topText = new VBox(4, welcomeLabel, roleLabel);

        Button refreshBtn = new Button("\u21BB Refresh");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshContent(student, stage));

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-logout");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, topText, spacer, refreshBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 16, 0));

        List<Proposal> proposals = DataStore.getProposalsByStudentID(student.getUserID());
        Proposal activeProposal = proposals.isEmpty() ? null : proposals.get(proposals.size() - 1);

        String proposalStatusText = activeProposal != null ?
                activeProposal.getStatus().toString().replace("_", " ") : "No Proposal";
        String supervisorName = "Not Assigned";
        if (activeProposal != null && activeProposal.getSupervisorID() != null) {
            User sup = DataStore.getUserByID(activeProposal.getSupervisorID());
            if (sup != null) supervisorName = sup.getFullName();
        }

        Project activeProject = null;
        if (activeProposal != null)
            activeProject = DataStore.getProjectByProposalID(activeProposal.getProposalID());
        float completionPct = activeProject != null ?
                ProjectService.getCompletionPct(activeProject.getProjectID()) : 0;

        VBox card1 = makeStatCard("Proposal Status", proposalStatusText, getStatusColor(activeProposal));
        VBox card2 = makeStatCard("Supervisor", supervisorName, "#00d4aa");

        Label progTitle = new Label("Project Progress");
        progTitle.getStyleClass().add("info-label");
        ProgressBar progressBar = new ProgressBar(completionPct / 100.0);
        progressBar.setPrefWidth(200);
        Label progPct = new Label(String.format("%.0f%%", completionPct));
        progPct.getStyleClass().add("score-label");
        VBox card3 = new VBox(8, progTitle, progressBar, progPct);
        card3.getStyleClass().add("stat-card");
        card3.setPrefWidth(280);

        HBox statsRow = new HBox(16, card1, card2, card3);

        Button submitBtn = new Button("Submit Proposal");
        submitBtn.getStyleClass().add("button-primary");
        submitBtn.setPrefWidth(200);
        submitBtn.setPrefHeight(44);
        submitBtn.setOnAction(e -> stage.setScene(new ProposalForm(student, stage).getScene()));

        Button feedbackBtn = new Button("View Feedback");
        feedbackBtn.getStyleClass().add("button-secondary");
        feedbackBtn.setPrefWidth(200);
        feedbackBtn.setPrefHeight(44);
        feedbackBtn.setOnAction(e -> stage.setScene(new FeedbackView(student, stage).getScene()));

        Button milestonesBtn = new Button("Manage Milestones");
        milestonesBtn.getStyleClass().add("button-secondary");
        milestonesBtn.setPrefWidth(200);
        milestonesBtn.setPrefHeight(44);
        if (activeProject != null) {
            Project proj = activeProject;
            milestonesBtn.setOnAction(e -> stage.setScene(new MilestoneView(student, proj, stage).getScene()));
        } else {
            milestonesBtn.setDisable(true);
        }

        HBox actionsRow = new HBox(16, submitBtn, feedbackBtn, milestonesBtn);

        VBox projectCard;
        if (activeProject != null) {
            Label pt = new Label("Active Project: " + activeProject.getTitle());
            pt.getStyleClass().add("section-header");
            Label ps = new Label("Status: " + activeProject.getStatus().toString().replace("_", " "));
            ps.getStyleClass().add("info-label");
            projectCard = new VBox(8, pt, ps);
        } else {
            Label np = new Label("No active project. Submit a proposal to get started!");
            np.getStyleClass().add("info-label");
            projectCard = new VBox(8, np);
        }
        projectCard.getStyleClass().add("card");

        contentBox.getChildren().addAll(topBar, new Separator(), statsRow, actionsRow, projectCard);
        contentBox.setPadding(new Insets(32));
        ViewAnimations.staggerChildren(contentBox);
    }

    private VBox makeStatCard(String title, String value, String color) {
        Label t = new Label(title); t.getStyleClass().add("info-label");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        VBox c = new VBox(6, t, v); c.getStyleClass().add("stat-card"); c.setPrefWidth(280);
        return c;
    }

    private String getStatusColor(Proposal p) {
        if (p == null) return "#555577";
        return switch (p.getStatus()) {
            case APPROVED -> "#2ed573";
            case SUBMITTED, UNDER_REVIEW -> "#ffa500";
            case REVISION_REQUIRED -> "#ff4757";
            default -> "#c8c8dc";
        };
    }

    public Scene getScene() { return scene; }
}
