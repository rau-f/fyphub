package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

        // --- Accent bar ---
        Region accentBar = new Region();
        accentBar.setPrefHeight(4);
        accentBar.setStyle("-fx-background-color: linear-gradient(to right, #6c5ce7, #00e5a0);");

        // --- Branding ---
        Label logo = new Label("FYP Hub");
        logo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label tagline = new Label("Final Year Project Management Platform");
        tagline.setStyle("-fx-font-size: 13px; -fx-text-fill: #6666aa; -fx-font-weight: normal;");
        VBox branding = new VBox(4, logo, tagline);
        branding.setAlignment(Pos.CENTER);
        branding.setPadding(new Insets(0, 0, 24, 0));

        // --- Form fields ---
        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("info-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(44);
        emailField.setMaxWidth(340);

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("info-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(44);
        passwordField.setMaxWidth(340);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("button-primary");
        loginBtn.setPrefWidth(340);
        loginBtn.setPrefHeight(46);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both email and password.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                ViewAnimations.pulse(errorLabel);
                return;
            }

            User user = AuthService.login(email, password);
            if (user == null) {
                errorLabel.setText("Invalid credentials. Please try again.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                ViewAnimations.pulse(errorLabel);
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

        // --- Demo accounts ---
        Label hint = new Label("Demo Accounts");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #5555aa; -fx-font-weight: bold;");
        Label accounts = new Label(
                "Student: student1@nu.edu.pk / 1234\n" +
                "Coordinator: coord1@nu.edu.pk / 1234\n" +
                "Supervisor: sup1@nu.edu.pk / 1234\n" +
                "Evaluator: eval1@nu.edu.pk / 1234\n" +
                "Admin: admin@nu.edu.pk / admin123");
        accounts.setStyle("-fx-font-size: 11px; -fx-text-fill: #444466; -fx-line-spacing: 3;");
        VBox hintBox = new VBox(6, hint, accounts);
        hintBox.setAlignment(Pos.CENTER);
        hintBox.setPadding(new Insets(16, 0, 0, 0));

        // --- Assemble card ---
        VBox formBox = new VBox(6,
                emailLabel, emailField,
                new Region() {{ setPrefHeight(4); }},
                passLabel, passwordField,
                new Region() {{ setPrefHeight(6); }},
                errorLabel, loginBtn, hintBox);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(340);

        VBox loginCard = new VBox(0, accentBar, new Region() {{ setPrefHeight(32); }}, branding, formBox);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(420);
        loginCard.setPadding(new Insets(0, 40, 40, 40));
        loginCard.setStyle(
                "-fx-background-color: rgba(14, 14, 36, 0.92);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: rgba(100, 100, 180, 0.1);" +
                "-fx-border-radius: 20;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 40, 0, 0, 12);");

        StackPane root = new StackPane(loginCard);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #08081a, #0d0d2b);");

        this.scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        ViewAnimations.slideUp(loginCard);
    }

    public Scene getScene() {
        return scene;
    }
}
