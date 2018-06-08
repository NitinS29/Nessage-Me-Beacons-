package example.com.messageme;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by Nitin on 8/30/2017.
 */

public class GetTokenAsyncTask extends AsyncTask<RequestParams, Void, String> {



    IGetTokenString iGetTokenString;

    public interface IGetTokenString{
        void getTokenForUser(String s);
    }

    public GetTokenAsyncTask(IGetTokenString tokenString){
        iGetTokenString = tokenString;
    }

    @Override
    protected String doInBackground(RequestParams... params) {
        StringBuilder sb = new StringBuilder();
        String result = "";
        try {
            HttpURLConnection con = params[0].createConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";

            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONUtil.parseTokenJSON(sb.toString());
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        iGetTokenString.getTokenForUser(s);
    }
}
