package me.kevingleason.pubnubchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vikas Deshpande on 1/21/2017.
 */

public class UsersAdapter extends ArrayAdapter<User>
{
    List<User> mData;
    Context mContext;
    private Set<String> onlineNow = new HashSet<String>();
    int mResource;


    public UsersAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData = objects;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }

        User user = mData.get(position);
        View userPresence;

        userPresence = convertView.findViewById(R.id.user_presence1);
        userPresence.setBackgroundDrawable( // If online show the green presence dot
                this.onlineNow.contains(user.getFnmae()+" "+user.getLname())
                        ? mContext.getResources().getDrawable(R.drawable.online_circle)
                        : null);
        TextView usernamee = (TextView) convertView.findViewById(R.id.tvusername);
        usernamee.setText(user.getFnmae()+" "+user.getLname());
        return convertView;
    }


    /**
     * Handle users. Fill the onlineNow set with current users. Data is used to display a green dot
     *   next to users who are currently online.
     * @param user UUID of the user online.
     * @param action The presence action
     */
    public void userPresence(String user, String action){
        boolean isOnline = action.equals("join") || action.equals("state-change");
        if (!isOnline && this.onlineNow.contains(user))
            this.onlineNow.remove(user);
        else if (isOnline && !this.onlineNow.contains(user))
            this.onlineNow.add(user);

        notifyDataSetChanged();
    }

    /**
     * Overwrite the onlineNow array with all the values attained from a call to hereNow().
     * @param onlineNow
     */
    public void setOnlineNow(Set<String> onlineNow){
        this.onlineNow = onlineNow;
        notifyDataSetChanged();
    }
}
