package example.com.messageme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    Button login;
    Button newUser;

    final static String TOKEN_KEY = "TOKEN";
    String token;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        //menu.setIcon(R.drawable.ic_launcher);
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(true);
        menu.setTitle("MessageMe!");
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.buttonLogin);
        newUser = (Button) findViewById(R.id.buttonNewUser);
        mPrefs = getSharedPreferences("authToken",MODE_PRIVATE);
        token = mPrefs.getString("token","");
        if(!token.isEmpty()){
            Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
            intent.putExtra(TOKEN_KEY,token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = ((EditText) findViewById(R.id.editTextUserName)).getText().toString();
                String pwd = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                final RequestParams params = new RequestParams("POST", "http://homework01.azurewebsites.net/oauth2/token");
                params.addParams("UserName", username);
                params.addParams("Password", pwd);
                params.addParams("grant_type", "password");

                new GetTokenAsyncTask(new GetTokenAsyncTask.IGetTokenString() {
                    @Override
                    public void getTokenForUser(String s) {
                        token = s;
                        mPrefs.edit().putString("token",token).apply();
                        if (token.length() != 0 && token != null) {


                                    //user = userProfile;
                                    Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
                                    intent.putExtra(TOKEN_KEY,token);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                        }else {
                            Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute(params);
            }
        });




        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}


