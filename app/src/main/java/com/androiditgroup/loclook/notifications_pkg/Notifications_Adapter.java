package com.androiditgroup.loclook.notifications_pkg;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.answers_pkg.Answers_Activity;
import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by OS1 on 23.03.2016.
 */
public class Notifications_Adapter extends RecyclerView.Adapter<Notification_ViewHolder> {

    private Context context;

    private List<Notification_ListItems> notifications;

    // private OnAvatarLoadListener            avatarLoadListener;
    private OnNotificationClickListener     notificationClickListener;

    private float density;

    // private String  mediaLinkHead = "http://192.168.1.229:7000";
    // private String  mediaLinkHead = "http://192.168.1.230:7000";
    // private String  mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

    private final String LOG_TAG = "myLogs";

    //////////////////////////////////////////////////////////////////////////////////////

    // интерфейс для работы с Notifications_Activity
    public interface OnNotificationClickListener {
        void onNotificationClick();
    }

//    // интерфейс для работы с Notifications_Activity
//    public interface OnAvatarLoadListener {
//        void onAvatarLoading(ImageView avatarImageView, int authorId);
//    }

    //////////////////////////////////////////////////////////////////////////////////////

    public Notifications_Adapter(Context context, List<Notification_ListItems> notifications) {
        this.context        = context;
        this.notifications  = notifications;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Notification_ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.notification_row, null);
        Notification_ViewHolder holder = new Notification_ViewHolder(v);

        holder.getLayoutPosition();

        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        // если Notifications_Activity выполняет интерфейс
        if (context instanceof OnNotificationClickListener)
            // получаем ссылку на Notifications_Activity
            notificationClickListener = (OnNotificationClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnNotificationClickListener");

//        // если Notifications_Activity выполняет интерфейс
//        if (context instanceof OnAvatarLoadListener)
//            // получаем ссылку на Notifications_Activity
//            avatarLoadListener = (OnAvatarLoadListener) context;
//        else
//            throw new ClassCastException(context.toString() + " must implement OnAvatarLoadListener");

        //////////////////////////////////////////////////////////////////////////////////

        return holder;
    }

    @Override
    public void onBindViewHolder(final Notification_ViewHolder viewHolder, final int position) {

        // imageLoader = MySingleton.getInstance(context).getImageLoader();

        final Notification_ListItems notification = notifications.get(position);

        /////////////////////////////////////////////////////////////////////////////

        final int publicationId         = notification.getPublicationId();
        final int notificationId        = notification.getNotificationId();
        final int notificationAuthorId  = notification.getNotificationAuthorId();

        String notificationAuthorAvatarLink = notification.getNotificationAuthorAvatarLink();
        String notificationAuthorName       = notification.getNotificationAuthorName();
        String notificationDate             = notification.getNotificationDate();
        String notificationType             = notification.getNotificationType();
        String notificationText             = notification.getNotificationText();

        /////////////////////////////////////////////////////////////////////////////

        // avatarLoadListener.onAvatarLoading(viewHolder.userAvatarCIV, notificationAuthorId);

        //
        if((notificationAuthorAvatarLink != null) && (!notificationAuthorAvatarLink.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + notificationAuthorAvatarLink)
                    .placeholder(R.drawable.anonymous_avatar_grey)
                    .into(viewHolder.userAvatarCIV);
        else
            viewHolder.userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);


        if(notificationType.equals("newReply"))
            viewHolder.notificationTypeIV.setImageResource(R.drawable.comments_icon);
        else
            viewHolder.notificationTypeIV.setImageResource(R.drawable.like_icon_active);

        viewHolder.userNameTV.setText(notificationAuthorName);
        viewHolder.notificationDateTV.setText(notificationDate);

        viewHolder.messageTV.setText(notificationText);

        viewHolder.notificationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Log.d(LOG_TAG, "Notifications_Adapter: onBindViewHolder: click on notificationId= " +notificationId+ ", with publicationId= "  + publicationId);

                // notificationClickListener.onNotificationClick();

                Intent intent = new Intent(context,Answers_Activity.class);
                intent.putExtra("publicationId", publicationId);

                ((Notifications_Activity) context).startActivityForResult(intent, 6);
            }
        });

        // если это не последний элемент в списке
        if((notifications.size() - 1) != position) {

            // создаем горизонтальную линию
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((int) (1 * density)));
//            setMargins(layoutParams, 10, 0, 10, 0);

            View hLine = new View(context);
            hLine.setLayoutParams(layoutParams);
            hLine.setBackgroundResource(R.color.h_line_grey);

            // добавить линию в контейнер
            viewHolder.hLineLL.addView(hLine);
        }
        // последний элемент в списке
        else
            // очистить контейнер
            viewHolder.hLineLL.removeAllViews();
    }

    public void clearAdapter () {
        notifications.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != notifications ? notifications.size() : 0);
    }
}
