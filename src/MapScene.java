import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class MapScene {
    private Stage primaryStage;
    private Map<String, ProvinceData> provinceDataMap = new HashMap<>();

    public MapScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeProvinceData();
    }

    public Scene createContent() {
        // Ambil ukuran layar utama
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Load custom font (Honey.otf)
        Font honeyFont = Font.loadFont(getClass().getResourceAsStream("assets/fonts/honey.otf"), 14);

        // Background peta Indonesia (Fullscreen)
        ImageView mapImage = new ImageView(new Image("file:assets/images/petaindo.jpeg"));
        mapImage.setFitWidth(screenWidth);
        mapImage.setFitHeight(screenHeight);
        mapImage.setPreserveRatio(false);

        // Root layout
        Pane root = new Pane(mapImage);

        // Tambahkan marker pada peta
        addProvinceMarker(root, "Aceh", 110, 210, honeyFont);
        addProvinceMarker(root, "Jakarta", 440, 360, honeyFont);
        addProvinceMarker(root, "Bandung", 620, 340, honeyFont);
        addProvinceMarker(root, "Surabaya", 580, 430, honeyFont);

        // Scene fullscreen
        Scene scene = new Scene(root, screenWidth, screenHeight);

        // Atur stage agar fullscreen
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint(""); // Hilangkan teks "Tekan ESC untuk keluar"
        primaryStage.setMaximized(true); // Pastikan stage penuh
        return scene;
    }

    private void initializeProvinceData() {
        // Inisialisasi data untuk Aceh
        ProvinceData aceh = new ProvinceData("Aceh");
        aceh.addQuestion("Ibu kota Aceh adalah?", "A. Banda Aceh");
        aceh.addQuestion("Gunung tertinggi di Aceh adalah?", "B. Gunung Leuser");
        provinceDataMap.put("Aceh", aceh);

        // Inisialisasi data untuk Jakarta
        ProvinceData jakarta = new ProvinceData("Jakarta");
        jakarta.addQuestion("Monumen Nasional terletak di?", "C. Jakarta");
        jakarta.addQuestion("Ibu kota Indonesia adalah?", "C. Jakarta");
        provinceDataMap.put("Jakarta", jakarta);

        // Inisialisasi data untuk Bandung
        ProvinceData bandung = new ProvinceData("Bandung");
        bandung.addQuestion("Gunung tertinggi di Jawa Barat adalah?", "D. Gunung Ciremai");
        bandung.addQuestion("Kawah Putih terletak di?", "B. Bandung");
        provinceDataMap.put("Bandung", bandung);

        // Inisialisasi data untuk Surabaya
        ProvinceData surabaya = new ProvinceData("Surabaya");
        surabaya.addQuestion("Kapal Selam Monumen terletak di?", "A. Surabaya");
        surabaya.addQuestion("Ibu kota Jawa Timur adalah?", "A. Surabaya");
        provinceDataMap.put("Surabaya", surabaya);
    }

    private void addProvinceMarker(Pane root, String cityName, double x, double y, Font font) {
        ProvinceData provinceData = provinceDataMap.get(cityName);
        boolean isCompleted = provinceData != null && provinceData.isCompleted();

        Circle marker = new Circle(x, y, 10);
        marker.setFill(isCompleted ? Color.GREEN : Color.RED);
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(2);

        Text label = new Text(cityName);
        label.setFont(font);
        label.setFill(isCompleted ? Color.GREEN : Color.RED);
        label.setX(x + 12);
        label.setY(y);

        marker.setOnMouseEntered(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.2), marker);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.play();
        });

        marker.setOnMouseExited(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.2), marker);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        marker.setOnMouseClicked(event -> {
            if (!isCompleted) {
                System.out.println("Memulai teka-teki di " + cityName);
                showGameplayScene(cityName);
            } else {
                System.out.println(cityName + " sudah selesai.");
            }
        });

        root.getChildren().addAll(marker, label);
    }

    private void showGameplayScene(String cityName) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            GameplayScene gameplayScene = new GameplayScene(primaryStage, cityName, provinceDataMap);
            primaryStage.setScene(gameplayScene.createContent());
        });
        fadeOut.play();
    }

    public void updateProvinceStatus(String cityName) {
        ProvinceData provinceData = provinceDataMap.get(cityName);
        if (provinceData != null && provinceData.isCompleted()) {
            System.out.println(cityName + " telah diselesaikan!");
        }
    }
}