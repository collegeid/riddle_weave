import javafx.animation.FadeTransition;
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
import java.util.List;
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
        Font honeyFont;
        try {
            honeyFont = Font.loadFont(getClass().getResourceAsStream("assets/fonts/honey.otf"), 14);
            if (honeyFont == null) {
                throw new Exception("Font honey.otf tidak dapat dimuat.");
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat font Honey.otf. Menggunakan font default.");
            honeyFont = Font.font("System", 14); // Fallback ke font sistem
        }
        System.out.println("Font yang digunakan: " + honeyFont.getName());

        // Background peta Indonesia (Fullscreen)
        ImageView mapImage;
        try {
            mapImage = new ImageView(new Image("file:assets/images/petaindo.jpeg"));
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar peta. Menggunakan placeholder.");
            mapImage = new ImageView(new Image(getClass().getResourceAsStream("assets/images/default_map.png")));
        }
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
        aceh.addQuestion("Ibu kota Aceh adalah?", "A. Banda Aceh", List.of("A. Banda Aceh", "B. Medan", "C. Jakarta", "D. Surabaya"));
        aceh.addQuestion("Gunung tertinggi di Aceh adalah?", "B. Gunung Leuser", List.of("A. Gunung Merapi", "B. Gunung Leuser", "C. Gunung Bromo", "D. Gunung Semeru"));
        provinceDataMap.put("Aceh", aceh);

        // Inisialisasi data untuk Jakarta
        ProvinceData jakarta = new ProvinceData("Jakarta");
        jakarta.addQuestion("Monumen Nasional terletak di?", "C. Jakarta", List.of("A. Bandung", "B. Surabaya", "C. Jakarta", "D. Yogyakarta"));
        jakarta.addQuestion("Ibu kota Indonesia adalah?", "C. Jakarta", List.of("A. Bandung", "B. Surabaya", "C. Jakarta", "D. Yogyakarta"));
        provinceDataMap.put("Jakarta", jakarta);

        // Inisialisasi data untuk Bandung
        ProvinceData bandung = new ProvinceData("Bandung");
        bandung.addQuestion("Gunung tertinggi di Jawa Barat adalah?", "D. Gunung Ciremai", List.of("A. Gunung Merapi", "B. Gunung Tangkuban Perahu", "C. Gunung Papandayan", "D. Gunung Ciremai"));
        bandung.addQuestion("Kawah Putih terletak di?", "B. Bandung", List.of("A. Bogor", "B. Bandung", "C. Garut", "D. Tasikmalaya"));
        provinceDataMap.put("Bandung", bandung);

        // Inisialisasi data untuk Surabaya
        ProvinceData surabaya = new ProvinceData("Surabaya");
        surabaya.addQuestion("Kapal Selam Monumen terletak di?", "A. Surabaya", List.of("A. Surabaya", "B. Jakarta", "C. Bandung", "D. Bali"));
        surabaya.addQuestion("Ibu kota Jawa Timur adalah?", "A. Surabaya", List.of("A. Surabaya", "B. Malang", "C. Kediri", "D. Madiun"));
        provinceDataMap.put("Surabaya", surabaya);
    }

    private void addProvinceMarker(Pane root, String cityName, double x, double y, Font font) {
        ProvinceData provinceData = provinceDataMap.get(cityName);
        boolean isCompleted = provinceData != null && provinceData.isCompleted(); // Periksa status provinsi
    
        Circle marker = new Circle(x, y, 10);
        marker.setFill(isCompleted ? Color.GREEN : Color.RED); // Hijau jika selesai, merah jika belum
        marker.setStroke(Color.WHITE);
        marker.setStrokeWidth(2);
    
        Text label = new Text(cityName);
        label.setFont(font);
        label.setFill(isCompleted ? Color.GREEN : Color.RED); // Warna teks sesuai status
        label.setX(x + 12);
        label.setY(y);
    
        if (!isCompleted) {
            marker.setOnMouseClicked(event -> {
                System.out.println("Memulai teka-teki di " + cityName);
                showGameplayScene(cityName);
            });
        } else {
            marker.setOnMouseClicked(event -> {
                System.out.println(cityName + " sudah selesai.");
            });
        }
    
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

    public void refreshAndShow() {
        // Muat ulang halaman peta dengan status terbaru
        primaryStage.setScene(createContent());
        primaryStage.show();
    }

   public void updateProvinceStatus(String cityName) {
    ProvinceData provinceData = provinceDataMap.get(cityName);
    if (provinceData != null && provinceData.isCompleted()) {
        System.out.println(cityName + " telah diselesaikan!");

        // Cari marker provinsi dan ubah warnanya menjadi hijau
        Pane root = (Pane) primaryStage.getScene().getRoot();
        for (var child : root.getChildren()) {
            if (child instanceof Circle) {
                Circle marker = (Circle) child;
                Text label = (Text) root.getChildren().stream()
                        .filter(node -> node instanceof Text && ((Text) node).getText().equals(cityName))
                        .findFirst()
                        .orElse(null);

                if (label != null && marker.getFill() == Color.RED) {
                    marker.setFill(Color.GREEN);
                    break;
                }
            }
        }
    }
}
}