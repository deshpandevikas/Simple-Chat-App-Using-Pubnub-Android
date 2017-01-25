package me.kevingleason.pubnubchat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChatSelectActivity extends Activity {

    Button personalmsg, groupmsg;
    String loggedinuser="";
    String emailid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_select);

         personalmsg = (Button) findViewById(R.id.personal);
         groupmsg = (Button) findViewById(R.id.group);

        if(getIntent().getExtras()!=null)
        {
            loggedinuser = getIntent().getExtras().getString("loggedinuser");
            emailid = getIntent().getExtras().getString("emailid");
        }
        groupmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent grpmsgintent = new Intent(ChatSelectActivity.this, MainActivity.class);
                finish();
                startActivity(grpmsgintent);
            }
        });


        personalmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent personalmsgintent = new Intent(ChatSelectActivity.this, PersonalMessage.class);
                personalmsgintent.putExtra("loggedinuser",loggedinuser);
                personalmsgintent.putExtra("emailid",emailid);
                finish();
                startActivity(personalmsgintent);
            }
        });
    }
}
