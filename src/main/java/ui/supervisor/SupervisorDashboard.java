package ui.supervisor;

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

public class SupervisorDashboard {
    private final Scene scene;
    private final VBox contentBox = new VBox(20);

    public SupervisorDashboard(Supervisor supervisor, Stage stage) {
        refreshContent(supervisor, stage);
        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");
        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void refreshContent(Supervisor supervisor, Stage stage) {
        contentBox.getChildren().clear();
        Label welcome = new Label("Welcome, " + supervisor.getFullName());
        welcome.getStyleClass().add("welcome-label");
        Label role = new Label("Supervisor Dashboard");
        role.getStyleClass().add("subtitle-label");
        VBox topText = new VBox(4, welcome, role);
        Button refreshBtn = new Button("\u21BB Refresh");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshContent(supervisor, stage));
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-logout");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, topText, spacer, refreshBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label sec1 = new Label("Proposals to Review");
        sec1.getStyleClass().add("section-header");
        VBox reviewList = new VBox(12);
        List<Proposal> toReview = DataStore.getProposals().values().stream()
                .filter(p -> p.getStatus() == ProposalStatus.UNDER_REVIEW &&
                        supervisor.getUserID().equals(p.getSupervisorID()))
                .collect(Collectors.toList());
        if (toReview.isEmpty()) {
            Label empty = new Label("No proposals to review.");
            empty.getStyleClass().add("info-label");
            reviewList.getChildren().add(empty);
        }
        for (Proposal p : toReview) {
            User student = DataStore.getUserByID(p.getStudentID());
            String sName = student != null ? student.getFullName() : "Unknown";
            Label title = new Label(p.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0; -fx-font-size: 16px;");
            Label by = new Label("By: " + sName);
            by.getStyleClass().add("info-label");
            Label desc = new Label(p.getDescription());
            desc.setWrapText(true);
            desc.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 13px;");
            desc.setMaxWidth(800);
            Button approveBtn = new Button("Approve");
            approveBtn.getStyleClass().add("button-success");
            approveBtn.setOnAction(ev -> {
                ProposalService.approveProposal(p.getProposalID());
                refreshContent(supervisor, stage);
            });
            TextArea fbArea = new TextArea();
            fbArea.setPromptText("Enter feedback comments...");
            fbArea.setPrefRowCount(3);
            fbArea.setPrefWidth(500);
            fbArea.setWrapText(true);
            Spinner<Integer> scoreSpinner = new Spinner<>(0, 10, 5);
            scoreSpinner.setPrefWidth(80);
            scoreSpinner.setEditable(true);
            Label scoreLbl = new Label("Score (0-10):");
            scoreLbl.getStyleClass().add("info-label");
            Button revisionBtn = new Button("Request Revision");
            revisionBtn.getStyleClass().add("button-danger");
            revisionBtn.setOnAction(ev -> {
                String content = fbArea.getText().trim();
                if (content.isEmpty()) { fbArea.setPromptText("Please enter feedback!"); return; }
                ProposalService.requestRevision(p.getProposalID(), content,
                        scoreSpinner.getValue(), supervisor.getUserID());
                refreshContent(supervisor, stage);
            });
            HBox scoreRow = new HBox(8, scoreLbl, scoreSpinner);
            scoreRow.setAlignment(Pos.CENTER_LEFT);
            HBox btnRow = new HBox(10, approveBtn, revisionBtn);
            VBox card = new VBox(10, title, by, desc, new Separator(), fbArea, scoreRow, btnRow);
            card.setPadding(new Insets(16));
            card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 10; -fx-border-color: #2a2a4a; -fx-border-radius: 10;");
            reviewList.getChildren().add(card);
        }

        Label sec2 = new Label("Supervised Projects");
        sec2.getStyleClass().add("section-header");
        VBox projectsList = new VBox(10);
        List<Project> supervised = DataStore.getProjects().values().stream()
                .filter(p -> supervisor.getUserID().equals(p.getSupervisorID()))
                .collect(Collectors.toList());
        if (supervised.isEmpty()) {
            Label empty = new Label("No projects under supervision.");
            empty.getStyleClass().add("info-label");
            projectsList.getChildren().add(empty);
        }
        for (Project proj : supervised) {
            float pct = ProjectService.getCompletionPct(proj.getProjectID());
            Label title = new Label(proj.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0;");
            Label status = new Label("Status: " + proj.getStatus().toString().replace("_", " "));
            status.getStyleClass().add("info-label");
            ProgressBar bar = new ProgressBar(pct / 100.0);
            bar.setPrefWidth(200);
            Label pctLbl = new Label(String.format("%.0f%%", pct));
            pctLbl.setStyle("-fx-text-fill: #6c63ff; -fx-font-weight: bold;");
            HBox progRow = new HBox(10, bar, pctLbl);
            progRow.setAlignment(Pos.CENTER_LEFT);
            VBox card = new VBox(6, title, status, progRow);
            card.setPadding(new Insets(14));
            card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-border-color: #2a2a4a; -fx-border-radius: 8;");
            projectsList.getChildren().add(card);
        }

        contentBox.getChildren().addAll(topBar, new Separator(), sec1, reviewList, new Separator(), sec2, projectsList);
        contentBox.setPadding(new Insets(32));
        ViewAnimations.staggerChildren(contentBox);
    }

    public Scene getScene() { return scene; }
}
