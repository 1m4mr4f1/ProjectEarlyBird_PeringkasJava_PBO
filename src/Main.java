import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private TextArea inputArea = new TextArea();
    private TextArea outputArea = new TextArea();
    private ComboBox<String> methodDropdown = new ComboBox<>();

    @Override
    public void start(Stage primaryStage) {
        // Inisialisasi komponen UI
        methodDropdown.getItems().addAll("Rule-based (Wiki)", "API-based");
        methodDropdown.setValue("Rule-based (Wiki)");

        Button summarizeButton = new Button("Ringkas");
        Button saveButton = new Button("Simpan");
        Button historyButton = new Button("Riwayat");

        // Event handler tombol
        summarizeButton.setOnAction(e -> ringkas());
        saveButton.setOnAction(e -> {
            DatabaseHelper.simpanRingkasan(inputArea.getText(), outputArea.getText());
            showAlert("Sukses", "Ringkasan berhasil disimpan.");
        });
        historyButton.setOnAction(e -> outputArea.setText(DatabaseHelper.getRiwayat()));

        // Styling TextArea
        inputArea.setWrapText(true);
        inputArea.setPromptText("Masukkan teks yang ingin diringkas di sini...");
        outputArea.setWrapText(true);
        outputArea.setEditable(false);

        // Layout
        VBox mainLayout = new VBox(10,
            new Label("📝 Input Teks:"), inputArea,
            new Label("📌 Metode Ringkasan:"), methodDropdown, summarizeButton,
            new Label("📃 Hasil Ringkasan:"), outputArea,
            new HBox(10, saveButton, historyButton)
        );
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

        Scene scene = new Scene(mainLayout, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("📚 Peringkas AI Teks - JavaFX");
        primaryStage.show();

        // Load word vector (sekali saat awal jika rule-based wiki digunakan)
        try {
            RuleBasedEmbedSummarizer.loadWordVectors(); // load vektor wiki
        } catch (Exception e) {
            showAlert("Error", "Gagal memuat model wiki: " + e.getMessage());
        }
    }

    private void ringkas() {
        String method = methodDropdown.getValue();
        String inputText = inputArea.getText();
        String result = "";

        if (inputText.trim().isEmpty()) {
            showAlert("Peringatan", "Input teks tidak boleh kosong.");
            return;
        }

        try {
            if (method.startsWith("Rule-based")) {
                result = RuleBasedEmbedSummarizer.summarize(inputText, 3); // 3 kalimat
            } else {
                result = ApiClient.summarizeViaAPI(inputText);
            }
            outputArea.setText(result);
        } catch (Exception ex) {
            showAlert("Error", "Terjadi kesalahan saat merangkum:\n" + ex.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
