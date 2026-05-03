package ui.student;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import repository.DataStore;
import service.ProjectService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MilestoneView {
    private final Scene scene;

    public MilestoneView(Student student, Project project, Stage stage) {
        Label header = new Label("Milestones — " + project.getTitle());
        header.getStyleClass().add("title-label");

        float pct = ProjectService.getCompletionPct(project.getProjectID());
        ProgressBar bar = new ProgressBar(pct / 100.0);
        bar.setPrefWidth(400);
        Label pctLabel = new Label(String.format("Overall Progress: %.0f%%", pct));
        pctLabel.getStyleClass().add("score-label");
        HBox progressRow = new HBox(12, bar, pctLabel);
        progressRow.setAlignment(Pos.CENTER_LEFT);

        VBox milestonesBox = new VBox(10);
        Runnable refresh = () -> {
            milestonesBox.getChildren().clear();
            float newPct = ProjectService.getCompletionPct(project.getProjectID());
            bar.setProgress(newPct / 100.0);
            pctLabel.setText(String.format("Overall Progress: %.0f%%", newPct));

            List<Milestone> milestones = DataStore.getMilestonesByProjectID(project.getProjectID());
            if (milestones.isEmpty()) {
                Label empty = new Label("No milestones yet. Add one below!");
                empty.getStyleClass().add("info-label");
                milestonesBox.getChildren().add(empty);
            }
            for (Milestone m : milestones) {
                Label icon = new Label(m.isCompleted() ? "\u2705" : "\u2B1C");
                Label title = new Label(m.getTitle());
                title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e0e0f0;");
                Label deadline = new Label("Deadline: " +
                        m.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                deadline.getStyleClass().add("info-label");

                VBox info = new VBox(2, title, deadline);
                HBox.setHgrow(info, Priority.ALWAYS);

                HBox row = new HBox(12, icon, info);
                row.setAlignment(Pos.CENTER_LEFT);

                if (!m.isCompleted()) {
                    Button completeBtn = new Button("Mark Complete");
                    completeBtn.getStyleClass().add("button-success");
                    completeBtn.setOnAction(ev -> {
                        ProjectService.completeMilestone(m.getMilestoneID());
                        stage.setScene(new MilestoneView(student, project, stage).getScene());
                    });
                    row.getChildren().add(completeBtn);
                } else {
                    Label done = new Label("Completed");
                    done.setStyle("-fx-text-fill: #2ed573; -fx-font-weight: bold;");
                    row.getChildren().add(done);
                }

                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; " +
                        "-fx-border-color: #2a2a4a; -fx-border-radius: 8;");
                milestonesBox.getChildren().add(row);
            }
        };
        refresh.run();

        // Add Milestone Form
        Label addHeader = new Label("Add New Milestone");
        addHeader.getStyleClass().add("section-header");
        TextField newTitle = new TextField();
        newTitle.setPromptText("Milestone title");
        newTitle.setPrefWidth(300);
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Deadline date");
        datePicker.setStyle("-fx-background-color: #12122a; -fx-text-fill: #e0e0f0;");

        Button addBtn = new Button("Add Milestone");
        addBtn.getStyleClass().add("button-primary");
        Label addMsg = new Label();
        addMsg.setVisible(false);

        addBtn.setOnAction(e -> {
            String t = newTitle.getText().trim();
            if (t.isEmpty() || datePicker.getValue() == null) {
                addMsg.setText("Fill in title and deadline.");
                addMsg.getStyleClass().setAll("error-label");
                addMsg.setVisible(true);
                return;
            }
            LocalDateTime dl = datePicker.getValue().atStartOfDay();
            ProjectService.addMilestone(project.getProjectID(), t, dl);
            stage.setScene(new MilestoneView(student, project, stage).getScene());
        });

        HBox addRow = new HBox(10, newTitle, datePicker, addBtn);
        addRow.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("button-secondary");
        backBtn.setOnAction(e -> stage.setScene(new StudentDashboard(student, stage).getScene()));

        VBox content = new VBox(16, header, progressRow, new Separator(),
                milestonesBox, new Separator(), addHeader, addRow, addMsg, backBtn);
        content.setPadding(new Insets(32));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");

        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public Scene getScene() { return scene; }
}
