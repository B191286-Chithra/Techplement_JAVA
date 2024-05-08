import java.util.*;

public class Quiz {
    private Map<String, List<String>> questions; // Map question text to list of options
    private Map<String, String> correctAnswers; // Map question text to correct answer

    public Quiz() {
        questions = new LinkedHashMap<>(); // LinkedHashMap to preserve insertion order
        correctAnswers = new HashMap<>();
    }

    public void addQuestion(String question, List<String> options, String correctAnswer) {
        questions.put(question, options);
        correctAnswers.put(question, correctAnswer);
    }

    public Map<String, List<String>> getQuestions() {
        return questions;
    }

    public Map<String, String> getCorrectAnswers() {
        return correctAnswers;
    }
}
