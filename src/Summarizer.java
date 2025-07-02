public class Summarizer {
    public static String ruleBasedSummary(String text) {
        String[] sentences = text.split("\\. ");
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < Math.min(3, sentences.length); i++) {
            summary.append(sentences[i]).append(". ");
        }
        return summary.toString();
    }
}
