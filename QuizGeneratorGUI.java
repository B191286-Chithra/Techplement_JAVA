import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;

public class QuizGeneratorGUI extends JFrame implements ActionListener {
    private JButton adminButton, studentButton, backButton;
    private JTextField nameField;
    private Quiz quiz;
    private JPanel panel;
    private ArrayList<Student> students;

    private enum UserType { ADMIN, STUDENT }

    private UserType currentUserType;

    public QuizGeneratorGUI() {
        super("Quiz Generator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quiz Generator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel);

        JLabel descriptionLabel = new JLabel("Select User Type:", SwingConstants.CENTER);
        descriptionLabel.setForeground(Color.BLACK);
        panel.add(descriptionLabel);

        adminButton = new JButton("Admin");
        adminButton.addActionListener(this);
        adminButton.setBackground(Color.WHITE);
        adminButton.setForeground(Color.BLACK);

        studentButton = new JButton("Student");
        studentButton.addActionListener(this);
        studentButton.setBackground(Color.WHITE);
        studentButton.setForeground(Color.BLACK);

        panel.add(adminButton);
        panel.add(studentButton);

        add(panel);
        setVisible(true);

        students = new ArrayList<>();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminButton) {
            openPanel(UserType.ADMIN);
        } else if (e.getSource() == studentButton) {
            openPanel(UserType.STUDENT);
        } else if (e.getSource() == backButton) {
            openPanel(null);
        }
    }

    public void openPanel(UserType userType) {
        currentUserType = userType;
        panel.removeAll();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBackground(Color.WHITE);

        if (userType == UserType.ADMIN) {
            JLabel adminLabel = new JLabel("Welcome Admin!", SwingConstants.CENTER);
            adminLabel.setFont(new Font("Arial", Font.BOLD, 20));
            adminLabel.setForeground(Color.BLACK);
            panel.add(adminLabel);

            JButton createQuizButton = new JButton("Create Quiz");
            createQuizButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createQuiz();
                }
            });
            createQuizButton.setBackground(Color.WHITE);
            createQuizButton.setForeground(Color.BLACK);
            panel.add(createQuizButton);

            JButton viewResultsButton = new JButton("View Results");
            viewResultsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewResults();
                }
            });
            viewResultsButton.setBackground(Color.WHITE);
            viewResultsButton.setForeground(Color.BLACK);
            panel.add(viewResultsButton);
        } else if (userType == UserType.STUDENT) {
            JLabel enterNameLabel = new JLabel("Enter your name:", SwingConstants.CENTER);
            enterNameLabel.setForeground(Color.BLACK);
            panel.add(enterNameLabel);

            nameField = new JTextField(20);
            nameField.setMaximumSize(new Dimension(300, 30));
            nameField.setBackground(Color.WHITE);
            nameField.setForeground(Color.BLACK);
            nameField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    enterNameLabel.setText("");
                }

                @Override
                public void focusLost(FocusEvent e) {
                    // Do nothing
                }
            });
            panel.add(nameField);

            JButton takeQuizButton = new JButton("Take Quiz");
            takeQuizButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getText();
                    takeQuiz(name);
                }
            });
            takeQuizButton.setBackground(Color.WHITE);
            takeQuizButton.setForeground(Color.BLACK);
            panel.add(takeQuizButton);
        } else {
            panel.add(adminButton);
            panel.add(studentButton);
        }

        if (userType != null) {
            backButton = new JButton("Back");
            backButton.addActionListener(this);
            backButton.setBackground(Color.WHITE);
            backButton.setForeground(Color.BLACK);
            panel.add(backButton);
        }

        panel.revalidate();
        panel.repaint();
    }

    public void createQuiz() {
        quiz = new Quiz();

        // Input dialog for number of questions
        String numQuestionsStr = JOptionPane.showInputDialog(this, "Enter number of questions:");
        if (numQuestionsStr == null) // User canceled
            return;

        int numQuestions = Integer.parseInt(numQuestionsStr);

        for (int i = 0; i < numQuestions; i++) {
            String questionText = JOptionPane.showInputDialog(this, "Enter question " + (i + 1) + ":");
            if (questionText == null) // User canceled
                return;

            java.util.List<String> options = new ArrayList<>();

            // Ask for options in a loop
            StringBuilder optionMessage = new StringBuilder("Enter options for question " + (i + 1) + ":\n");
            for (int j = 0; j < 4; j++) {
                char optionChar = (char) ('A' + j);
                String optionText = JOptionPane.showInputDialog(this, "Enter option " + optionChar + ":");
                if (optionText == null) { // User canceled
                    return;
                }
                options.add(optionText);
                optionMessage.append(optionChar).append(": ").append(optionText).append("\n");
            }

            // Ask for correct answer
            String[] optionArray = options.toArray(new String[0]);
            String correctAnswer = (String) JOptionPane.showInputDialog(this,
                    "Select correct option for question " + (i + 1) + ":\n" + optionMessage, "Correct Answer",
                    JOptionPane.PLAIN_MESSAGE, null, optionArray, optionArray[0]);
            if (correctAnswer == null) { // User canceled
                return;
            }

            quiz.addQuestion(questionText, options, correctAnswer);
        }

        JOptionPane.showMessageDialog(this, "Quiz created successfully!");
    }

    public void viewResults() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students have taken the quiz yet.");
            return;
        }

        StringBuilder result = new StringBuilder("Results:\n");
        for (Student student : students) {
            result.append("Name: ").append(student.getName()).append(", Score: ").append(student.getScore()).append("\n");
        }

        JOptionPane.showMessageDialog(this, result.toString());
    }

    public void takeQuiz(String name) {
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quiz available. Admin hasn't created any quiz yet.");
            return;
        }

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
        StringBuilder feedback = new StringBuilder("Feedback:\n");

        for (Map.Entry<String, java.util.List<String>> entry : quiz.getQuestions().entrySet()) {
            String question = entry.getKey();
            java.util.List<String> options = entry.getValue();

            String userAnswer = (String) JOptionPane.showInputDialog(this, question, "Question",
                    JOptionPane.PLAIN_MESSAGE, null, options.toArray(), options.get(0));
            if (userAnswer == null) // User canceled
                return;

            String correctAnswer = quiz.getCorrectAnswers().get(question);

            if (userAnswer.equals(correctAnswer)) {
                score++;
                feedback.append("Question: ").append(question).append("\n");
                feedback.append("Your answer: ").append(userAnswer).append("\n");
                feedback.append("Correct!\n\n");
            } else {
                feedback.append("Question: ").append(question).append("\n");
                feedback.append("Your answer: ").append(userAnswer).append("\n");
                feedback.append("Correct answer: ").append(correctAnswer).append("\n\n");
            }
        }

        double percentage = (double) score / totalQuestions * 100;
        String resultMessage = "Quiz completed!\nYour score: " + score + "/" + totalQuestions + " (" + percentage + "%)\n\n";
        JOptionPane.showMessageDialog(this, resultMessage + feedback.toString());

        // Store student's result
        students.add(new Student(name, score));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizGeneratorGUI());
    }

    static class Student {
        private String name;
        private int score;

        public Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
