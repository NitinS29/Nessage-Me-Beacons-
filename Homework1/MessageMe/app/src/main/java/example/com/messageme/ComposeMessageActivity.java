package example.com.messageme;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ComposeMessageActivity extends AppCompatActivity {

    AlertDialog contactsList;
    AlertDialog.Builder contactsListBuilder;
    AlertDialog regionsList;
    AlertDialog.Builder regionsListBuilder;
    ImageView imgPerson, imgRegion;
    TextView toName, region;
    private BeaconManager beaconManager;
    private BeaconRegion region1, region2, region3;
    ArrayList<String> regionArrayList;
    ArrayList<String> allUsersList;
    boolean isareply;
    String regionName;
    ProgressDialog progressDialog;
    Button btnSend;
    SharedPreferences mPrefs;
    String token;
    EditText messageBody;
    String sentFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        //menu.setIcon(R.drawable.ic_launcher);
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(true);
        menu.setTitle("Compose Message");
        setContentView(R.layout.activity_compose_message);

        toName = (TextView)findViewById(R.id.textViewToName);
        region = (TextView)findViewById(R.id.textViewRegionName);
        sentFrom = getIntent().getExtras().getString(InboxActivity.USER_KEY);

        mPrefs = getSharedPreferences("authToken",MODE_PRIVATE);
        token = mPrefs.getString("token","");
        messageBody = (EditText)findViewById(R.id.EditTextMessage) ;
        isareply = false;
        regionArrayList = new ArrayList<>();
        btnSend = (Button)findViewById(R.id.buttonSend);
        final RequestParams params = new RequestParams("POST","http://homework01.azurewebsites.net/api/Messages");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.addParams("Authorization","Bearer " + token);
                params.addParams("MessageBody", messageBody.getText().toString());
                params.addParams("RegionId", region.getText().toString());
                params.addParams("ReceiverId",toName.getText().toString());
                params.addParams("SenderId",sentFrom);
                params.addParams("MessageTime", (new Date()).toString());
                new SendMessageAsyncTask(new SendMessageAsyncTask.ISendMessage() {
                    @Override
                    public void getMessageStatus(String s) {
                        Toast.makeText(ComposeMessageActivity.this,s,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).execute(params);
            }
        });



        if(getIntent().getExtras().getBoolean(ReadActivity.REPLY_KEY) ){
            toName.setText("TO: " + getIntent().getExtras().getString(ReadActivity.SENDER_KEY));
            region.setText("REGION: " + getIntent().getExtras().getString(ReadActivity.REGION_KEY));
            isareply = getIntent().getExtras().getBoolean(ReadActivity.REPLY_KEY,false);
        }else{
            toName.setText("TO: " );
            region.setText("REGION: " );
            isareply = false;
        }

        contactsListBuilder = new AlertDialog.Builder(ComposeMessageActivity.this);

        if(!isareply) {
            imgPerson = (ImageView) findViewById(R.id.imageViewPerson);
            imgPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetUsersAsyncTask(new GetUsersAsyncTask.IGetProfile() {
                        @Override
                        public void getSenderName(ArrayList<String> names) {
                            allUsersList = names;
                            contactsListBuilder.setTitle("Contacts")
                                    .setCancelable(false)
                                    .setItems(allUsersList.toArray(new String[allUsersList.size()]), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            toName.setText("TO: " + allUsersList.get(i));
                                        }
                                    });
                            contactsList = contactsListBuilder.create();
                            contactsList.show();
                        }
                    }).execute("http://homework01.azurewebsites.net/api/Users");

                }
            });


            imgRegion = (ImageView) findViewById(R.id.imageViewRegion);
            imgRegion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SystemRequirementsChecker.checkWithDefaultDialogs(ComposeMessageActivity.this);


                    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                        @Override
                        public void onServiceReady() {
                            try {
                                beaconManager.startRanging(region1);
                                beaconManager.startRanging(region2);
                                beaconManager.startRanging(region3);

                            } catch (Exception ex) {
                                Log.d("Exception", "Cannot start ranging");
                            }

                        }


                    });

                    progressDialog = new ProgressDialog(ComposeMessageActivity.this);
                    progressDialog.setMessage("Getting Regions");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
        }

        region1 = new BeaconRegion("Region 1", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),1564,null);
        region2 = new BeaconRegion("Region 2", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),15212,null);
        region3 = new BeaconRegion("Region 3", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),26535,null);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setForegroundScanPeriod(2,8000);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {

                if(!beacons.isEmpty()){
                    for (Beacon b : beacons){
                        if(b.getMajor() == 1564){
                            regionName = region1.getIdentifier();
                        }else if(b.getMajor() == 15212){
                            regionName = region2.getIdentifier();
                        }else if(b.getMajor() == 26535){
                            regionName = region3.getIdentifier();
                        }
                        regionArrayList.add(regionName);
                    }
                }else{
                    Toast.makeText(ComposeMessageActivity.this,"No regions available",Toast.LENGTH_SHORT).show();
                }

            }
        });
        if(regionArrayList.size() > 0) {
            progressDialog.dismiss();
            beaconManager.stopRanging(region1);
            beaconManager.stopRanging(region2);
            beaconManager.stopRanging(region3);
            regionsListBuilder = new AlertDialog.Builder(ComposeMessageActivity.this);
            regionsListBuilder.setTitle("Regions")
                    .setCancelable(false)
                    .setItems(regionArrayList.toArray(new String[regionArrayList.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            region.setText("REGION: " + regionArrayList.get(i));
                        }
                    });
            regionsList = regionsListBuilder.create();
            regionsList.show();
        }else{
            Toast.makeText(ComposeMessageActivity.this,"No regions found",Toast.LENGTH_SHORT).show();
        }


    }


}
