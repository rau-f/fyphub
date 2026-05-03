package ui.student;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Student;
import service.ProposalService;

public class ProposalForm {
    private final Scene scene;

    public ProposalForm(Student student, Stage stage) {
        Label header = new Label("Submit New Proposal");
        header.getStyleClass().add("title-label");

        Label titleLbl = new Label("Proposal Title");
        titleLbl.getStyleClass().add("info-label");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter proposal title");
        titleField.setPrefWidth(500);

        Label descLbl = new Label("Description / Abstract");
        descLbl.getStyleClass().add("info-label");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe your project idea in detail...");
        descArea.setPrefWidth(500);
        descArea.setPrefRowCount(8);
        descArea.setWrapText(true);

        Label msgLabel = new Label();
        msgLabel.setVisible(false);
        msgLabel.setManaged(false);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(500);

        Button submitBtn = new Button("Submit Proposal");
        submitBtn.getStyleClass().add("button-primary");
        submitBtn.setPrefWidth(200);
        submitBtn.setPrefHeight(44);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("button-secondary");
        backBtn.setOnAction(e -> stage.setScene(new StudentDashboard(student, stage).getScene()));

        submitBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            if (title.isEmpty() || desc.isEmpty()) {
                msgLabel.setText("Please fill in both title and description.");
                msgLabel.getStyleClass().setAll("error-label");
                msgLabel.setVisible(true);
                msgLabel.setManaged(true);
                return;
            }
            try {
                ProposalService.submitProposal(student.getUserID(), title, desc);
                msgLabel.setText("Proposal submitted successfully!");
                msgLabel.getStyleClass().setAll("success-label");
                msgLabel.setVisible(true);
                msgLabel.setManaged(true);
                titleField.clear();
                descArea.clear();
                submitBtn.setDisable(true);
            } catch (IllegalArgumentException ex) {
                msgLabel.setText(ex.getMessage());
                msgLabel.getStyleClass().setAll("error-label");
                msgLabel.setVisible(true);
                msgLabel.setManaged(true);
            }
        });

        HBox buttons = new HBox(12, submitBtn, backBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(12, header, new Separator(),
                titleLbl, titleField, descLbl, descArea, msgLabel, buttons);
        form.setPadding(new Insets(32));
        form.setMaxWidth(600);
        form.getStyleClass().add("card");

        StackPane root = new StackPane(form);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0f0f1a;");

        this.scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public Scene getScene() { return scene; }
}
