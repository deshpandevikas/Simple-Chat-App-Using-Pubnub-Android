package me.kevingleason.pubnubchat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.kevingleason.pubnubchat.callbacks.BasicCallback;

public class SignUpActivity extends Activity {

    private Button signup, cancel;
    private EditText firstname, lastname, email, password, cpassword;
    int flag=0;
    private Pubnub pubnub;
    private String channel  = "USERSLIST";
    String fullname="";
    String checkemail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup = (Button) findViewById(R.id.button_signup);
        cancel = (Button) findViewById(R.id.button_cancel);

        firstname = (EditText) findViewById(R.id.editText_fname);
        lastname = (EditText) findViewById(R.id.editText_lname);
        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);
        cpassword = (EditText) findViewById(R.id.editText_password1);

        if (this.pubnub==null){
            initPubNub();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userfname = firstname.getText().toString().trim();
                final String userlname = lastname.getText().toString().trim();
                final String userEmail = email.getText().toString().trim();
                final String userPassword = password.getText().toString().trim();
                final String confirmPassword = password.getText().toString().trim();
                fullname = userfname+" "+userlname;

                if(userfname.length()==0 || userlname.length()==0 || userEmail.length()==0 || userPassword.length()==0 || confirmPassword.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please enter all details",Toast.LENGTH_LONG).show();
                }
                else {

                    if (checkidemailidexists(userEmail)) {
                        Toast.makeText(getApplicationContext(), "Sorry, that emailid is already taken! Please choose another emailid", Toast.LENGTH_LONG).show();
                        email.setText("");
                    } else {
                        if (!userPassword.equals(confirmPassword)) {
                            Toast.makeText(getApplicationContext(), "Passwords Do not match", Toast.LENGTH_LONG).show();
                            password.setText("");
                            cpassword.setText("");
                        } else {
                            User user = new User(userEmail, userPassword, userfname, userlname);
                            try {
                                JSONObject json = new JSONObject();
                                json.put("FIRSTNAME", user.getFnmae());
                                json.put("LASTNAME", user.getLname());
                                json.put("EMAIL", user.getEmail());
                                json.put("PASSWORD", user.getPassword());
                                publish("USERSLIST", json);
                                //history();
                                Toast.makeText(getApplicationContext(), "Account created Successfully", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                finish();
                                startActivity(i);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }

            }
        });


            }}

    public void publish(String type, JSONObject data){
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        this.pubnub.publish(this.channel, json, new BasicCallback());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.pubnub != null)
            this.pubnub.unsubscribeAll();
    }

    private boolean checkidemailidexists(String emaild)
    {
        checkemail=emaild;
        history();

        if(flag==0)
            return false;
        else
            return true;

    }

    public void history(){
        final int[] flag2 = {0};
        final List<String> emailids = new ArrayList<String>();
        Callback cb = new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                Log.d("demostring","Yes");
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    //final List<User> chatMsgs = new ArrayList<User>();
                    flag2[0] =0;
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String cemail = jsonMsg.getString("EMAIL");
                            //String cpwd = jsonMsg.getString("PASSWORD");
                            //User tempuser = new User(cemail,cpwd,jsonMsg.getString("LASTNAME"),jsonMsg.getString("FIRSTNAME"));
                            //allusers.add(tempuser);
                            emailids.add(cemail);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }
                    flag2[0] =1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        };

        pubnub.history("USERSLIST",100,false,cb);


        while(flag2[0]==0)
        {

        }
        flag = emailids.contains(checkemail)?1:0;

        return;

    }


    private void initPubNub(){
        this.pubnub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        this.pubnub.setUUID(this.fullname);
        //subscribeWithPresence();
        //history();
        //gcmRegister();
    }

    /**
     * I remove the PubNub object in onDestroy since turning the screen off triggers onStop and
     *   I wanted PubNub to receive messages while the screen is off.
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
