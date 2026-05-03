package service;

import model.Proposal;
import repository.DataStore;

import java.util.HashSet;
import java.util.Set;

public class PlagiarismService {

    public static float checkSimilarity(String newAbstract) {
        Set<String> newTokens = tokenize(newAbstract);
        float maxSimilarity = 0.0f;

        for (Proposal existing : DataStore.getProposals().values()) {
            Set<String> existingTokens = tokenize(existing.getDescription());
            float similarity = jaccardSimilarity(newTokens, existingTokens);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
            }
        }
        return maxSimilarity * 100.0f;
    }

    public static boolean isAboveThreshold(float score) {
        return score >= 20.0f;
    }

    private static Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        if (text == null || text.isEmpty()) return tokens;

        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        String[] words = cleaned.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    private static float jaccardSimilarity(Set<String> setA, Set<String> setB) {
        if (setA.isEmpty() && setB.isEmpty()) return 0.0f;

        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);

        if (union.isEmpty()) return 0.0f;
        return (float) intersection.size() / union.size();
    }
}
