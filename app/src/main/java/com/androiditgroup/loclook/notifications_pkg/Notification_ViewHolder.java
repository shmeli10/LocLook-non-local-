package com.androiditgroup.loclook.notifications_pkg;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androiditgroup.loclook.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 23.03.2016.
 */
public class Notification_ViewHolder extends RecyclerView.ViewHolder {

    public TextView userNameTV;
    public TextView notificationDateTV;
    public TextView messageTV;

    public ImageView notificationTypeIV;

    public CircleImageView userAvatarCIV;

    public LinearLayout notificationLL;
    public LinearLayout hLineLL;

    public Notification_ViewHolder(View view) {
        super(view);

        userNameTV          = (TextView) view.findViewById(R.id.NotificationRow_UserNameTV);
        notificationDateTV  = (TextView) view.findViewById(R.id.NotificationRow_DateTV);
        messageTV           = (TextView) view.findViewById(R.id.NotificationRow_MessageTV);

        notificationTypeIV  = (ImageView) view.findViewById(R.id.NotificationRow_TypeIV);

        userAvatarCIV   = (CircleImageView) view.findViewById(R.id.NotificationRow_UserAvatarCIV);

        notificationLL  = (LinearLayout) view.findViewById(R.id.NotificationRow_NotificationContainerLL);
        hLineLL         = (LinearLayout) view.findViewById(R.id.NotificationRow_HLine_LL);
    }
}
