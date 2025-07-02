import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RuleBasedEmbedSummarizer {
    private static final String EMBEDDING_PATH = "model/wiki-news-300d-1M.vec";

    private static Map<String, double[]> wordVectors = new HashMap<>();

    // Load word vectors dari file
    public static void loadWordVectors() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(EMBEDDING_PATH));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length < 10) continue; // skip header/empty
            String word = parts[0];
            double[] vec = new double[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                vec[i - 1] = Double.parseDouble(parts[i]);
            }
            wordVectors.put(word, vec);
        }
        br.close();
    }

    // Hitung rata-rata vektor untuk satu kalimat
    private static double[] getSentenceVector(String sentence) {
        String[] words = sentence.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        List<double[]> vectors = new ArrayList<>();
        for (String word : words) {
            if (wordVectors.containsKey(word)) {
                vectors.add(wordVectors.get(word));
            }
        }

        if (vectors.isEmpty()) return new double[300];

        double[] avg = new double[300];
        for (double[] vec : vectors) {
            for (int i = 0; i < 300; i++) {
                avg[i] += vec[i];
            }
        }
        for (int i = 0; i < 300; i++) {
            avg[i] /= vectors.size();
        }
        return avg;
    }

    // Cosine similarity
    private static double cosine(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }

    // Ringkas
    public static String summarize(String text, int topN) {
        String[] sentences = text.split("(?<=[.!?])\\s+");
        int n = sentences.length;
        double[][] similarity = new double[n][n];
        double[][] scores = new double[n][1];

        double[][] vectors = new double[n][];
        for (int i = 0; i < n; i++) {
            vectors[i] = getSentenceVector(sentences[i]);
        }

        // bangun matrix similaritas
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    similarity[i][j] = cosine(vectors[i], vectors[j]);
                }
            }
        }

        // pagerank sederhana (skor awal = 1)
        double[] ranks = new double[n];
        Arrays.fill(ranks, 1.0);
        for (int iter = 0; iter < 10; iter++) {
            double[] newRanks = new double[n];
            for (int i = 0; i < n; i++) {
                double sum = 0;
                for (int j = 0; j < n; j++) {
                    if (i != j) sum += similarity[j][i] * ranks[j];
                }
                newRanks[i] = 0.15 + 0.85 * sum;
            }
            ranks = newRanks;
        }

        // ambil N kalimat tertinggi
       // ambil N kalimat tertinggi
List<Integer> topIndexes = new ArrayList<>();
for (int i = 0; i < n; i++) topIndexes.add(i);
final double[] ranksFinal = ranks;
topIndexes.sort((a, b) -> Double.compare(ranksFinal[b], ranksFinal[a]));

List<String> topSentences = topIndexes.subList(0, Math.min(topN, n))
        .stream().sorted().map(i -> sentences[i]).collect(Collectors.toList());

return String.join(" ", topSentences);

    }
}
