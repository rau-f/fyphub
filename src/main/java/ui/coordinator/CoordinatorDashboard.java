package ui.coordinator;

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
import service.ProposalService;
import ui.LoginView;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinatorDashboard {
    private final Scene scene;
    private final VBox contentBox = new VBox(20);

    public CoordinatorDashboard(Coordinator coordinator, Stage stage) {
        refreshContent(coordinator, stage);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");

        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void refreshContent(Coordinator coordinator, Stage stage) {
        contentBox.getChildren().clear();

        Label welcome = new Label("Welcome, " + coordinator.getFullName());
        welcome.getStyleClass().add("welcome-label");
        Label role = new Label("Coordinator Dashboard");
        role.getStyleClass().add("subtitle-label");
        VBox topText = new VBox(4, welcome, role);

        Button refreshBtn = new Button("\u21BB Refresh");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshContent(coordinator, stage));

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-logout");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, topText, spacer, refreshBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // --- Pending Proposals ---
        Label sec1 = new Label("Pending Proposals");
        sec1.getStyleClass().add("section-header");
        VBox proposalsList = new VBox(10);

        List<Proposal> submitted = DataStore.getProposals().values().stream()
                .filter(p -> p.getStatus() == ProposalStatus.SUBMITTED)
                .collect(Collectors.toList());
        List<Supervisor> supervisors = DataStore.getAllSupervisors();

        if (submitted.isEmpty()) {
            Label empty = new Label("No pending proposals.");
            empty.getStyleClass().add("info-label");
            proposalsList.getChildren().add(empty);
        }
        for (Proposal p : submitted) {
            User student = DataStore.getUserByID(p.getStudentID());
            String sName = student != null ? student.getFullName() : "Unknown";
            Label title = new Label(p.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0; -fx-font-size: 15px;");
            Label info = new Label("By: " + sName + "  |  Plagiarism: " + String.format("%.1f%%", p.getPlagiarismScore()));
            info.getStyleClass().add("info-label");

            ComboBox<String> supCombo = new ComboBox<>();
            for (Supervisor s : supervisors) supCombo.getItems().add(s.getUserID() + " — " + s.getFullName());
            supCombo.setPromptText("Select Supervisor");
            supCombo.setPrefWidth(250);

            Button assignBtn = new Button("Assign");
            assignBtn.getStyleClass().add("button-success");
            assignBtn.setOnAction(ev -> {
                String sel = supCombo.getValue();
                if (sel == null) return;
                ProposalService.assignSupervisor(p.getProposalID(), sel.split(" — ")[0]);
                refreshContent(coordinator, stage);
            });

            HBox actions = new HBox(10, supCombo, assignBtn);
            actions.setAlignment(Pos.CENTER_LEFT);
            VBox card = new VBox(6, title, info, actions);
            card.setPadding(new Insets(14));
            card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-border-color: #2a2a4a; -fx-border-radius: 8;");
            proposalsList.getChildren().add(card);
        }

        // --- Approved Proposals ---
        Label sec2 = new Label("Approved Proposals — Create Projects");
        sec2.getStyleClass().add("section-header");
        VBox approvedList = new VBox(10);

        List<Proposal> approved = DataStore.getProposals().values().stream()
                .filter(p -> p.getStatus() == ProposalStatus.APPROVED)
                .collect(Collectors.toList());
        if (approved.isEmpty()) {
            Label empty = new Label("No approved proposals.");
            empty.getStyleClass().add("info-label");
            approvedList.getChildren().add(empty);
        }
        for (Proposal p : approved) {
            Project existing = DataStore.getProjectByProposalID(p.getProposalID());
            User student = DataStore.getUserByID(p.getStudentID());
            String sName = student != null ? student.getFullName() : "Unknown";
            Label title = new Label(p.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0; -fx-font-size: 15px;");
            Label info = new Label("Student: " + sName);
            info.getStyleClass().add("info-label");

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(new VBox(4, title, info));
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            if (existing == null) {
                Button createBtn = new Button("Create Project");
                createBtn.getStyleClass().add("button-primary");
                createBtn.setOnAction(ev -> {
                    ProjectService.createProject(p.getProposalID(), coordinator.getUserID());
                    refreshContent(coordinator, stage);
                });
                row.getChildren().addAll(sp, createBtn);
            } else {
                Label done = new Label("Project Created");
                done.setStyle("-fx-text-fill: #2ed573; -fx-font-weight: bold;");
                row.getChildren().addAll(sp, done);
            }
            row.setPadding(new Insets(14));
            row.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-border-color: #2a2a4a; -fx-border-radius: 8;");
            approvedList.getChildren().add(row);
        }

        // --- Active Projects ---
        Label sec3 = new Label("Active Projects");
        sec3.getStyleClass().add("section-header");
        VBox projectsList = new VBox(10);

        List<Project> activeProjects = DataStore.getProjects().values().stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS)
                .collect(Collectors.toList());
        if (activeProjects.isEmpty()) {
            Label empty = new Label("No active projects.");
            empty.getStyleClass().add("info-label");
            projectsList.getChildren().add(empty);
        }
        for (Project proj : activeProjects) {
            User sup = DataStore.getUserByID(proj.getSupervisorID());
            String supName = sup != null ? sup.getFullName() : "Unassigned";
            float pct = ProjectService.getCompletionPct(proj.getProjectID());
            Label title = new Label(proj.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0;");
            Label info = new Label("Supervisor: " + supName + "  |  Progress: " + String.format("%.0f%%", pct));
            info.getStyleClass().add("info-label");

            Button evalBtn = new Button("Send to Evaluation");
            evalBtn.getStyleClass().add("button-secondary");
            evalBtn.setOnAction(ev -> {
                proj.setStatus(ProjectStatus.UNDER_EVALUATION);
                DataStore.updateProject(proj);
                refreshContent(coordinator, stage);
            });

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            HBox row = new HBox(12, new VBox(4, title, info), sp, evalBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14));
            row.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-border-color: #2a2a4a; -fx-border-radius: 8;");
            projectsList.getChildren().add(row);
        }

        contentBox.getChildren().addAll(topBar, new Separator(),
                sec1, proposalsList, new Separator(),
                sec2, approvedList, new Separator(),
                sec3, projectsList);
        contentBox.setPadding(new Insets(32));
        ViewAnimations.staggerChildren(contentBox);
    }

    public Scene getScene() { return scene; }
}