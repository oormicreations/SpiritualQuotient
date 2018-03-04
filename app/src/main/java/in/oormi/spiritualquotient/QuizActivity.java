package in.oormi.spiritualquotient;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QuizActivity extends AppCompatActivity {
    public int SectionNum;
    public int QueNum;
    Quiz quiz;
    QuizDBHandler qdb;
    private int firstId = 0;
    private boolean analysisMode;
    static TextView textViewAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_quiz);

        SQApp sqApp = (SQApp) this.getApplication();
        quiz = sqApp.getQuiz();
        qdb = sqApp.getQdb();

        analysisMode = this.getIntent().getIntExtra("Analyze", 0) == 1;
        SectionNum = this.getIntent().getIntExtra("Section", 0);
        QueNum = 0;

        SetAnswer();
        ShowQuestion();

        Button buttonHome = (Button) findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button buttonPrev = (Button) findViewById(R.id.buttonPrevious);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAnswer();
                QueNum--;
                ShowQuestion();
            }
        });

        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAnswer();
                QueNum++;
                ShowQuestion();
            }
        });

        textViewAnalysis = (TextView) findViewById(R.id.textViewAnalysis);
        textViewAnalysis.setMovementMethod(new ScrollingMovementMethod());
        if (analysisMode) textViewAnalysis.setVisibility(View.VISIBLE);

    }

    private void SetAnswer() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupOpt);
        firstId = radioGroup.getChildAt(0).getId();
        int checkedId = radioGroup.getCheckedRadioButtonId();
        int userChoice = 0;
        if (checkedId >= 0) {
            userChoice = checkedId - firstId + 1;
            QuizQuestion qq = quiz.quizSections.get(SectionNum).getQuizQuestions().get(QueNum);
            qq.setUserChoice(userChoice);
            int qid = qq.getQid();
            qdb.setUserChoice(qid, userChoice);
        }
        radioGroup.clearCheck();
    }

    @Override
    protected void onPause() {
        SetAnswer();
        super.onPause();
    }

    private void ShowQuestion() {

        boolean prev = false;

        if (QueNum < 0) {
            QueNum = 0;
            SectionNum--;
            prev = true;
        }
        if (SectionNum < 0) SectionNum = quiz.getSectionCount() - 1;

        QuizSection qs = quiz.quizSections.get(SectionNum);

        if (QueNum >= qs.getQueCount()) {
            QueNum = 0;
            SectionNum++;
        }
        if (SectionNum >= quiz.getSectionCount()) {
            SectionNum = 0;
        }
        qs = quiz.quizSections.get(SectionNum);

        if (prev) QueNum = qs.getQuizQuestions().size() - 1;
        QuizQuestion qq = qs.getQuizQuestions().get(QueNum);

        TextView textViewSection = (TextView) findViewById(R.id.textViewSection);
        textViewSection.setText(String.valueOf(SectionNum + 1) + " : " + qs.getSectionName());

        TextView textViewCount = (TextView) findViewById(R.id.textViewCount);
        textViewCount.setText(String.valueOf(QueNum + 1) + " of " + qs.getQueCount());

        TextView textViewQue = (TextView) findViewById(R.id.textViewQue);
        textViewQue.setText(qq.getQuestion());

        int[] radioButtons = {R.id.radioAns1, R.id.radioAns2, R.id.radioAns3,
                R.id.radioAns4, R.id.radioAns5, R.id.radioAns6};

        for (int rb = 0; rb < radioButtons.length; rb++) {
            RadioButton radioButton = (RadioButton) findViewById(radioButtons[rb]);
            radioButton.setVisibility(View.INVISIBLE);
            if (analysisMode) radioButton.setEnabled(false);

            if (rb < qq.getOptions().size()) {
                QuizOption qo = qq.getOptions().get(rb);
                radioButton.setVisibility(View.VISIBLE);
                radioButton.setText(qo.getOption());
            }
        }

        int userChoice = qq.getUserChoice();
        //Log.d("QTAG", "User choice: " + String.valueOf(userChoice));
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupOpt);
        radioGroup.clearCheck();
        if (userChoice > 0) radioGroup.check(firstId + userChoice - 1);

        ImageView imageView = (ImageView)findViewById(R.id.imageViewSq);
        imageView.setVisibility(View.VISIBLE);

        textViewAnalysis = (TextView) findViewById(R.id.textViewAnalysis);
        textViewAnalysis.scrollTo(0, 0);

        if ((analysisMode) && (userChoice > 0)) {
            imageView.setVisibility(View.INVISIBLE);
            String exp = qq.getOptions().get(userChoice - 1).getExplanation();
            exp = exp.replace("#", "\n");
            int uScore = qq.getOptions().get(userChoice - 1).getScore();
            String str = "Score: " + String.valueOf(uScore) + "\n\n" + exp;
            textViewAnalysis.setText(str);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Drawable grad = getDrawable(R.drawable.button_gradient);
                if (uScore == 3) grad = getDrawable(R.drawable.button_gradient_complete);
                if (uScore == 2) grad = getDrawable(R.drawable.button_gradient_incomplete);
                if (uScore < 2) grad = getDrawable(R.drawable.button_gradient);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textViewAnalysis.setBackground(grad);
                    }
                }
            }
        } else textViewAnalysis.setText("");

    }

}