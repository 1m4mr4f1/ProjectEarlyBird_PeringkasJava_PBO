import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/ringkasan_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ignored) {}
    }

    public static void simpanRingkasan(String input, String output) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO ringkasan (input, output) VALUES (?, ?)");
            stmt.setString(1, input);
            stmt.setString(2, output);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getRiwayat() {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ringkasan ORDER BY id DESC");
            while (rs.next()) {
                sb.append("Input: ").append(rs.getString("input")).append("\n");
                sb.append("Output: ").append(rs.getString("output")).append("\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
