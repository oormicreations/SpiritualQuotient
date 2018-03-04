package in.oormi.spiritualquotient;

import android.app.Application;

public class SQApp extends Application {
    public Quiz quiz;
    public QuizDBHandler qdb;

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public QuizDBHandler getQdb() {
        return qdb;
    }

    public void setQdb(QuizDBHandler qdb) {
        this.qdb = qdb;
    }
}
