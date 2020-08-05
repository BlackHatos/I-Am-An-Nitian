package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import me.at.nitsxr.NotificationReceiver;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;


import static me.at.nitsxr.App.CHANNEL_1_ID;
import static me.at.nitsxr.App.CHANNEL_2_ID;

public class Notification extends AppCompatActivity
{
    private NotificationManagerCompat notificationManager;
    private EditText title, message;
    private Button channel, channel2;
    private NotificationTarget notificationTarget1, notificationTarget2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationManager = NotificationManagerCompat.from(this);

        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        channel = findViewById(R.id.send1);
        channel2 = findViewById(R.id.send2);
    }

    public void sendOnChannel1(View view)
    {

        sendNotification(1);

        /*String my_title = title.getText().toString();
        String my_message =  message.getText().toString();

        //onClick notification open an activity
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage",my_message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,0, broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new  NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.imnitian)
                .setContentTitle(my_title)
                .setContentText(my_message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setColor(Color.BLUE)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.imnitian, "Toast", actionIntent)
                .setAutoCancel(true)
                .build();

       notificationManager.notify(1,notification);*/
    }

    public void sendOnChannel2(View view)
    {
        sendNotification(2);
    }


    public void sendNotification(int id)
    {

        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_collapse);
        RemoteViews expandView = new RemoteViews(getPackageName(), R.layout.notification_expand);

        //===> onClick notification open an activity
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        //===> this is necessary to use this to set image in custom notification image view
        collapseView.setImageViewResource(R.id.collapse_image_view,R.drawable.nitrkl);
        expandView.setImageViewResource(R.id.image_view_expand,R.drawable.nitrkl);

        //===> setting the text in custom text views
        collapseView.setTextViewText(R.id.notify_collap_title, "Gate 2021 result declared check the result here");
        expandView.setTextViewText(R.id.text_view_expand, "Gate 2021 result declared check the result here");

        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.imnitian)
                .setCustomContentView(collapseView)
                .setContentIntent(contentIntent)
                .setCustomBigContentView(expandView)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();

        notificationTarget1 = new NotificationTarget(this,
                R.id.collapse_image_view,collapseView,notification, id);

        notificationTarget2  = new NotificationTarget(this,
                R.id.image_view_expand,expandView,notification, id);

        //===> setting up images into custom notification image view using glide notification target
        //===> put url here from data base
        Glide.with(this)
                .asBitmap()
                .load("http://app.thenextsem.com/images/Bhopal.jpg")
                .into(notificationTarget1);

        Glide.with(this)
                .asBitmap()
                .load("http://app.thenextsem.com/images/Bhopal.jpg")
                .into(notificationTarget2);

        notificationManager.notify(id,notification);
    }
}
