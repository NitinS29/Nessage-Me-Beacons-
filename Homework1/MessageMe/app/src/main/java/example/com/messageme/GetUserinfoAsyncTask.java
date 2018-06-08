package example.com.messageme;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Nitin on 8/27/2017.
 */

public class GetUserinfoAsyncTask extends AsyncTask<String,Void,User> {

    IGetProfile iGetProfile;
    String token;

    public GetUserinfoAsyncTask(IGetProfile iProfile, String s) {
        iGetProfile = iProfile;
        token = s;
    }

    public interface IGetProfile{
        void getUserProfile(User userProfile);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected User doInBackground(String... params) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url =  new URL(params[0] + "?" + "token" + "=" +token);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
           return JSONUtil.parseUserJSON(sb.toString());
    }

    @Override
    protected void onPostExecute(User userProfile) {
        super.onPostExecute(userProfile);
        iGetProfile.getUserProfile(userProfile);
    }
}
