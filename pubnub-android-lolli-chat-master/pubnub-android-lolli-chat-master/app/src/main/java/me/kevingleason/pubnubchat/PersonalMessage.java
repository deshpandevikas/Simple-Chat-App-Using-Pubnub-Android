package me.kevingleason.pubnubchat;

import android.app.Activity;
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

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonalMessage extends Activity {

    ListView userslistview;
    ArrayList<User> listofusers = new ArrayList<User>();
    UsersAdapter usersAdapter;
    private Pubnub mPubNub;
    String loggedinuser="";
    int flag=0;
    String emailid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_message);
        userslistview = (ListView) findViewById(R.id.listview);


        if(getIntent().getExtras().getString("loggedinuser")!=null)
        {
            loggedinuser = getIntent().getExtras().getString("loggedinuser");
            emailid = getIntent().getExtras().getString("emailid");
        }
        if(getIntent().getExtras().getString("loggediemailid")!=null)
        {
            emailid = getIntent().getExtras().getString("loggediemailid");
        }
        if (this.mPubNub == null)
            initPubNub();

        history();
        usersAdapter = new UsersAdapter(this,R.layout.csutomlayout,listofusers);
        userslistview.setAdapter(usersAdapter);
        usersAdapter.setNotifyOnChange(true);

        userslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ii = new Intent(PersonalMessage.this,MyChat.class);
                ii.putExtra("chatwith",listofusers.get(position));
                ii.putExtra("loggedinuser",loggedinuser);
                finish();
                startActivity(ii);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuchangetype, menu);
        //this.mHereNow = menu.findItem(R.id.action_here_now);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){

            case R.id.action_change_type1:
                changechattype();
                return true;

            case R.id.action_sign_out:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut(){
        this.mPubNub.unsubscribeAll();
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }

    public void changechattype()
    {
        Intent iq = new Intent(PersonalMessage.this,MainActivity.class);
        finish();
        startActivity(iq);
    }

    public void history() {

        final int[] flag2 = {0};
        final List<User> allusers = new ArrayList<User>();
        Callback cb = new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                Log.d("demo", "Yes");
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    final List<User> chatMsgs = new ArrayList<User>();
                    flag2[0] = 0;
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String cemail = jsonMsg.getString("EMAIL");
                            String cpwd = jsonMsg.getString("PASSWORD");
                            User tempuser = new User(cemail, cpwd, jsonMsg.getString("LASTNAME"), jsonMsg.getString("FIRSTNAME"));
                            allusers.add(tempuser);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }
                    flag2[0] = 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        };

        mPubNub.history("USERSLIST", 100, false, cb);



        while(flag2[0]==0)
        {

        }

        flag = 0;
        for(int i=0;i<allusers.size();i++)
        {
            User temp = allusers.get(i);
            if(!temp.email.equalsIgnoreCase(emailid))
            {
                listofusers.add(temp);
            }
        }
        Log.d("araysize",listofusers.size()+"");
    }

    private void initPubNub(){
        this.mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        //this.mPubNub.setUUID(this.usernamemerged);
        //subscribeWithPresence();
        //history();
        //gcmRegister();
    }


}
