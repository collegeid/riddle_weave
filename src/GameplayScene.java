import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Map;

public class GameplayScene {
    private Stage primaryStage;
    private String provinceName;
    private Map<String, ProvinceData> provinceDataMap;

    // Field instance untuk objek yang digunakan di dalam lambda
    private ImageView swiper;
    private ImageView cage;
    private Rectangle house;
    private Timeline timeline;
    private boolean isGameOver = false; // Flag untuk melacak status game over
    private int currentQuestionIndex = 0; // Indeks pertanyaan saat ini

    public GameplayScene(Stage primaryStage, String provinceName, Map<String, ProvinceData> provinceDataMap) {
        this.primaryStage = primaryStage;
        this.provinceName = provinceName;
        this.provinceDataMap = provinceDataMap;
    }

    public Scene createContent() {
        // Load custom font (Honey.otf) with fallback
        Font honeyFont;
        try {
            honeyFont = Font.loadFont(getClass().getResourceAsStream("assets/fonts/honey.otf"), 18);
            if (honeyFont == null) {
                throw new Exception("Font honey.otf tidak dapat dimuat.");
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat font Honey.otf. Menggunakan font default.");
            honeyFont = Font.font("System", 18); // Fallback ke font sistem
        }
        System.out.println("Font yang digunakan: " + honeyFont.getName());

        // Background gameplay
        ImageView background;
        try {
            background = new ImageView(new Image(new File("assets/images/gameplay.jpg").toURI().toString()));
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar background. Menggunakan placeholder.");
            background = new ImageView(new Image(getClass().getResourceAsStream("assets/images/gameplay.jpg")));
        }
        background.setFitWidth(800);
        background.setFitHeight(600);

        // Swiper character
        try {
            swiper = new ImageView(new Image(new File("assets/images/swiper.png").toURI().toString()));
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar swiper. Menggunakan placeholder.");
            swiper = new ImageView(new Image(getClass().getResourceAsStream("assets/images/swiper.png")));
        }
        swiper.setFitWidth(50);
        swiper.setFitHeight(50);
        swiper.setX(-50); // Start off-screen
        swiper.setY(400);

        // House (target for Swiper)
        house = new Rectangle(700, 380, 100, 100);
        house.setFill(Color.BROWN);

        // Cage (falls from the sky when correct answer is given)
        try {
            cage = new ImageView(new Image(new File("assets/images/cage.png").toURI().toString()));
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar cage. Menggunakan placeholder.");
            cage = new ImageView(new Image(getClass().getResourceAsStream("assets/images/cage.png")));
        }
        cage.setFitWidth(100);
        cage.setFitHeight(100);
        cage.setX(650); // Above the house
        cage.setY(-100); // Off-screen initially

        // Question and answer
        Label questionLabel = new Label(getCurrentQuestion());
        questionLabel.setFont(honeyFont);
        questionLabel.setTextFill(Color.WHITE);
        questionLabel.setTranslateX(50);
        questionLabel.setTranslateY(50);

        // Multiple choice buttons
        Button optionA = new Button("A. Banda Aceh");
        Button optionB = new Button("B. Medan");
        Button optionC = new Button("C. Jakarta");
        Button optionD = new Button("D. Surabaya");

        optionA.setFont(honeyFont);
        optionB.setFont(honeyFont);
        optionC.setFont(honeyFont);
        optionD.setFont(honeyFont);

        optionA.setTranslateX(50);
        optionA.setTranslateY(150);
        optionB.setTranslateX(200);
        optionB.setTranslateY(150);
        optionC.setTranslateX(50);
        optionC.setTranslateY(200);
        optionD.setTranslateX(200);
        optionD.setTranslateY(200);

        // Timer visual (progress bar)
        Rectangle progressBar = new Rectangle(50, 550, 700, 20);
        progressBar.setFill(Color.GRAY);
        Rectangle progressIndicator = new Rectangle(50, 550, 700, 20);
        progressIndicator.setFill(Color.GREEN);

        // Timer and Swiper movement
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            if (isGameOver) return; // Hentikan semua proses jika game over

            swiper.setX(swiper.getX() + 2); // Move Swiper to the right
            double progressWidth = 700 - ((swiper.getX() + 50) / (house.getX() + 100)) * 700;
            progressIndicator.setWidth(progressWidth);

            if (swiper.getX() >= house.getX()) {
                gameOver(false); // Game over if Swiper reaches the house
                isGameOver = true; // Set flag game over
                timeline.stop(); // Stop timeline
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Root layout
        Pane root = new Pane(background, swiper, house, cage, questionLabel, optionA, optionB, optionC, optionD, progressBar, progressIndicator);

        // Event handling for answer submission
        optionA.setOnAction(event -> handleAnswer(optionA.getText()));
        optionB.setOnAction(event -> handleAnswer(optionB.getText()));
        optionC.setOnAction(event -> handleAnswer(optionC.getText()));
        optionD.setOnAction(event -> handleAnswer(optionD.getText()));

        return new Scene(root, 800, 600);
    }

    private void handleAnswer(String selectedAnswer) {
        ProvinceData provinceData = provinceDataMap.get(provinceName);
        if (selectedAnswer.equals(getCorrectAnswer())) {
            provinceData.incrementCorrectAnswers();
            playSound("menang.mp3");
        } else {
            playSound("kalah.mp3");
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < provinceData.getTotalQuestions()) {
            // Lanjutkan ke pertanyaan berikutnya
            primaryStage.setScene(createContent());
        } else {
            // Semua pertanyaan selesai
            if (provinceData.isCompleted()) {
                primaryStage.setScene(new MapScene(primaryStage).createContent());
            } else {
                showNotificationAndReturnToMap("Sayang sekali! Anda belum menyelesaikan semua pertanyaan.", false);
            }
        }
    }

    private void gameOver(boolean wrongAnswer) {
        if (isGameOver) return; // Jangan jalankan lagi jika game sudah over

        boolean soundPlayed = playSound(wrongAnswer ? "kalah.mp3" : "kalah.mp3");

        // Tampilkan pop-up jika audio gagal dimuat
        if (!soundPlayed) {
            Platform.runLater(() -> {
                showAlert("Gagal memuat suara!", "Silakan cek file audio di folder assets.");
            });
        }

        System.out.println(wrongAnswer ? "Jawaban salah!" : "Waktu habis!");
        showNotificationAndReturnToMap(wrongAnswer ? "Sayang sekali! Jawaban Anda salah." : "Waktu habis!", false);
    }

    private void showNotificationAndReturnToMap(String message, boolean isWin) {
        Platform.runLater(() -> {
            Alert alert = new Alert(isWin ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
            alert.setTitle(isWin ? "Selamat!" : "Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });

        // Delay sebelum beralih ke halaman map
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            primaryStage.setScene(new MapScene(primaryStage).createContent());
        }));
        delayTimeline.play();
    }

    private String getCurrentQuestion() {
        ProvinceData provinceData = provinceDataMap.get(provinceName);
        return provinceData.getQuestion(currentQuestionIndex);
    }

    private String getCorrectAnswer() {
        ProvinceData provinceData = provinceDataMap.get(provinceName);
        return provinceData.getCorrectAnswer(currentQuestionIndex);
    }

    private void cageFallAnimation(ImageView cage, ImageView swiper) {
        Timeline fallTimeline = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            cage.setY(cage.getY() + 5); // Drop the cage
            if (cage.getY() >= swiper.getY()) {
                cage.setY(swiper.getY()); // Stop at Swiper's position
                ((Timeline) event.getSource()).stop();
            }
        }));
        fallTimeline.setCycleCount(Timeline.INDEFINITE);
        fallTimeline.play();
    }

    private boolean playSound(String soundFile) {
        try {
            String fullPath = new File("assets/audio/" + soundFile).toURI().toString();
            System.out.println("Memuat suara dari: " + fullPath); // Debugging

            AudioClip sound = new AudioClip(fullPath);
            sound.play();
            return true; // Sukses memainkan audio
        } catch (Exception e) {
            System.err.println("Gagal memuat suara: " + soundFile);
            e.printStackTrace(); // Tampilkan detail kesalahan
            return false; // Gagal memainkan audio
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}