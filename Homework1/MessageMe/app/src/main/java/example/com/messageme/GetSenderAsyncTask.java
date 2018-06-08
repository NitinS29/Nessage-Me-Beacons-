package example.com.messageme;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nitin on 8/27/2017.
 */

public class GetSenderAsyncTask extends AsyncTask<String,Void,String> {

    IGetProfile iGetProfile;
    String id;

    public GetSenderAsyncTask(IGetProfile iProfile, String s) {
        iGetProfile = iProfile;
        id = s;
    }

    public interface IGetProfile{
        void getSenderName(String name);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder sb = new StringBuilder();
        try {

            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            //con.setRequestProperty("Authorization","Bearer " + id);
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line = "";

            while ((line = reader.readLine()) != null){
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("name", sb.toString());
        return JSONUtil.parseSenderName(sb.toString(),id);
    }

    @Override
    protected void onPostExecute(String userProfile) {
        super.onPostExecute(userProfile);
        iGetProfile.getSenderName(userProfile);
    }
}
