import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

public class ApiClient {
    public static String summarizeViaAPI(String inputText) {
        try {
            URL url = new URL("https://api-inference.huggingface.co/models/facebook/bart-large-cnn");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer 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"); // ganti token
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject payload = new JsonObject();
            payload.addProperty("inputs", inputText);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }

            InputStream is = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonArray arr = JsonParser.parseString(response.toString()).getAsJsonArray();
            return arr.get(0).getAsJsonObject().get("summary_text").getAsString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
