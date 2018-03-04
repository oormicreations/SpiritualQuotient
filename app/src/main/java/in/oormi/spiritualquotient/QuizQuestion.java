package in.oormi.spiritualquotient;

import java.util.ArrayList;

public class QuizQuestion {
    ArrayList<QuizOption> options;
    int optionsCount;
    String question;
    int userChoice; //0=no choice, else 1,2,3,4
    int qId;
    int maxScore;

    public QuizQuestion(ArrayList<QuizOption> options, String question, int userChoice) {
        this.options = options;
        this.question = question;
        this.userChoice = userChoice;
        qId = 0;
        maxScore = 0;
    }

    public ArrayList<QuizOption> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<QuizOption> options) {
        this.options = options;
        optionsCount = options.size();
    }

    public int getOptionsCount() {
        return options.size();
    }

//    public void setOptionsCount(int optionsCount) {
//        this.optionsCount = optionsCount;
//    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getUserChoice() {
        return userChoice;
    }


    public void setUserChoice(int userChoice) {
        this.userChoice = userChoice;
    }

    public int getMaxScore() {
        maxScore = 0;
        for (int op = 0; op < getOptionsCount(); op++) {
            int score = getOptions().get(op).getScore();
            if (score > maxScore) maxScore = score;
        }
        return maxScore;
    }

    public void addOption(QuizOption quizOption) {
        QuizOption tOption = new QuizOption(quizOption.option, quizOption.score,
                quizOption.explanation);
        options.add(tOption);
        optionsCount = options.size();

    }

    public void setqId(int qId) {
        this.qId = qId;
    }

    public int getQid() {
        return qId;
    }
}
