package in.oormi.spiritualquotient;

import java.util.ArrayList;

public class QuizSection {
    ArrayList<QuizQuestion> quizQuestions;
    private int queCount;
    String sectionName;

    public QuizSection(ArrayList<QuizQuestion> quizQuestions, String sectionName) {
        this.quizQuestions = quizQuestions;
        this.sectionName = sectionName;
        queCount = quizQuestions.size();
    }

    public ArrayList<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }

    public void setQuizQuestions(ArrayList<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
        queCount = quizQuestions.size();
    }

    public int getQueCount() {
        return queCount;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void addQuestion(QuizQuestion quizQuestion) {
        ArrayList<QuizOption> tOptions = new ArrayList<>(quizQuestion.options);
        QuizQuestion tQuestion = new QuizQuestion(tOptions, quizQuestion.question, 0);
        tQuestion.setUserChoice(quizQuestion.getUserChoice());
        tQuestion.setqId(quizQuestion.qId);
        quizQuestions.add(tQuestion);
        queCount = quizQuestions.size();
    }
}
