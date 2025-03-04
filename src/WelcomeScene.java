import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import javafx.scene.Parent;

public class WelcomeScene {
    private final Stage primaryStage;
    private static final String VIDEO_PATH = "assets/welcome.mp4";
    private static final String MODERATOR_IMAGE_PATH = "file:assets/images/mod.png";
    private static final String FONT_PATH = "assets/fonts/Honey.otf";
    private static final Color SPEECH_BUBBLE_COLOR = Color.web("#42A5F5");

    public WelcomeScene(Stage stage) {
        this.primaryStage = stage;
    }

    public Parent createContent() {
        StackPane root = new StackPane();

        // Background video (FULL SCREEN)
        MediaView mediaView = setupBackgroundVideo();

        // Judul game di tengah layar
        Label titleLabel = createTitleLabel();

        // Moderator & speech bubble (di pojok kiri)
        HBox moderatorLayout = createModeratorLayout();

        // Start button (di tengah bawah)
        Button startButton = createStartButton();

        // Layout utama
        VBox mainLayout = new VBox(50, titleLabel, startButton);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // Panel atas untuk moderator
        BorderPane topPanel = new BorderPane();
        topPanel.setLeft(moderatorLayout);
        topPanel.setPadding(new Insets(20));

        // Root container
        BorderPane mainContainer = new BorderPane();
        mainContainer.setTop(topPanel);
        mainContainer.setCenter(mainLayout);

        root.getChildren().addAll(mediaView, mainContainer);

        return root;
    }

    private MediaView setupBackgroundVideo() {
        Media media = new Media(new File(VIDEO_PATH).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // **FULLSCREEN OTOMATIS**
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(primaryStage.widthProperty());
        mediaView.fitHeightProperty().bind(primaryStage.heightProperty());

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        return mediaView;
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label("Riddle Weave");
        titleLabel.setFont(new Font("Arial", 48));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px; -fx-border-radius: 10px;");
        return titleLabel;
    }

    private HBox createModeratorLayout() {
        ImageView moderatorImage = new ImageView(new Image(MODERATOR_IMAGE_PATH));
        moderatorImage.setFitWidth(150);
        moderatorImage.setPreserveRatio(true);

        VBox speechBubble = createSpeechBubble("Selamat datang di Riddle Weave!\nKlik tombol 'Start' untuk memulai petualangan.");

        HBox layout = new HBox(10, moderatorImage, speechBubble);
        layout.setAlignment(Pos.CENTER_LEFT);
        return layout;
    }

    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-padding: 12 24; " +
                "-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold;"
        );

        startButton.setOnAction(event -> {
            MapScene mapScene = new MapScene(primaryStage);
            primaryStage.setScene(mapScene.createContent());
        });

        // Animasi masuk
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), startButton);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        return startButton;
    }

    private VBox createSpeechBubble(String message) {
        Text dialogText = new Text(message);
        dialogText.setFont(Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 16));
        dialogText.setFill(Color.WHITE);

        Rectangle bubbleBackground = new Rectangle(300, 90, SPEECH_BUBBLE_COLOR);
        bubbleBackground.setArcWidth(15);
        bubbleBackground.setArcHeight(15);

        StackPane bubble = new StackPane(bubbleBackground, dialogText);
        bubble.setPadding(new Insets(15));

        VBox speechBubble = new VBox(bubble);
        speechBubble.setAlignment(Pos.CENTER_LEFT);
        return speechBubble;
    }
}
