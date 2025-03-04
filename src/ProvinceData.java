import java.util.ArrayList;
import java.util.List;

public class ProvinceData {
    private String provinceName;
    private List<String> questions = new ArrayList<>();
    private List<String> correctAnswers = new ArrayList<>();
    private int answeredCorrectly = 0;
    private boolean isCompleted = false;

    public ProvinceData(String provinceName) {
        this.provinceName = provinceName;
    }

    public void addQuestion(String question, String correctAnswer) {
        questions.add(question);
        correctAnswers.add(correctAnswer);
    }

    public String getQuestion(int index) {
        return questions.get(index);
    }

    public String getCorrectAnswer(int index) {
        return correctAnswers.get(index);
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public void incrementCorrectAnswers() {
        answeredCorrectly++;
        if (answeredCorrectly == questions.size()) {
            isCompleted = true;
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
