package in.oormi.spiritualquotient;


import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Quiz {
    private static final String QTAG = "QLOG";
    public ArrayList<QuizSection> quizSections;
    public String userName;

    private float quizScore;
    private int sectionCount;
    public String quizAuthor;
    public String quizVersion;
    public String quizName;

    public Quiz(ArrayList<QuizSection> quizSections, String quizAuthor,
                String quizVersion, String quizName) {
        this.quizSections = quizSections;
        this.quizAuthor = quizAuthor;
        this.quizVersion = quizVersion;
        this.quizName = quizName;
        this.sectionCount = quizSections.size();
        this.quizScore = 0;
        this.userName = SetRandUserName();

    }

    private String SetRandUserName() {
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        int id1 = r.nextInt(9999);
        int id2 = r.nextInt(9999);
        String idstr = String.format("SQ%04d%04d", id1, id2);
        return idstr;
    }

    public float getQuizScore() {
        quizScore = 0.0f;
        for (int s = 0; s < getSectionCount(); s++) {
            quizScore = quizScore + getSectionScore(s);
        }
        quizScore = quizScore / getSectionCount();
        return quizScore;
    }

    public float getSectionScore(int section) {
        int secScore = 0;
        int maxScore = 0;
        boolean isTaken = true;

        for (int q = 0; q < quizSections.get(section).getQuizQuestions().size(); q++) {
            QuizQuestion qq = quizSections.get(section).getQuizQuestions().get(q);
            maxScore = maxScore + qq.getMaxScore();
            int userChoice = qq.getUserChoice();
            if (userChoice > 0) {
                int score = qq.getOptions().get(userChoice - 1).getScore();
                secScore = secScore + score;
            } else isTaken = false;
        }
        if (!isTaken) return -1.00f;
        return (float) secScore * 100.f / (float) maxScore;
    }

    public void addSection(QuizSection quizSection) {
        ArrayList<QuizQuestion> tQue = new ArrayList<>(quizSection.quizQuestions);
        QuizSection tSec = new QuizSection(tQue, quizSection.sectionName);
        quizSections.add(tSec);
        sectionCount = quizSections.size();
    }

    public void Dump() {
        Log.d(QTAG, quizName);
        Log.d(QTAG, quizAuthor);
        Log.d(QTAG, quizVersion);
        Log.d(QTAG, userName);
        Log.d(QTAG, String.valueOf(quizScore));

        for (int ns = 0; ns < quizSections.size(); ns++) {
            Log.d(QTAG, quizSections.get(ns).sectionName);
            for (int nq = 0; nq < quizSections.get(ns).quizQuestions.size(); nq++) {
                Log.d(QTAG, quizSections.get(ns).quizQuestions.get(nq).question + " | " +
                        String.valueOf(quizSections.get(ns).quizQuestions.get(nq).userChoice));
                for (int nopt = 0; nopt < quizSections.get(ns).quizQuestions.get(nq).options.size(); nopt++) {
                    Log.d(QTAG, quizSections.get(ns).quizQuestions.get(nq).options.get(nopt).option + " | " +
                            String.valueOf(quizSections.get(ns).quizQuestions.get(nq).options.get(nopt).score) + " | " +
                            quizSections.get(ns).quizQuestions.get(nq).options.get(nopt).explanation);
                }
            }
        }
    }

    public int getSectionCount() {
        sectionCount = quizSections.size();
        return sectionCount;
    }
}
