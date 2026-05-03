package ui.student;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import repository.DataStore;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FeedbackView {
    private final Scene scene;

    public FeedbackView(Student student, Stage stage) {
        Label header = new Label("My Feedback");
        header.getStyleClass().add("title-label");

        List<Proposal> proposals = DataStore.getProposalsByStudentID(student.getUserID());
        List<Feedback> allFeedback = new ArrayList<>();
        for (Proposal p : proposals) {
            allFeedback.addAll(DataStore.getFeedbacksByProposalID(p.getProposalID()));
        }

        VBox feedbackList = new VBox(12);
        if (allFeedback.isEmpty()) {
            Label empty = new Label("No feedback received yet.");
            empty.getStyleClass().add("info-label");
            empty.setStyle("-fx-font-size: 16px;");
            feedbackList.getChildren().add(empty);
        } else {
            for (Feedback fb : allFeedback) {
                User sup = DataStore.getUserByID(fb.getSupervisorID());
                String supName = sup != null ? sup.getFullName() : "Unknown";

                Label supLabel = new Label("From: " + supName);
                supLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #00d4aa; -fx-font-size: 14px;");

                Label contentLabel = new Label(fb.getContent());
                contentLabel.setWrapText(true);
                contentLabel.getStyleClass().add("info-label");
                contentLabel.setStyle("-fx-text-fill: #c8c8dc; -fx-font-size: 14px;");

                Label scoreLabel = new Label(String.format("Score: %.1f / 10", fb.getScore()));
                scoreLabel.getStyleClass().add("score-label");

                Label dateLabel = new Label("Date: " +
                        fb.getGivenAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                dateLabel.getStyleClass().add("info-label");

                VBox card = new VBox(8, supLabel, contentLabel, scoreLabel, dateLabel);
                card.getStyleClass().add("card");
                card.setPadding(new Insets(16));
                feedbackList.getChildren().add(card);
            }
        }

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("button-secondary");
        backBtn.setOnAction(e -> stage.setScene(new StudentDashboard(student, stage).getScene()));

        VBox content = new VBox(16, header, new Separator(), feedbackList, backBtn);
        content.setPadding(new Insets(32));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");

        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public Scene getScene() { return scene; }
}
