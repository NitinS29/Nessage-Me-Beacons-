package example.com.messageme;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadActivity extends AppCompatActivity {

    ActionBar actionBar;
    ImageButton deleteButton;
    ImageButton replyButton;
    ImageView imgPerson;
    TextView sender,messageBody,region;
    final static String SENDER_KEY = "SENDER";
    final static String REGION_KEY = "REGION";
    final static String REPLY_KEY = "REPLY";
    final static boolean ISAREPLY = true;
    CustMessage msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        //menu.setIcon(R.drawable.ic_launcher);
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(true);
        //menu.setTitle("Read Message");
        setContentView(R.layout.activity_read);

        sender = (TextView)findViewById(R.id.textViewToName);
        region = (TextView)findViewById(R.id.textViewRegionName);
        messageBody = (TextView)findViewById(R.id.EditTextMessage);

        if(getIntent().getExtras() != null){
            msg = (CustMessage) getIntent().getExtras().getSerializable(InboxActivity.MSG_KEY);
            sender.setText("FROM: " + msg.getSender());
            region.setText("REGION: " + getMessageRegionName(Integer.parseInt(msg.getRegionName())));
            messageBody.setText(msg.getTextMessage());
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_message_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.deleteButton)
        {
            new DeletemessagesAsyncTask().execute("http://homework01.azurewebsites.net/api/Messages/"+msg.getMsgId());
            finish();
        }else if(item.getItemId() == R.id.replyButton){
            Intent intent = new Intent(ReadActivity.this,ComposeMessageActivity.class);
            intent.putExtra(SENDER_KEY,msg.getSender());
            intent.putExtra(REGION_KEY,getMessageRegionName(Integer.parseInt(msg.getRegionName())));
            intent.putExtra(REPLY_KEY,ISAREPLY);
            Log.d("sender",sender.getText().toString());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public String getMessageRegionName(int regionId){
        return "Region " + regionId;
    }
}
