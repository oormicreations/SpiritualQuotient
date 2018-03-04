package in.oormi.spiritualquotient;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class RankActivity extends AppCompatActivity {
    private static final String SQTAG = "SQTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        TextView textView1 = (TextView) findViewById(R.id.textViewGraph);
        TextView textView2 = (TextView) findViewById(R.id.textViewRank);
        if (!isNetworkAvailable()) {
            textView1.setText(R.string.noNet1);
            textView2.setText(R.string.connFail);
            return;
        }

        final int userScore = getIntent().getIntExtra("Score", 0);

        SqRankData conn = new SqRankData() {
            @Override
            public void onPostExecute(String result) {
                Log.d(SQTAG, result);
                ShowGraph(result, userScore);
            }
        };

        conn.execute("score=" + String.valueOf(userScore));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowGraph(String result, int userScore) {

        TextView textView1 = (TextView) findViewById(R.id.textViewGraph);
        TextView textView2 = (TextView) findViewById(R.id.textViewRank);
        if ((result.isEmpty()) || (result.contains("NoConnection") || (!isNetworkAvailable()))) {
            textView1.setText(R.string.noNet);
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        int totalUsers = 0;
        int belowUsers = 0;
        int aboveUsers = 0;
        int sameScoreUsers = 0;
        int totalSq = 0;

        StringTokenizer stringTokenizer = new StringTokenizer(result, "<br>");
        while (stringTokenizer.hasMoreTokens()) {
            String str = stringTokenizer.nextToken();
            StringTokenizer st = new StringTokenizer(str, ",");
            int score = Integer.parseInt(st.nextToken());
            float userCount = Float.parseFloat(st.nextToken());
            labels.add(String.valueOf(score));
            entries.add(new BarEntry(userCount, score));

            int uCount = (int) userCount;
            if (score == userScore) sameScoreUsers = uCount;
            totalUsers = totalUsers + uCount;
            if (score < userScore) belowUsers = belowUsers + uCount;
            if (score > userScore) aboveUsers = aboveUsers + uCount;
            totalSq = totalSq + uCount * score;
        }

        BarChart barChart = (BarChart) findViewById(R.id.barchart);
        BarDataSet bardataset = new BarDataSet(entries, getString(R.string.axisTitle));

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data);

        barChart.setDescription(getString(R.string.zoom));
        //bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        barChart.animateY(500);
        int[] colors = new int[101];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.rgb(120, 120, 130);
        }
        colors[userScore] = Color.rgb(200, 0, 0);
        bardataset.setColors(colors);

        float pSame = ((float) sameScoreUsers * 100.0f) / (float) totalUsers;
        float pBelow = ((float) belowUsers * 100.0f) / (float) totalUsers;
        float pAbove = ((float) aboveUsers * 100.0f) / (float) totalUsers;
        float meanSq = (float) totalSq / (float) totalUsers;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarRank);
        progressBar.setProgress((int) pBelow);

        textView1.setText(String.format(Locale.getDefault(),
                getString(R.string.format1), pSame, userScore, meanSq));
        textView2.setText(String.format(Locale.getDefault(),
                getString(R.string.format2), pBelow, pAbove));
    }
}











