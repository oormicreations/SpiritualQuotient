package in.oormi.spiritualquotient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String SQ_XML_VERSION = "1.0";
    private static final int WRITE_REQUEST_CODE = 1011;
    private static final int READ_REQUEST_CODE = 1012;
    private QuizDBHandler qdb = new QuizDBHandler(this);
    SharedPreferences prefs = null;
    private ShareActionProvider mShareActionProvider;
    private static String SQTAG = "SQ";
    public Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        SQApp sqApp = (SQApp) this.getApplication();
        if (CheckQDB()) sqApp.setQuiz(quiz);
        sqApp.setQdb(qdb);

        int[] sectionButtons = {R.id.buttonS1, R.id.buttonS2,
                R.id.buttonS3, R.id.buttonS4, R.id.buttonS5, R.id.buttonS6,
                R.id.buttonS7, R.id.buttonS8, R.id.buttonS9, R.id.buttonS10};
        int nButtons = quiz.getSectionCount();
        if (nButtons > sectionButtons.length) nButtons = sectionButtons.length;

        for (int b = 0; b < nButtons; b++) {
            final int bCount = b;
            Button buttonSec = (Button) findViewById(sectionButtons[b]);
            buttonSec.setText(quiz.quizSections.get(b).getSectionName());
            buttonSec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentQuiz = new Intent(MainActivity.this,
                            QuizActivity.class);
                    intentQuiz.putExtra("Section", bCount);
                    startActivity(intentQuiz);
                }
            });
        }

        Button buttonRes = (Button) findViewById(R.id.buttonResults);
        buttonRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRes = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intentRes);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        int[] sectionButtons = {R.id.buttonS1, R.id.buttonS2,
                R.id.buttonS3, R.id.buttonS4, R.id.buttonS5, R.id.buttonS6,
                R.id.buttonS7, R.id.buttonS8, R.id.buttonS9, R.id.buttonS10};
        int nButtons = quiz.getSectionCount();
        if (nButtons > sectionButtons.length) nButtons = sectionButtons.length;
        for (int b = 0; b < nButtons; b++) {
            int totalQue = quiz.quizSections.get(b).getQueCount();
            int comQue = 0;
            for (int i = 0; i < totalQue; i++) {
                if (quiz.quizSections.get(b).getQuizQuestions().get(i).getUserChoice() > 0) {
                    comQue++;
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Drawable grad = getDrawable(R.drawable.button_gradient);
                if (comQue == totalQue) grad = getDrawable(R.drawable.button_gradient_complete);
                if (comQue < totalQue) grad = getDrawable(R.drawable.button_gradient_incomplete);
                if (comQue < 1) grad = getDrawable(R.drawable.button_gradient);

                Button buttonSec = (Button) findViewById(sectionButtons[b]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        buttonSec.setBackground(grad);
                    }
                }
            }
        }
    }

    private boolean CheckQDB() {
        int ns = qdb.getSectionCount();
        int nq = qdb.getQueCount();
        if ((ns < 1) || (nq < 1)) {/*its a hack TODO: add versioning*/
            readXml(null, true);
            qdb.onUpgrade(qdb.getWritableDatabase(), 1, 1);
            qdb.insertData(quiz);
            //qdb.DumpData();
            Log.d(SQTAG, "DB Init");
        } else {
            if (quiz == null) {
                quiz = qdb.loadData();
                Log.d(SQTAG, "DB found. Sec:" + String.valueOf(ns) + " Que:" + String.valueOf(nq));
                //quiz.Dump();
            } else {
                Log.d(SQTAG, "Quiz Ok. Sec:" + String.valueOf(ns) + " Que:" + String.valueOf(nq));
                return false;
            }
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.infomenu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menuShare);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=in.oormi.spiritualquotient");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getString(R.string.menuShareSubject));
        setShareIntent(shareIntent);
        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuInfo:
                Intent intentInfo = new Intent(this, ResourceShow.class);
                startActivity(intentInfo);
                break;

            case R.id.menuMore:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Oormi+Creations"));
                startActivity(i);
                break;

            case R.id.menuAbout:
                String str = "SQ Quiz Info:\n\nName: " + quiz.quizName + "\nAuthor: "
                        + quiz.quizAuthor + "\nVersion: " + quiz.quizVersion;
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                break;

            case R.id.menuShare:
                break;

            case R.id.menuLoad:
                getQuizFile();
                break;

//            case R.id.menuEdit:
//                Intent intentEdit = new Intent(MainActivity.this, EditQuizActivity.class);
//                //intentEdit.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Oormi+Creations"));
//                startActivity(intentEdit);
//                break;

            case R.id.menuDel:
                try {
                    qdb.resetDB();
                    Log.d(SQTAG, "DB deleted");
                    SQApp sqApp = (SQApp) this.getApplication();
                    if (CheckQDB()) sqApp.setQuiz(quiz);
                    sqApp.setQdb(qdb);
                    onResume();
                    Toast.makeText(this, R.string.dataCleared, Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

//            case R.id.menuDumpDB:
//                qdb.DumpData();
//                break;
//
//            case R.id.menuDumpQuiz:
//                quiz.Dump();
//                break;

        }
        return true;
    }

    private void createQuizFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    private void getQuizFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xml");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("xmlHelperTAG", "Uri Read: " + uri.toString());
                readXml(uri, false);
            }
        }

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("xmlHelperTAG", "Uri Write: " + uri.toString());
                writeXml(uri);
            }
        }
    }

    public void writeXml(Uri uri) {
        OutputStream outs = null;
        try {
            outs = getContentResolver().openOutputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        XmlSerializer serializer = Xml.newSerializer();

        try {
            serializer.setOutput(outs, "UTF-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "tasklist");
            serializer.attribute(null, "ver", SQ_XML_VERSION);
/*
            for (int n=0;n<allTaskList.size();n++) {
                serializer.startTag(null, "task");
                GroupInfo g = allTaskList.get(n);
                serializer.attribute(null, "task", g.getTask());
                serializer.attribute(null, "taskid", String.valueOf(g.getId()));

                for (int m=0;m<g.getDetailsList().size();m++) {
                    serializer.startTag(null, "step");
                    ChildInfo c = g.getDetailsList().get(m);
                    serializer.attribute(null, "seq", String.valueOf(c.getSequence()));
                    serializer.attribute(null, "enabled", String.valueOf(c.getEnabled()));
                    serializer.attribute(null, "delay", c.getDelay());
                    serializer.attribute(null, "desc", c.getDescription());
                    serializer.endTag(null, "step");
                }

                serializer.endTag(null, "task");
            }
*/
            serializer.endTag(null, "tasklist");
            serializer.endDocument();
            serializer.flush();
            if (outs != null) {
                outs.close();
            }

        } catch (Exception e) {
            Log.e("Exception", "Exception occurred in writing");
        }
    }


    public void readXml(Uri uri, boolean builtIn) {
        InputStream inputStream = null;
        String str = "Unknown";
        String option = "Unknown";
        String score = "Unknown";
        String exp = "Unknown";
        String section = "Unknown";

        if (quiz != null) {
            quiz = null;
        }

        QuizOption quizOption = new QuizOption("Unknown", 0, "Unknown");
        ArrayList<QuizOption> options = new ArrayList<>();

        QuizQuestion quizQuestion = new QuizQuestion(options, "Unknown", 0);
        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();

        QuizSection quizSection = new QuizSection(quizQuestions, "Unknown");
        ArrayList<QuizSection> quizSections = new ArrayList<>();

        quiz = new Quiz(quizSections, "Unknown", "Unknown", "Unknown");


        if (builtIn) {
            try {
                inputStream = getResources().getAssets().open("quiz_sample.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                inputStream = getContentResolver().openInputStream(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("SqQuiz")) {
                            if (parser.getAttributeCount() > 0) {
                                str = parser.getAttributeValue(null, "sqversion");
                                if (str.equals("1.0")) {
                                    str = parser.getAttributeValue(null, "quizversion");
                                    if (str != null) quiz.quizVersion = str;
                                    str = parser.getAttributeValue(null, "author");
                                    if (str != null) quiz.quizAuthor = str;
                                    str = parser.getAttributeValue(null, "quizname");
                                    if (str != null) quiz.quizName = str;

                                } else {
                                    Toast.makeText(this, "SQ version mismatch",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                            }
                        }

                        if (tagname.equalsIgnoreCase("section")) {
                            if (parser.getAttributeCount() > 1) {
                                str = parser.getAttributeValue(null, "id");
                                if (str != null) {
                                    str = parser.getAttributeValue(null, "name");
                                    if (str != null) section = str;
                                }
                            }
                        }
                        if (tagname.equalsIgnoreCase("question")) {
                            if (parser.getAttributeCount() > 0) {
                                str = parser.getAttributeValue(null, "q");
                                if (str != null) quizQuestion.question = str;
                            }
                        }
                        if (tagname.equalsIgnoreCase("option")) {
                            if (parser.getAttributeCount() > 0) {
                                str = parser.getAttributeValue(null, "op");
                                if (str != null) option = str;
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        str = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("score")) {
                            if (str != null) score = str;
                        }
                        if (tagname.equalsIgnoreCase("explanation")) {
                            if (str != null) exp = str;
                        }
                        if (tagname.equalsIgnoreCase("option")) {
                            quizOption.option = option;
                            quizOption.score = Integer.decode(score);
                            quizOption.explanation = exp;
                            quizQuestion.addOption(quizOption);
                        }
                        if (tagname.equalsIgnoreCase("question")) {
                            quizSection.addQuestion(quizQuestion);
                            quizQuestion.options.clear();
                        }
                        if (tagname.equalsIgnoreCase("section")) {
                            quizSection.sectionName = section;
                            quiz.addSection(quizSection);
                            quizSection.quizQuestions.clear();
                        }
                        break;

                    default:
                        break;
                }
                try {
                    eventType = parser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
