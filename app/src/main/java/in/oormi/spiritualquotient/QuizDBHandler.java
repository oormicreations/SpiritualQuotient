package in.oormi.spiritualquotient;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

public class QuizDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "quizManager";

    private static final String TABLE_QUIZ = "quiz";
    private static final String TABLE_SECTIONS = "sections";
    private static final String TABLE_QUE = "questions";
    private static final String TABLE_OPT = "options";

    private static final String KEY_ID = "id";
    private static final String KEY_QUIZDESC = "quizDesc";

    private static final String KEY_SECNAME = "sectionName";
    private static final String KEY_SID = "sectionId";

    private static final String KEY_QUE = "question";
    private static final String KEY_QID = "questionId";

    private static final String KEY_OPT = "option";
    private static final String KEY_OID = "optionId";
    private static final String KEY_SCORE = "score";
    private static final String KEY_EXP = "explanation";
    private static final String KEY_USROPT = "userOption";
    private static final String QDBTAG = "QDB";

    public QuizDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String str = "CREATE TABLE "
                + TABLE_QUIZ + "("
                + KEY_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + KEY_QUIZDESC + " TEXT" +")";
        db.execSQL(str);

        str = "CREATE TABLE "
                + TABLE_SECTIONS + "("
                + KEY_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + KEY_SECNAME + " TEXT,"
                + KEY_SID + " INTEGER" + ")";
        db.execSQL(str);

        str = "CREATE TABLE "
                + TABLE_QUE + "("
                + KEY_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + KEY_QUE + " TEXT,"
                + KEY_QID + " INTEGER,"
                + KEY_USROPT + " INTEGER" + ")";
        db.execSQL(str);

        str = "CREATE TABLE "
                + TABLE_OPT + "("
                + KEY_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + KEY_OPT + " TEXT,"
                + KEY_OID + " INTEGER,"
                + KEY_SCORE + " TEXT,"
                + KEY_EXP + " TEXT" + ")";
        db.execSQL(str);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPT);
        // Create tables again
        onCreate(db);
    }

    void insertData(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        String str = "INSERT INTO " + TABLE_QUIZ + " VALUES(NULL, \"" ;

        db.execSQL(str + quiz.quizName + "\")");
        db.execSQL(str + quiz.quizAuthor + "\")");
        db.execSQL(str + quiz.quizVersion + "\")");
        db.execSQL(str + String.valueOf(quiz.getSectionCount()) + "\")");
        db.execSQL(str + quiz.userName + "\")");
        db.execSQL(str + quiz.getQuizScore() + "\")");

        ContentValues values = new ContentValues();
        for (int ns=0; ns<quiz.quizSections.size(); ns++){
            values.put(KEY_SECNAME, quiz.quizSections.get(ns).getSectionName());
            values.put(KEY_SID, ns+1);
            db.insert(TABLE_SECTIONS, null, values);
            values.clear();

            ArrayList<QuizQuestion> quizQuestions = quiz.quizSections.get(ns).quizQuestions;
            for (int nq=0; nq<quizQuestions.size(); nq++){
                values.put(KEY_QUE, quizQuestions.get(nq).getQuestion());
                values.put(KEY_QID, (ns+1)*100 + nq+1);
                values.put(KEY_USROPT, quizQuestions.get(nq).getUserChoice());
                db.insert(TABLE_QUE, null, values);
                values.clear();

                ArrayList<QuizOption> options = quizQuestions.get(nq).options;
                for (int nopt=0; nopt<options.size(); nopt++) {
                    values.put(KEY_OPT, options.get(nopt).getOption());
                    values.put(KEY_OID, ((ns + 1) * 100 + nq + 1) * 100 + nopt + 1);
                    values.put(KEY_SCORE, options.get(nopt).getScore());
                    values.put(KEY_EXP, options.get(nopt).getExplanation());
                    db.insert(TABLE_OPT, null, values);
                    values.clear();
                }

            }
        }


        db.close();
    }

    void DumpData() {
        SQLiteDatabase db = this.getWritableDatabase();

        String str = "SELECT * FROM " + TABLE_QUIZ;
        Cursor cursor = db.rawQuery(str, null);

        if (cursor.moveToFirst()) {
            do {
                str = cursor.getString(1);
                Log.d(QDBTAG, str);
            } while (cursor.moveToNext());
        }

        str = "SELECT * FROM " + TABLE_SECTIONS;
        cursor = db.rawQuery(str, null);

        if (cursor.moveToFirst()) {
            do {
                str = cursor.getString(1) + "|" + cursor.getInt(2);
                Log.d(QDBTAG, str);
            } while (cursor.moveToNext());
        }

        str = "SELECT * FROM " + TABLE_QUE;
        cursor = db.rawQuery(str, null);

        if (cursor.moveToFirst()) {
            do {
                str = cursor.getString(1) + "|" + cursor.getInt(2) + "|" + cursor.getInt(3);
                Log.d(QDBTAG, str);
            } while (cursor.moveToNext());
        }

        str = "SELECT * FROM " + TABLE_OPT;
        cursor = db.rawQuery(str, null);

        if (cursor.moveToFirst()) {
            do {
                str = cursor.getString(1) + "|" + cursor.getInt(2)
                        + "|" + cursor.getInt(3) + "|" + cursor.getString(4);
                Log.d(QDBTAG, str);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    void addQue(String qid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QID, qid);

        long id = db.insert(TABLE_QUE, null, values);
        values.clear();

        db.close();
    }

    public String getQue(int qNum) {
        String selectQuery = "SELECT  * FROM " + TABLE_QUE + " LIMIT 1 OFFSET " + String.valueOf(qNum);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String queId = "Nothing to see.";
        int q = 0;
        if (cursor.moveToFirst()) {
            do {
                queId = cursor.getString(2);
                q++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return queId;
    }

    public String getSection(int secNum) {
        String selectQuery = "SELECT  * FROM " + TABLE_SECTIONS
                + " LIMIT 1 OFFSET " + String.valueOf(secNum);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String pageDesc = "Nothing to see.";
        if (cursor.moveToFirst()) {
            do {
                pageDesc = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return pageDesc;
    }

    public void deleteQue(String q) {
        String selectQuery = "DELETE FROM " + TABLE_QUE
                + " WHERE " + KEY_QID + " = \"" + q + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public void deleteAllQue() {
        String selectQuery = "DELETE FROM " + TABLE_QUE;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public void deleteAllSections() {
        String selectQuery = "DELETE FROM " + TABLE_SECTIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public void deleteSection(int secNum) {
        if (secNum<1) secNum=1;
        String selectQuery = "DELETE FROM " + TABLE_SECTIONS
                + " WHERE id in (SELECT id FROM " + TABLE_SECTIONS
                + " ORDER BY " + KEY_SECNAME + " ASC LIMIT 1 OFFSET " + String.valueOf(secNum-1) + ")";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public int getQueCount() {
        String countQuery = "SELECT  * FROM " + TABLE_QUE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int qcount = cursor.getCount();
        cursor.close();
        db.close();

        return qcount;
    }

    public int getSectionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SECTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int seccount = cursor.getCount();
        cursor.close();
        db.close();

        return seccount;
    }

    public void resetDB () throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase ();
        db.delete(TABLE_SECTIONS, null, null);
        db.delete(TABLE_QUE, null, null);
        db.close ();
    }

    public Quiz loadData() {
        QuizOption quizOption = new QuizOption("Unknown", 0, "Unknown");
        ArrayList<QuizOption> options = new ArrayList<>();

        QuizQuestion quizQuestion = new QuizQuestion(options, "Unknown", 0);
        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();

        QuizSection quizSection = new QuizSection(quizQuestions, "Unknown");
        ArrayList<QuizSection> quizSections = new ArrayList<>();

        Quiz quiz = new Quiz(quizSections,"Unknown","Unknown","Unknown");

        SQLiteDatabase db = this.getWritableDatabase();

        String str = "SELECT * FROM " + TABLE_QUIZ;
        Cursor cursor1 = db.rawQuery(str, null);
        ArrayList<String> stringArrayList = new ArrayList<>();

        if (cursor1.moveToFirst()) {
            do {
                str = cursor1.getString(1);
                stringArrayList.add(str);
            } while (cursor1.moveToNext());
            quiz.quizName = stringArrayList.get(0);
            quiz.quizAuthor = stringArrayList.get(1);
            quiz.quizVersion = stringArrayList.get(2);
            quiz.userName = stringArrayList.get(4);
        }

        str = "SELECT * FROM " + TABLE_SECTIONS;
        cursor1 = db.rawQuery(str, null);
        if (cursor1.moveToFirst()) {
            do {
                quizSection.sectionName = cursor1.getString(1);

                int qid1 = (1+quizSections.size()) * 100;
                int qid2 = (2+quizSections.size()) * 100;
                str = "SELECT * FROM " + TABLE_QUE + " WHERE "
                        + KEY_QID + ">" + qid1 + " AND " + KEY_QID + "<" + qid2;
                Cursor cursor2 = db.rawQuery(str, null);
                if (cursor2.moveToFirst()) {
                    do {
                        quizQuestion.question = cursor2.getString(1);
                        quizQuestion.qId = cursor2.getInt(2);
                        quizQuestion.userChoice = cursor2.getInt(3);
                        //(ns + 1) * 100 + nq + 1) * 100
                        int id1 = (((1+quizSections.size()) * 100) + quizSection.quizQuestions.size() + 1) * 100;
                        int id2 = (((1+quizSections.size()) * 100) + quizSection.quizQuestions.size() + 2) * 100;
                        str = "SELECT * FROM " + TABLE_OPT + " WHERE "
                                + KEY_OID + ">" + id1 + " AND " + KEY_OID + "<" + id2;
                        Cursor cursor3 = db.rawQuery(str, null);
                        if (cursor3.moveToFirst()) {
                            do {
                                quizOption.option = cursor3.getString(1);
                                quizOption.score = cursor3.getInt(3);
                                quizOption.explanation = cursor3.getString(4);
                                quizQuestion.addOption(quizOption);
                            } while (cursor3.moveToNext());
                        }
                        cursor3.close();
                        quizSection.addQuestion(quizQuestion);
                        quizQuestion.options.clear();

                    } while (cursor2.moveToNext());
                }
                cursor2.close();
                quiz.addSection(quizSection);
                quizSection.quizQuestions.clear();

            } while (cursor1.moveToNext());
        }


        str = "SELECT * FROM " + TABLE_OPT;
        cursor1 = db.rawQuery(str, null);
        if (cursor1.moveToFirst()) {
            do {
                str = cursor1.getString(1);
                options.add(new QuizOption(str, cursor1.getInt(3), cursor1.getString(4)));
            } while (cursor1.moveToNext());
        }

        cursor1.close();
        db.close();
        return quiz;
    }

    public void setUserChoice(int qid, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String str = "UPDATE " + TABLE_QUE + " SET " + KEY_USROPT + " = "
                + String.valueOf(id) + " WHERE " + KEY_QID + " = " + String.valueOf(qid);

        db.execSQL(str);
    }
}
