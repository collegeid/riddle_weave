import java.util.ArrayList;
import java.util.List;

public class ProvinceData {
    private String provinceName;
    private List<String> questions = new ArrayList<>();
    private List<String> correctAnswers = new ArrayList<>();
    private List<List<String>> options = new ArrayList<>();
    private int answeredCorrectly = 0;
    private boolean isCompleted = false; // Boolean untuk status provinsi

    public ProvinceData(String provinceName) {
        this.provinceName = provinceName;
    }

    public void addQuestion(String question, String correctAnswer, List<String> optionList) {
        questions.add(question);
        correctAnswers.add(correctAnswer);
        options.add(optionList);
    }

    public String getQuestion(int index) {
        return questions.get(index);
    }

    public String getCorrectAnswer(int index) {
        return correctAnswers.get(index);
    }

    public List<String> getOptions(int index) {
        return options.get(index);
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
            isCompleted = true; // Set status completed jika semua jawaban benar
        }
    }

    public boolean isCompleted() {
        return isCompleted; // Getter untuk status completed
    }
}