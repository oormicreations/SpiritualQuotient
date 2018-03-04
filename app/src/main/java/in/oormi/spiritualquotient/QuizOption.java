package in.oormi.spiritualquotient;

public class QuizOption {
    String option;
    int score;
    String explanation;

    public QuizOption(String option, int score, String explanation) {
        this.option = option;
        this.score = score;
        this.explanation = explanation;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
