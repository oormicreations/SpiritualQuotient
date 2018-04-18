package in.oormi.spiritualquotient;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class SqRankData extends AsyncTask<String, Void, String> {
    private static final String SQTAG = "SqData";
    private int reconnectCount = 1;
    private String verStr = "1.0.2";

    @Override
    protected String doInBackground(String... msgList) {
        if (msgList[0].isEmpty()) {
            return "Empty Message";
        } else {
            String res2 = "Error";
            try {
                while (reconnectCount<5) {
                    res2 = HttpPostMsg(msgList[0]);
                    if (res2.length()>0){
                        //res = "Your question has been sent!";
                        break;
                    } else
                    if (res2.contains("Error")) {
                        //res = "> " + res + " <";
                        break;
                    }else {
                        reconnectCount++;
                        Log.i(SQTAG, "Reconnect try: " + String.valueOf(reconnectCount));
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return res2;
        }
    }

    private String HttpPostMsg(String data) throws UnsupportedEncodingException {
        String postResult = "HttpPostMsg Error. No Internet.";
        BufferedReader reader = null;

        try
        {
            URL url = new URL("https://oormi.in/software/sq/sq01.php");
            data = data + "&ver=" + verStr;
            Log.i(SQTAG, data);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                sb.append(line);
            }

            postResult = sb.toString();
         }
        catch(Exception ex) {
            Log.e(SQTAG, "Error in HttpPostMsg.");
            postResult = "HttpPostMsg: NoConnection : Could not connect to the Internet.";
        }
        finally {
            try {
                reader.close();
            }
            catch(Exception ex) {
                Log.e(SQTAG, "HttpPostMsg: Error in reader close.");
            }
        }

        Log.i(SQTAG, postResult);
        return postResult;
    }

}