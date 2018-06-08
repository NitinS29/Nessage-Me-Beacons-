package example.com.messageme;

import android.os.AsyncTask;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by Nitin on 9/21/2017.
 */

public class SendMessageAsyncTask extends AsyncTask<RequestParams,Void,String> {

    ISendMessage iSendMessage;

    public SendMessageAsyncTask(ISendMessage iSendMessage1){
        this.iSendMessage = iSendMessage1;
    }
    public interface ISendMessage{
        void getMessageStatus(String s);
    }
    @Override
    protected String doInBackground(RequestParams... requestParamses) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpURLConnection con = requestParamses[0].createConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line = "";

            while ((line = reader.readLine()) != null){
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Message Sent Successfully";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        iSendMessage.getMessageStatus(s);

    }
}
