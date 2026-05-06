package ui;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class ViewAnimations {

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.play();
    }

    public static void slideUp(Node node) {
        node.setOpacity(0);
        node.setTranslateY(30);
        TranslateTransition tt = new TranslateTransition(Duration.millis(450), node);
        tt.setFromY(30);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(450), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(tt, ft).play();
    }

    public static void staggerChildren(javafx.scene.layout.Pane parent) {
        int i = 0;
        for (Node child : parent.getChildren()) {
            child.setOpacity(0);
            child.setTranslateY(20);
            int delay = i * 60;
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> {
                TranslateTransition tt = new TranslateTransition(Duration.millis(350), child);
                tt.setFromY(20);
                tt.setToY(0);
                tt.setInterpolator(Interpolator.EASE_OUT);
                FadeTransition ft = new FadeTransition(Duration.millis(350), child);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.setInterpolator(Interpolator.EASE_OUT);
                new ParallelTransition(tt, ft).play();
            });
            pause.play();
            i++;
        }
    }

    public static void pulse(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), node);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
}
