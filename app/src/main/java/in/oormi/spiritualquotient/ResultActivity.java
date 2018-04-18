package in.oormi.spiritualquotient;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "SQRES";
    private static final int REQ_CODE_PERM_WES = 2;
    Quiz quiz;
    QuizDBHandler qdb;
    String achievement;
    String achRemark;
    String strScore;
    boolean isComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_result);

        SQApp sqApp = (SQApp) this.getApplication();
        quiz = sqApp.getQuiz();
        qdb = sqApp.getQdb();

        if (quiz == null) finish();
        if (qdb == null) finish();

        int pbCount = quiz.getSectionCount();

        int[] progressBarsIds = {R.id.progressBar1, R.id.progressBar2
                , R.id.progressBar3, R.id.progressBar4, R.id.progressBar5, R.id.progressBar6
                , R.id.progressBar7, R.id.progressBar8, R.id.progressBar9, R.id.progressBar10
                , R.id.progressBar0};

        int[] pbTextIds = {R.id.textViewPB1, R.id.textViewPB2
                , R.id.textViewPB3, R.id.textViewPB4, R.id.textViewPB5, R.id.textViewPB6
                , R.id.textViewPB7, R.id.textViewPB8, R.id.textViewPB9, R.id.textViewPB10
                , R.id.textViewPB0};

        for (int pb = 0; pb < progressBarsIds.length - 1; pb++) {
            ProgressBar progressBar = (ProgressBar) findViewById(progressBarsIds[pb]);
            TextView pbText = (TextView) findViewById(pbTextIds[pb]);
            if (pb < pbCount) {
                float secScore = quiz.getSectionScore(pb);
                if (secScore > 0) {
                    progressBar.setProgress((int) secScore);
                    progressBar.setAlpha(0.5f);
                    pbText.setText(quiz.quizSections.get(pb).getSectionName()
                            + " : " + String.format("%.2f", secScore));
                } else {
                    progressBar.setAlpha(0.1f);
                    progressBar.setProgress(0);
                    pbText.setText(quiz.quizSections.get(pb).getSectionName() + " : ?");
                    isComplete = false;
                }
                pbText.setPadding(20, 0, 0, 0);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                pbText.setVisibility(View.INVISIBLE);
            }
        }

        ProgressBar progressBar = (ProgressBar) findViewById(progressBarsIds[10]);
        TextView pbText = (TextView) findViewById(pbTextIds[10]);
        TextView tv2 = (TextView) findViewById(R.id.textViewSq2);
        TextView tv4 = (TextView) findViewById(R.id.textViewSq4);
        TextView tv5 = (TextView) findViewById(R.id.textViewSq5);

        float qScore = quiz.getQuizScore();
        Achievement(qScore);

        progressBar.setAlpha(0.5f);
        progressBar.setProgress((int) qScore);
        pbText.setPadding(20, 0, 0, 0);
        if (qScore > 0.0f)
            pbText.setText(String.format(getString(R.string.overall) + " : %.2f", qScore));

        tv2.setText(strScore);
        tv4.setText(achievement);
        tv5.setText(achRemark);

        //Buttons/////////////////////////////////////////////
        Button bRetake = (Button) findViewById(R.id.buttonStartOver);
        bRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetakeOption();
            }
        });

        Button bShare = (Button) findViewById(R.id.buttonShare);
        Button bRank = (Button) findViewById(R.id.buttonRank);
        Button bAnalyze = (Button) findViewById(R.id.buttonAnalyze);
        //bRank.setEnabled(true);

        bAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentQuiz = new Intent(ResultActivity.this,
                        QuizActivity.class);
                intentQuiz.putExtra("Analyze", 1);
                startActivity(intentQuiz);
                finish();
            }
        });

        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWriteStoragePermissionGranted()) ShareResults();
                //isReadStoragePermissionGranted();
            }
        });

        bRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckRank();
            }
        });

        if (isComplete) {
            bAnalyze.setEnabled(true);
            bRank.setEnabled(true);
            bShare.setEnabled(true);
            bRetake.setEnabled(true);
        }
    }

    private void CheckRank() {
        int iScore = (int) (quiz.getQuizScore() + 0.5f);
        //strScore = String.format("%d", iScore);

        Intent intentRank = new Intent(ResultActivity.this, RankActivity.class);
        intentRank.putExtra("Score", iScore);
        startActivity(intentRank);

    }

    private void RetakeOption() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        for (int qs = 0; qs < quiz.getSectionCount(); qs++) {
                            for (int q = 0; q < quiz.quizSections.get(qs).getQueCount(); q++) {
                                QuizQuestion qq = quiz.quizSections.get(qs).getQuizQuestions().get(q);
                                qq.setUserChoice(0);
                                int qid = qq.getQid();
                                qdb.setUserChoice(qid, 0);
                            }
                        }
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setTitle("Clear Options?");
        builder.setMessage("Tap Yes to clear all your previous answers.\n\n" +
                "Tap No to proceed and change previous answers")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void Achievement(float qScore) {
        if (isComplete) {
            int iScore = (int) (qScore + 0.5f);
            strScore = String.format("%d", iScore);
            if (iScore < 101) {
                achievement = "Guru";
                achRemark = "You are ready to teach. Spread your wisdom.";
            }
            if (iScore < 81) {
                achievement = "Yogi";
                achRemark = "You are doing great. Go deeper.";
            }
            if (iScore < 61) {
                achievement = "Seeker";
                achRemark = "You are on your path. Speed up.";
            }
            if (iScore < 41) {
                achievement = "Student";
                achRemark = "You have the potential. Pick a path.";
            }
            if (iScore < 21) {
                achievement = "Newbie";
                achRemark = "You need a good Guru. Wake up!";
            }

        } else {
            achievement = getString(R.string.unknown);
            achRemark = getString(R.string.incomplete);
            strScore = getString(R.string.nodata);
        }
    }

    public void ShareResults() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(),
                //getBitmapFromView(this.findViewById(android.R.id.content))));
                getBitmapFromView(getWindow().getDecorView().getRootView())));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.shareText));
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.knowSq));

        try {
            startActivity(Intent.createChooser(shareIntent, "Share your SQ"));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Spiritual Quotient", null);
        return Uri.parse(path);
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Read Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Read Permission is revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Read Permission is granted");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Write Permission ok");
                return true;
            } else {
                Log.v(TAG, "Write Permission revoked");

                showExplanation(getString(R.string.permReqTitle), getString(R.string.permRationale),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,REQ_CODE_PERM_WES);

                //ActivityCompat.requestPermissions(this,
                        //new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_PERM_WES);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Write Permission granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            switch (requestCode) {
                case 2:
                    Log.d(TAG, "Request results External storage");
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission: " + permissions[0] + " : " + grantResults[0]);
                        //resume tasks needing this permission
                        ShareResults();
                    } else {
                        Log.d(TAG, "Share failed.");
                    }
                    break;

                case 3:
                    Log.d(TAG, "Request results External storage");
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission: " + permissions[0] + " : " + grantResults[0]);
                        //resume tasks needing this permission
                    } else {
                        Log.d(TAG, "Share failed.");
                    }
                    break;
            }
        }
    }
}
