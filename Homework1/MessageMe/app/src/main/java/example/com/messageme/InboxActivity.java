package example.com.messageme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class InboxActivity extends AppCompatActivity {

    ListView messagesListView;
    ArrayList<CustMessage> messagesList;
    MessagesArrayAdapter adapter;
    String receiverId;
    String token;
    final static String MSG_KEY = "MESSAGE";
    User user;
    final static String USER_KEY = "USER";
    private BeaconManager beaconManager;
    private BeaconRegion region1, region2, region3;
    long lastActiveTimeRegion1, lastActiveTimeRegion2, lastActiveTimeRegion3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        //menu.setIcon(R.drawable.ic_launcher);
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        menu.setDisplayShowTitleEnabled(true);
        menu.setTitle("Inbox");
        setContentView(R.layout.activity_inbox);


        messagesListView = (ListView)findViewById(R.id.listViewMessages);
        messagesList = new ArrayList<>();

        if(getIntent().getExtras() != null){
            token = getIntent().getExtras().getString(LoginActivity.TOKEN_KEY);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetUserinfoAsyncTask(new GetUserinfoAsyncTask.IGetProfile() {
            @Override
            public void getUserProfile(User userProfile) {
                user = userProfile;
                receiverId= user.getId();



                //receiverId = "b5a0df98-3b5e-460c-8bd5-bf9fd3ab5241";
                final RequestParams params = new RequestParams("GET", "http://homework01.azurewebsites.net/api/Messages");
                params.addParams("receiverId", receiverId);

                new GetMessagesAsyncTask(new GetMessagesAsyncTask.IAsyncPassMessages() {
                    @Override
                    public void getArrayList(ArrayList<CustMessage> messagesArrayList) {

                        messagesList = messagesArrayList;
                        sortList();
                        adapter = new MessagesArrayAdapter(InboxActivity.this, R.layout.listitem, messagesList);
                        messagesListView.setAdapter(adapter);
                        adapter.setNotifyOnChange(true);
                    }

                    @Override
                    public Context getContext() {
                        return InboxActivity.this;
                    }
                }).execute(params);

            }
        },token).execute("http://homework01.azurewebsites.net/api/Account/UserInfo");


        messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!messagesList.get(i).isLocked()) {
                    new MessagesReadMarkAsncTask().execute("http://homework01.azurewebsites.net/api/Messages/EditReadStatus?messageId=" + messagesList.get(i).getMsgId());
                    Intent intent = new Intent(InboxActivity.this, ReadActivity.class);
                    intent.putExtra(MSG_KEY, messagesList.get(i));
                    startActivity(intent);
                }else{
                    Toast.makeText(InboxActivity.this, "This message is Locked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        region1 = new BeaconRegion("Region 1", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),1564,null);
        region2 = new BeaconRegion("Region 2", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),15212,null);
        region3 = new BeaconRegion("Region 3", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),26535,null);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setForegroundScanPeriod(2,8000);
        SystemRequirementsChecker.checkWithDefaultDialogs(InboxActivity.this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region1);
                beaconManager.startRanging(region2);
                beaconManager.startRanging(region3);
            }
        });

        lastActiveTimeRegion1 = 0;lastActiveTimeRegion2 = 0;lastActiveTimeRegion3 = 0;

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {

                if(lastActiveTimeRegion1 == 0) {
                    lastActiveTimeRegion1 = (new Date()).getTime();

                }
                if(lastActiveTimeRegion2 == 0) {
                    lastActiveTimeRegion2= (new Date()).getTime();

                }
                if(lastActiveTimeRegion3 == 0) {
                    lastActiveTimeRegion3 = (new Date()).getTime();

                }
                if(messagesList.size()>0) {
                    for (CustMessage msg : messagesList) {
                    if (((new Date()).getTime() - lastActiveTimeRegion1) > 4000) {

                            Log.d("time1", ((new Date()).getTime() - lastActiveTimeRegion1) + "");

                            Log.d("Beaconregion", beaconRegion.getIdentifier() + msg.isLocked());
                            if (msg.isLocked() && ("Region " + msg.getRegionName()) == beaconRegion.getIdentifier()) {
                                //msg.setLocked(false);
                                new MarkUnlockedAsynctask().execute("http://homework01.azurewebsites.net/api/Messages/EditLockStatus?messageId=" + msg.getMsgId());
                                adapter.notifyDataSetChanged();
                                Log.d("Beacon", "1" + msg.isLocked() + " " + msg.getMsgId());
                            }


                            if (((new Date()).getTime() - lastActiveTimeRegion2) > 4000) {
                                Log.d("time2", ((new Date()).getTime() - lastActiveTimeRegion1) + "");

                                Log.d("Beacons", msg.getRegionName() + "  " + beaconRegion.getIdentifier());
                                if (msg.isLocked() && "Region " + msg.getRegionName() == beaconRegion.getIdentifier()) {
                                    new MarkUnlockedAsynctask().execute("http://homework01.azurewebsites.net/api/Messages/EditLockStatus?messageId=" + msg.getMsgId());
                                    adapter.notifyDataSetChanged();
                                    Log.d("Beacon", "2" + msg.isLocked() + " " + msg.getMsgId());
                                }


                            }
                            if (((new Date()).getTime() - lastActiveTimeRegion3) > 4000) {
                                Log.d("time3", ((new Date()).getTime() - lastActiveTimeRegion1) + "");

                                if (msg.isLocked() && "Region " + msg.getRegionName() == beaconRegion.getIdentifier()) {
                                    new MarkUnlockedAsynctask().execute("http://homework01.azurewebsites.net/api/Messages/EditLockStatus?messageId=" + msg.getMsgId());
                                    adapter.notifyDataSetChanged();
                                    Log.d("Beacon", "3" + msg.isLocked() + " " + msg.getMsgId());
                                }
                            }

                        }
                    }
                }else{
                    beaconManager.stopRanging(region1);beaconManager.stopRanging(region2);
                    beaconManager.stopRanging(region3);
                }
            }
        });

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inbox_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.composeButton){
            Intent intent = new Intent(InboxActivity.this,ComposeMessageActivity.class);
            intent.putExtra(USER_KEY,user.getId());
            startActivity(intent);
        }else if(item.getItemId() == R.id.refreshButton){
            SharedPreferences mPrefs;
            mPrefs = getSharedPreferences("authToken",MODE_PRIVATE);
            token = mPrefs.getString("token","");
            if(!token.isEmpty()){
                Intent intent = new Intent(InboxActivity.this, InboxActivity.class);
                intent.putExtra(LoginActivity.TOKEN_KEY,token);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void sortList(){

        Collections.sort(messagesList, new Comparator<CustMessage>() {
            @Override
            public int compare(CustMessage o1, CustMessage o2) {
                int returnValue = 0;
                if (o1.date != null && o2.date != null) {
                    try {
                        if (new SimpleDateFormat("MM-dd-yyyy").parse(o1.getDate()).before(new SimpleDateFormat("MM-dd-yyyy").parse(o2.getDate())))// > 0 ? 1 : 0;
                        {
                            returnValue = -1;
                        } else if (new SimpleDateFormat("MM-dd-yyyy").parse(o1.getDate()).after(new SimpleDateFormat("MM-dd-yyyy").parse(o2.getDate())))// > 0 ? 1 : 0;
                        {
                            returnValue = 1;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
                return returnValue;
            }
        });
    }
}
