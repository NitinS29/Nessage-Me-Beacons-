package example.com.messageme;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nitin on 9/21/2017.
 */

public class MarkUnlockedAsynctask extends AsyncTask<String,Void,Void> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        //return JSONUtil.parseUserJSON(sb.toString());
        return null;
    }


}