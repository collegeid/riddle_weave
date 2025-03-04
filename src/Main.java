import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Panggil scene Welcome
        WelcomeScene welcomeScene = new WelcomeScene(primaryStage);
        Scene scene = new Scene(welcomeScene.createContent(), 800, 600);

        // Set stage properties
        primaryStage.setTitle("Riddle Weave - Game Teka-Teki Interaktif");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}