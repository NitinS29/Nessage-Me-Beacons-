package example.com.messageme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    Button btnSignUp;
    HashMap<String,String> userParams;
    EditText firstName, lastName, userName, pwd, cPwd;
    String token;
    final static String TOKEN_KEY = "TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        //menu.setIcon(R.drawable.ic_launcher);
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(true);
        menu.setTitle("Message Me!(Sign Up)");
        setContentView(R.layout.activity_sign_up);

        userParams = new HashMap<>();
        btnSignUp = (Button)findViewById(R.id.buttonCreateAcc);
        firstName = (EditText)findViewById(R.id.editTextFirstName);
        lastName = (EditText)findViewById(R.id.editTextlastName);
        userName  = (EditText)findViewById(R.id.editTextFirstName);
        pwd = (EditText)findViewById(R.id.editTextPwd);
        cPwd = (EditText)findViewById(R.id.editTextConfirmPwd);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userParams.put("FirstName",firstName.getText().toString());
                userParams.put("LastName",lastName.getText().toString());
                userParams.put("UserName",userName.getText().toString());
                userParams.put("Password",pwd.getText().toString());
                userParams.put("ConfirmPassword",cPwd.getText().toString());

                new SignupAsyncTask(new SignupAsyncTask.IGetMessage() {
                    @Override
                    public void getSignUpMessage(String message) {
                        Toast.makeText(SignUpActivity.this,message,Toast.LENGTH_SHORT).show();
                        if(message.equals("Account created succesfully")) {
                            final RequestParams params = new RequestParams("POST", "http://homework01.azurewebsites.net/oauth2/token");
                            params.addParams("username", userName.getText().toString());
                            params.addParams("password", pwd.getText().toString());
                            params.addParams("grant_type", "password");

                            new GetTokenAsyncTask(new GetTokenAsyncTask.IGetTokenString() {
                                @Override
                                public void getTokenForUser(String s) {
                                    token = s;

                                    if (token.length() != 0 && token != null) {
                                        Intent intent = new Intent(SignUpActivity.this, InboxActivity.class);
                                        intent.putExtra(TOKEN_KEY,token);
                                        startActivity(intent);
                                    }
                                }
                            }).execute(params);
                        }
                    }
                },userParams).execute("http://homework01.azurewebsites.net/api/Account/Register");
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SignUpActivity.this,"Sign Up aborted",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
