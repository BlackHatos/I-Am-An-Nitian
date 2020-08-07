package me.at.nitsxr;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.messaging.RemoteMessage;
import androidx.core.app.NotificationManagerCompat;
import in.co.iamannitian.iamannitian.MainActivity;
import in.co.iamannitian.iamannitian.R;
import static me.at.nitsxr.App.CHANNEL_1_ID;
import static me.at.nitsxr.App.CHANNEL_2_ID;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private NotificationTarget notificationTarget1, notificationTarget2;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message)
    {
        String msg[] = message.split(",");

        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_collapse);
        RemoteViews expandView = new RemoteViews(getPackageName(), R.layout.notification_expand);

        //===> this is necessary to use this to set image in custom notification image view
        collapseView.setImageViewResource(R.id.collapse_image_view,R.drawable.nitrkl);
        expandView.setImageViewResource(R.id.image_view_expand,R.drawable.nitrkl);

        //===> setting the text in custom text views
        collapseView.setTextViewText(R.id.notify_collap_title, msg[1]);
        expandView.setTextViewText(R.id.text_view_expand, msg[1]);

        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setSmallIcon(R.drawable.imnitian)
                .setCustomContentView(collapseView)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(expandView)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();


        notificationTarget1 = new NotificationTarget(this,
                R.id.collapse_image_view,collapseView,builder, 0);

        notificationTarget2  = new NotificationTarget(this,
                R.id.image_view_expand,expandView,builder, 0);

        //===> setting up images into custom notification image view using glide notification target
        //===> put url here from data base
        Glide.with(this)
                .asBitmap()
                .load(msg[0].trim())
                .into(notificationTarget1);

        Glide.with(this)
                .asBitmap()
                .load(msg[0].trim())
                .into(notificationTarget2);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0,builder);
    }

}