package example.com.messageme;

import android.os.*;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nitin on 6/6/2017.
 */

public class JSONUtil {

    public static ArrayList<CustMessage> parseMessageJSON(String s){
        ArrayList<CustMessage> messageArrayList = new ArrayList<CustMessage>();
        //final String[] senderId = new String[1];
        try {
            //JSONObject root = new JSONObject(s);
            JSONArray jsonArray = new JSONArray(s);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject root = jsonArray.getJSONObject(i);
                CustMessage msg = new CustMessage();
                if(root.has("SenderId")){
                    //senderId[0] = root.getString("SenderId");
                    msg.setSender(root.getString("SenderId"));
                }
                if(root.has("ReceiverId")) {
                    msg.setReceiver(root.getString("ReceiverId"));
                }
                if(root.has("MessageBody")) {
                    msg.setTextMessage(root.getString("MessageBody"));
                }
                if(root.has("RegionId")) {
                    msg.setRegionName(root.getString("RegionId"));
                }
                if(root.has("IsRead")){
                    msg.setRead(root.getBoolean("IsRead"));
                    //Log.d("isRead",root.getBoolean("isRead") + "" );
                }
                if(root.has("MessageTime")){
                    msg.setDate(root.getString("MessageTime"));
                }
                if (root.has("Id")){
                    msg.setMsgId(root.getInt("Id"));
                }
                if(root.has("IsUnLocked")){
                    msg.setLocked(!root.getBoolean("IsUnLocked"));
                }
                messageArrayList.add(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageArrayList;
    }

    public static String parseTokenJSON(String s)  {
        JSONObject root;// = null;
        String token = "";
        try {
            root = new JSONObject(s);
            Log.d("demo",s);
            token = root.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return token;
    }

    public static User parseUserJSON(String s){
        User user = new User();
        try {
            //JSONObject root = new JSONObject(s);
            //JSONArray userInfo = new JSONArray(s);
            //for(int i = 0; i < userInfo.length(); i++) {
            JSONObject newJSONObject = new JSONObject(s);//userInfo.getJSONObject(i);

            if(newJSONObject.has("FirstName")){
                user.setFirstName(newJSONObject.getString("FirstName"));
            }
            if(newJSONObject.has("LastName")) {
                user.setLastName(newJSONObject.getString("LastName"));
            }
            if(newJSONObject.has("Id")) {
                user.setId(newJSONObject.getString("Id"));
            }
            if(newJSONObject.has("UserName")) {
                user.setUserName(newJSONObject.getString("UserName"));
            }


            //articleList.add(article);
            //}

        //}

    } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static String parseSenderName(String s, String id){
        String userName = "";
        try {
            //JSONObject root = new JSONObject(s);
            JSONArray userInfo = new JSONArray(s);

            for(int i = 0; i < userInfo.length(); i++) {
                JSONObject newJSONObject = userInfo.getJSONObject(i);

                    if (newJSONObject.getString("Id").equals(id)) {
                        if (newJSONObject.has("FirstName")) {
                            userName = newJSONObject.getString("FirstName");
                        }
                    }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userName;
    }

    public static ArrayList<String> parseUsersListJSON(String s){
        ArrayList<String> usersArrayList = new ArrayList<>();
        //final String[] senderId = new String[1];
        try {
            //JSONObject root = new JSONObject(s);
            JSONArray jsonArray = new JSONArray(s);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject root = jsonArray.getJSONObject(i);
                String username = new String();
                if(root.has("FirstName")){
                    //senderId[0] = root.getString("SenderId");
                    username = root.getString("FirstName");
                }

                Log.d("Inside",usersArrayList.toString());
                usersArrayList.add(username);
            }
            return usersArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Outside",usersArrayList.toString());
        return usersArrayList;
    }
}
