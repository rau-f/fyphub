package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.*;
import service.AuthService;
import ui.admin.AdminDashboard;
import ui.coordinator.CoordinatorDashboard;
import ui.evaluator.EvaluatorDashboard;
import ui.student.StudentDashboard;
import ui.supervisor.SupervisorDashboard;

public class LoginView {

    private final Scene scene;

    public LoginView(Stage stage) {
        // --- Brand Section ---
        Label brandIcon = new Label("\u2B22");
        brandIcon.setStyle("-fx-font-size: 48px; -fx-text-fill: #6c63ff;");

        Label brandTitle = new Label("FYP Hub");
        brandTitle.getStyleClass().add("title-label");
        brandTitle.setStyle("-fx-font-size: 36px; -fx-text-fill: #ffffff;");

        Label brandSubtitle = new Label("Final Year Project Management");
        brandSubtitle.getStyleClass().add("subtitle-label");

        VBox brandBox = new VBox(8, brandIcon, brandTitle, brandSubtitle);
        brandBox.setAlignment(Pos.CENTER);

        // --- Login Form ---
        Label formTitle = new Label("Sign In");
        formTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("info-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(320);
        emailField.setId("emailField");

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("info-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(320);
        passwordField.setId("passwordField");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setPrefWidth(320);
        loginBtn.setPrefHeight(44);
        loginBtn.setId("loginButton");

        // --- Login action ---
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both email and password");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            User user = AuthService.login(email, password);

            if (user == null) {
                errorLabel.setText("Invalid email or password");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            switch (user.getRole()) {
                case STUDENT:
                    stage.setScene(new StudentDashboard((Student) user, stage).getScene());
                    break;
                case COORDINATOR:
                    stage.setScene(new CoordinatorDashboard((Coordinator) user, stage).getScene());
                    break;
                case SUPERVISOR:
                    stage.setScene(new SupervisorDashboard((Supervisor) user, stage).getScene());
                    break;
                case EVALUATOR:
                    stage.setScene(new EvaluatorDashboard((Evaluator) user, stage).getScene());
                    break;
                case ADMIN:
                    stage.setScene(new AdminDashboard((Admin) user, stage).getScene());
                    break;
            }
        });

        passwordField.setOnAction(e -> loginBtn.fire());

        // --- Demo accounts hint ---
        Label hintLabel = new Label("Demo: student1@nu.edu.pk / 1234");
        hintLabel.setStyle("-fx-text-fill: #555577; -fx-font-size: 12px;");

        VBox formFields = new VBox(6,
                formTitle,
                new Region() {
                    {
                        setPrefHeight(12);
                    }
                },
                emailLabel, emailField,
                new Region() {
                    {
                        setPrefHeight(4);
                    }
                },
                passwordLabel, passwordField,
                new Region() {
                    {
                        setPrefHeight(4);
                    }
                },
                errorLabel,
                loginBtn,
                hintLabel);
        formFields.setAlignment(Pos.CENTER_LEFT);
        formFields.setPadding(new Insets(32));
        formFields.setMaxWidth(400);
        formFields.getStyleClass().add("card");

        // --- Center layout ---
        VBox centerBox = new VBox(32, brandBox, formFields);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        StackPane root = new StackPane(centerBox);
        root.setStyle("-fx-background-color: #0f0f1a;");

        this.scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}
