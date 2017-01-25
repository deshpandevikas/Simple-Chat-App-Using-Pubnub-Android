package me.kevingleason.pubnubchat;

import  android.app.Activity;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.kevingleason.pubnubchat.adt.ChatMessage;
import me.kevingleason.pubnubchat.callbacks.BasicCallback;

public class MyChat extends Activity implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    Button sendmessage, channelbar;
    ArrayList<PersonalChatMessage> chatmessages;
    EditText texttosend;
    private Pubnub mPubNub;
    User chatwith;
    String msg, luser="";
    private String channel="";
    String loggedinuser="", chatwithuser="";
    private SharedPreferences sharedPreferences;
    String chatwithuseremail="";
    PersonalMessageAdapter personalMessageAdapter;
    ListView lvmessages;
    String currentlyloggedinusersfirstname="";
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);

        // Start Google Client
        this.buildGoogleApiClient();
        mGoogleApiClient.connect();

        sendmessage = (Button) findViewById(R.id.btnsendmsg);
        texttosend = (EditText) findViewById(R.id.ettext);
        channelbar = (Button) findViewById(R.id.mychat_channel_bar);

        chatmessages = new ArrayList<PersonalChatMessage> ();
        chatwith = (User) getIntent().getExtras().getSerializable("chatwith");


        chatwithuser = chatwith.getFnmae()+" "+chatwith.getLname();

        chatwithuseremail = chatwith.getEmail();



        channelbar.setText("Your" +"chat with " + chatwithuser);


        lvmessages = (ListView) findViewById(R.id.lvmessages);
        personalMessageAdapter = new PersonalMessageAdapter(this,R.layout.chatmessagecustomlayout,chatmessages);
        lvmessages.setAdapter(personalMessageAdapter);
        setupAutoScroll();
        personalMessageAdapter.setNotifyOnChange(true);


        sharedPreferences = getSharedPreferences(Constants.CHAT_PREFS, MODE_PRIVATE);
        luser = sharedPreferences.getString("currentlyloggedinuser","");
        currentlyloggedinusersfirstname = sharedPreferences.getString("currentlyloggedinusersfirstname","");



        if(getIntent().getExtras()!=null)
        {
            loggedinuser = getIntent().getExtras().getString("loggedinuser");
        }

        if(luser.compareTo(chatwithuseremail)<=1)
        {
            channel = luser+" 's chat with "+chatwithuseremail;
            Log.d("channelis",channel);
        }
        else if(luser.compareTo(chatwithuseremail)>1)
        {
            channel = chatwithuseremail+" 's chat with "+luser;
            Log.d("channelis",channel);
        }

        //channel = luser+" 's chat with "+chatwithuseremail;
        if (this.mPubNub == null)
            initPubNub();

        history();

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(texttosend.getText().toString().trim()!=null)
                {
                    msg = texttosend.getText().toString().trim();

                    PersonalChatMessage text = new PersonalChatMessage(msg,luser, chatwithuseremail, System.currentTimeMillis());

                    try
                    {
                        JSONObject json = new JSONObject();
                        json.put("msgtext",text.getMessage());
                        json.put("sender",text.getSender());
                        json.put("receiver",text.getReciever());
                        json.put("timestamp",text.getTimeStamp());
                        publish("PersonalMessage",json);
                        texttosend.setText("");
                        personalMessageAdapter.setNotifyOnChange(true);

                        chatmessages.add(text);
                        personalMessageAdapter.notifyDataSetChanged();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        Log.d("eror",e.getMessage());
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter text",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void publish(String type, JSONObject data){
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        this.mPubNub.publish(this.channel, json, new BasicCallback());
    }

    public void history(){
        this.mPubNub.history(this.channel,1000,false,new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    //final List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>();
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String sender = jsonMsg.getString("sender");
                            String receiver = jsonMsg.getString("receiver");
                            String msgg = jsonMsg.getString("msgtext");
                            long time = jsonMsg.getLong("timestamp");
                            PersonalChatMessage chatMsg = new PersonalChatMessage(msgg, sender, receiver, time);
                            chatmessages.add(chatMsg);
                            personalMessageAdapter.setNotifyOnChange(true);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        });
    }

    private void setupAutoScroll(){
        this.personalMessageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                lvmessages.setSelection(personalMessageAdapter.getCount() - 1);
                // mListView.smoothScrollToPosition(mChatAdapter.getCount()-1);
            }
        });
    }

    private void initPubNub(){
        this.mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        this.mPubNub.setUUID(this.chatwith.getFnmae()+" "+chatwith.getLname());
        //subscribeWithPresence();
        //history();
        //gcmRegister();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this).addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationRequest mLocationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.d("suspended", "Connection to Google API suspended");

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location Update", "Latitude: " + location.getLatitude() +
                " Longitude: " + location.getLongitude());

        broadcastLocation(location);
    }

    private void broadcastLocation(Location location) {
        JSONObject message = new JSONObject();
        try {
            message.put("lat", location.getLatitude());
            message.put("lng", location.getLongitude());
            message.put("alt", location.getAltitude());
        } catch (JSONException e) {
            Log.e("location changed", e.toString());
        }
        mPubNub.publish("A Channel Name", message, publishCallback);
    }

    Callback publishCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object response) {
            Log.d("PUBNUB", response.toString());
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.e("PUBNUB", error.toString());
        }
    };
}
