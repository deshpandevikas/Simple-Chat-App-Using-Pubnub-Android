package me.kevingleason.pubnubchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Vikas Deshpande on 1/22/2017.
 */

public class PersonalMessageAdapter extends ArrayAdapter<PersonalChatMessage>
{
    List<PersonalChatMessage> mData;
    Context mContext;
    int mResource;
    String emailid="";




    public PersonalMessageAdapter(Context context, int resource, List<PersonalChatMessage> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData = objects;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);

        PersonalChatMessage which = mData.get(position);

        TextView message = (TextView) convertView.findViewById(R.id.tvchattuser1);

        TextView message1 = (TextView) convertView.findViewById(R.id.tvchattsender);

        TextView tvpersonalchattimestamp = (TextView) convertView.findViewById(R.id.tvpersonalchattimestamp);
        //TextView tvpersonalchattimestamp1 = (TextView) convertView.findViewById(R.id.tvpersonalchattimestamp1);

        // Get Relative Layouts for user;
        //RelativeLayout receiverLayout = (RelativeLayout) convertView.findViewById(R.id.receiverLayout);
        //RelativeLayout senderRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.senderLayout);

        if(LoginActivity.loggedinuseremailid.equals(which.getSender()))
        {
            message1.setText(which.getMessage());
           tvpersonalchattimestamp.setText(formatTimeStamp(which.getTimeStamp()));

            //receiverLayout.setVisibility(View.GONE);
            //message.setVisibility(View.GONE);
            //tvpersonalchattimestamp.setVisibility(View.GONE);
        }
        else
        {
            Log.d("Adapter",""+position);
            message.setText(which.getMessage());
            tvpersonalchattimestamp.setText(formatTimeStamp(which.getTimeStamp()));

            //senderRelativeLayout.setVisibility(View.GONE);
            //message1.setVisibility(View.INVISIBLE);
            //tvpersonalchattimestamp1.setVisibility(View.INVISIBLE );
        }
        return convertView;


        //updated code0

//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        PersonalChatMessage which = mData.get(position);
//
//        if(LoginActivity.loggedinuseremailid.equals(which.getSender()))
//        {
//            convertView=inflater.inflate(R.layout.custom_sender, parent, false);
//            TextView message = (TextView) convertView.findViewById(R.id.sender_msg);
//            TextView tvpersonalchattimestamp = (TextView) convertView.findViewById(R.id.sender_date);
//            message.setText(which.getMessage());
//            tvpersonalchattimestamp.setText(formatTimeStamp(which.getTimeStamp()));
//        }else{
//            convertView=inflater.inflate(R.layout.custom_receiver,parent,false);
//            TextView message = (TextView) convertView.findViewById(R.id.receiver_msg);
//            TextView tvpersonalchattimestamp = (TextView) convertView.findViewById(R.id.receiver_date);
//            message.setText(which.getMessage());
//            tvpersonalchattimestamp.setText(formatTimeStamp(which.getTimeStamp()));
//        }
//
//        return convertView;
    }

    public static String formatTimeStamp(long timeStamp){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }
}
