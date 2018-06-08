package example.com.messageme;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Nitin on 8/27/2017.
 */

public class GetUsersAsyncTask extends AsyncTask<String,Void,ArrayList<String>> {

    IGetProfile iGetProfile;
    String id;

    public GetUsersAsyncTask(IGetProfile iProfile) {
        iGetProfile = iProfile;
        //id = s;
    }

    public interface IGetProfile{
        void getSenderName(ArrayList<String> names);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
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
        return JSONUtil.parseUsersListJSON(sb.toString());
    }

    @Override
    protected void onPostExecute(ArrayList<String> userProfile) {
        super.onPostExecute(userProfile);
        iGetProfile.getSenderName(userProfile);
    }
}
