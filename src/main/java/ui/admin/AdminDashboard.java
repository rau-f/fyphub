package ui.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import repository.DataStore;
import repository.DatabaseManager;
import ui.LoginView;
import java.util.Map;

public class AdminDashboard {
    private final Scene scene;
    private final VBox contentBox = new VBox(20);

    public AdminDashboard(Admin admin, Stage stage) {
        refreshContent(admin, stage);
        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0f0f1a;");
        this.scene = new Scene(scroll, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void refreshContent(Admin admin, Stage stage) {
        contentBox.getChildren().clear();

        // --- Top Bar ---
        Label welcome = new Label("Welcome, " + admin.getFullName());
        welcome.getStyleClass().add("welcome-label");
        Label roleLabel = new Label("Admin Panel");
        roleLabel.getStyleClass().add("subtitle-label");
        VBox topText = new VBox(4, welcome, roleLabel);
        Button refreshBtn = new Button("\u21BB Refresh");
        refreshBtn.getStyleClass().add("button-secondary");
        refreshBtn.setOnAction(e -> refreshContent(admin, stage));
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("button-logout");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, topText, spacer, refreshBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // --- Section 1: Register User ---
        Label sec1 = new Label("Register New User");
        sec1.getStyleClass().add("section-header");

        Label roleLbl = new Label("Role:");
        roleLbl.getStyleClass().add("info-label");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("STUDENT", "SUPERVISOR", "COORDINATOR", "EVALUATOR");
        roleCombo.setPromptText("Select Role");
        roleCombo.setPrefWidth(200);

        Label nameLbl = new Label("Full Name:");
        nameLbl.getStyleClass().add("info-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        nameField.setPrefWidth(300);

        Label emailLbl = new Label("Email:");
        emailLbl.getStyleClass().add("info-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Email address");
        emailField.setPrefWidth(300);

        Label passLbl = new Label("Password:");
        passLbl.getStyleClass().add("info-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefWidth(300);

        Label deptLbl = new Label("Department:");
        deptLbl.getStyleClass().add("info-label");
        TextField deptField = new TextField();
        deptField.setPromptText("Department");
        deptField.setPrefWidth(300);

        // Role-specific fields
        Label extraLbl1 = new Label();
        extraLbl1.getStyleClass().add("info-label");
        TextField extraField1 = new TextField();
        extraField1.setPrefWidth(300);
        Label extraLbl2 = new Label();
        extraLbl2.getStyleClass().add("info-label");
        TextField extraField2 = new TextField();
        extraField2.setPrefWidth(300);

        VBox extraFields = new VBox(6, extraLbl1, extraField1, extraLbl2, extraField2);
        extraFields.setVisible(false);
        extraFields.setManaged(false);

        roleCombo.setOnAction(ev -> {
            String r = roleCombo.getValue();
            extraFields.setVisible(true);
            extraFields.setManaged(true);
            if ("STUDENT".equals(r)) {
                extraLbl1.setText("Student ID:"); extraField1.setPromptText("e.g. STU-003");
                extraLbl2.setText("CGPA:"); extraField2.setPromptText("e.g. 3.5");
            } else if ("SUPERVISOR".equals(r)) {
                extraLbl1.setText("Faculty ID:"); extraField1.setPromptText("e.g. FAC-003");
                extraLbl2.setText("Specialization:"); extraField2.setPromptText("e.g. AI");
            } else if ("COORDINATOR".equals(r)) {
                extraLbl1.setText("Coordinator ID:"); extraField1.setPromptText("e.g. COORD-002");
                extraLbl2.setVisible(false); extraLbl2.setManaged(false);
                extraField2.setVisible(false); extraField2.setManaged(false);
            } else if ("EVALUATOR".equals(r)) {
                extraLbl1.setText("Evaluator ID:"); extraField1.setPromptText("e.g. EVAL-002");
                extraLbl2.setText("Organization:"); extraField2.setPromptText("e.g. Tech Corp");
                extraLbl2.setVisible(true); extraLbl2.setManaged(true);
                extraField2.setVisible(true); extraField2.setManaged(true);
            }
        });

        Label regMsg = new Label();
        regMsg.setVisible(false);
        regMsg.setManaged(false);

        Button registerBtn = new Button("Register User");
        registerBtn.getStyleClass().add("button-primary");
        registerBtn.setPrefWidth(200);
        registerBtn.setOnAction(ev -> {
            String r = roleCombo.getValue();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText().trim();
            String dept = deptField.getText().trim();

            if (r == null || name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                regMsg.setText("Please fill all required fields.");
                regMsg.getStyleClass().setAll("error-label");
                regMsg.setVisible(true); regMsg.setManaged(true);
                return;
            }
            if (DataStore.getUserByEmail(email) != null) {
                regMsg.setText("Email already registered!");
                regMsg.getStyleClass().setAll("error-label");
                regMsg.setVisible(true); regMsg.setManaged(true);
                return;
            }

            User newUser = null;
            switch (r) {
                case "STUDENT":
                    float cgpa = 0;
                    try { cgpa = Float.parseFloat(extraField2.getText().trim()); } catch (Exception ignored) {}
                    newUser = new Student(email, pass, name, extraField1.getText().trim(), dept, cgpa);
                    break;
                case "SUPERVISOR":
                    newUser = new Supervisor(email, pass, name, extraField1.getText().trim(), dept, extraField2.getText().trim());
                    break;
                case "COORDINATOR":
                    newUser = new Coordinator(email, pass, name, extraField1.getText().trim(), dept);
                    break;
                case "EVALUATOR":
                    newUser = new Evaluator(email, pass, name, extraField1.getText().trim(), extraField2.getText().trim());
                    break;
            }

            if (newUser != null) {
                DataStore.saveUser(newUser);
                regMsg.setText("User registered successfully: " + name);
                regMsg.getStyleClass().setAll("success-label");
                regMsg.setVisible(true); regMsg.setManaged(true);
                nameField.clear(); emailField.clear(); passField.clear();
                deptField.clear(); extraField1.clear(); extraField2.clear();
                refreshContent(admin, stage);
            }
        });

        VBox registerCard = new VBox(8,
                new HBox(10, roleLbl, roleCombo),
                nameLbl, nameField, emailLbl, emailField,
                passLbl, passField, deptLbl, deptField,
                extraFields, regMsg, registerBtn);
        registerCard.getStyleClass().add("card");
        registerCard.setPadding(new Insets(20));
        registerCard.setMaxWidth(500);

        // --- Section 2: All Users ---
        Label sec2 = new Label("All Registered Users");
        sec2.getStyleClass().add("section-header");
        VBox usersList = new VBox(8);

        Map<String, User> allUsers = DataStore.getUsers();
        for (User u : allUsers.values()) {
            Label uName = new Label(u.getFullName());
            uName.setStyle("-fx-font-weight: bold; -fx-text-fill: #e0e0f0; -fx-font-size: 14px;");
            Label uInfo = new Label(u.getEmail() + "  |  " + u.getRole().name());
            uInfo.getStyleClass().add("info-label");
            HBox row = new HBox(12, new VBox(2, uName, uInfo));
            row.setPadding(new Insets(10));
            row.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-border-color: #2a2a4a; -fx-border-radius: 8;");
            usersList.getChildren().add(row);
        }

        // --- Section 3: Database Controls ---
        Label sec3 = new Label("Database Controls");
        sec3.getStyleClass().add("section-header");

        Button resetBtn = new Button("Reset Database to Defaults");
        resetBtn.getStyleClass().add("button-danger");
        resetBtn.setPrefWidth(280);
        resetBtn.setPrefHeight(44);

        Label resetMsg = new Label();
        resetMsg.setVisible(false);

        resetBtn.setOnAction(ev -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "This will DELETE all data and re-initialize with dummy data. Continue?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Reset Database");
            confirm.setHeaderText("Are you sure?");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    DatabaseManager.resetDatabase();
                    resetMsg.setText("Database reset successfully!");
                    resetMsg.getStyleClass().setAll("success-label");
                    resetMsg.setVisible(true);
                    refreshContent(admin, stage);
                }
            });
        });

        VBox dbCard = new VBox(12, resetBtn, resetMsg);
        dbCard.getStyleClass().add("card");
        dbCard.setPadding(new Insets(20));

        contentBox.getChildren().addAll(topBar, new Separator(),
                sec1, registerCard, new Separator(),
                sec2, usersList, new Separator(),
                sec3, dbCard);
        contentBox.setPadding(new Insets(32));
    }

    public Scene getScene() { return scene; }
}
