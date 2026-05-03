package app;

import javafx.application.Application;
import javafx.stage.Stage;
import repository.DataStore;
import ui.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DataStore.initialize();

        primaryStage.setTitle("FYP Hub — Final Year Project Management");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
