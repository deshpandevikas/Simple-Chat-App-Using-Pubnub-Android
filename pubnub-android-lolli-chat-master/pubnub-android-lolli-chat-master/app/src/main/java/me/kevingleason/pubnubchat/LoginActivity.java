package me.kevingleason.pubnubchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import me.kevingleason.pubnubchat.adt.ChatMessage;

/**
 * Login Activity for the first time the app is opened, or when a user clicks the sign out button.
 * Saves the username in SharedPreferences.
 */
public class LoginActivity extends Activity {

    private EditText mUsername;
    private EditText mUserpassword;
    private Button signup;
    private Pubnub mPubNub;
    String enteredEmail, enteredPwd,usernamemerged;
    User loggedinuser=null;
    public static String loggedinuseremailid="";

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.login_username);
        mUserpassword = (EditText) findViewById(R.id.login_password);
        signup = (Button) findViewById(R.id.btnsignup);

        if (this.mPubNub == null)
            initPubNub();


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupact= new Intent(LoginActivity.this, SignUpActivity.class);
                finish();
                startActivity(signupact);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String lastUsername = extras.getString("oldUsername", "");
            mUsername.setText(lastUsername);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in


        return super.onOptionsItemSelected(item);
    }

    /**
     * Takes the username from the EditText, check its validity and saves it if valid.
     *   Then, redirects to the MainActivity.
     * @param view Button clicked to trigger call to joinChat
     */
    public void joinChat(View view){
        enteredEmail = mUsername.getText().toString();
        enteredPwd = mUserpassword.getText().toString();
        usernamemerged = enteredEmail;
        if (!validUsernameAndPassword(enteredEmail,enteredPwd)) {

            Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
            mUserpassword.setText("");
            return;
        }

        else {

            SharedPreferences sp = getSharedPreferences(Constants.CHAT_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(Constants.CHAT_USERNAME, usernamemerged);
            edit.putString("currentlyloggedinuser", loggedinuser.email);
            edit.putString("currentlyloggedinusersfirstname",loggedinuser.fname);
            edit.apply();

            Intent intent = new Intent(this, ChatSelectActivity.class);
            intent.putExtra("loggedinuser",usernamemerged);
            loggedinuseremailid=enteredEmail;
            intent.putExtra("emailid",enteredEmail);
            Toast.makeText(getApplicationContext(), "Login was successfull", Toast.LENGTH_LONG).show();
            finish();
            startActivity(intent);
        }
    }

    /**
     * Optional function to specify what a username in your chat app can look like.
     * @param username The name entered by a user.
     * @return
     */
    private boolean validUsernameAndPassword(String username, String password) {
        if (username.length() == 0) {
            mUsername.setError("Username cannot be empty.");
            return false;
        }

        history();

        if(flag==0)
            return false;
        else
            return true;

    }

    public void history(){
        final int[] flag2 = {0};
        final List<User> allusers = new ArrayList<User>();
        Callback cb = new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                Log.d("demo","Yes");
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    final List<User> chatMsgs = new ArrayList<User>();
                    flag2[0] =0;
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String cemail = jsonMsg.getString("EMAIL");
                            String cpwd = jsonMsg.getString("PASSWORD");
                           User tempuser = new User(cemail,cpwd,jsonMsg.getString("LASTNAME"),jsonMsg.getString("FIRSTNAME"));
                            allusers.add(tempuser);
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

        mPubNub.history("USERSLIST",100,false,cb);


        while(flag2[0]==0)
        {

        }

        flag = 0;
        for(int i=0;i<allusers.size();i++)
        {
            User temp = allusers.get(i);

            if(temp.email.equalsIgnoreCase(enteredEmail))
            {
                if(temp.getPassword().equals(enteredPwd))
                {
                    loggedinuser=temp;
                    flag=1;
                    usernamemerged = temp.getFnmae() + temp.getLname();
                    return;
                }else {
                    flag = 0;
                    return;
                }
            }
        }
    }
    private void initPubNub(){
        this.mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        this.mPubNub.setUUID(this.usernamemerged);
        //subscribeWithPresence();
        //history();
        //gcmRegister();
    }
}
