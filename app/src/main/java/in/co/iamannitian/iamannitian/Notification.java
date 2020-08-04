package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import me.at.nitsxr.NotificationReceiver;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;

import static me.at.nitsxr.App.CHANNEL_1_ID;
import static me.at.nitsxr.App.CHANNEL_2_ID;

public class Notification extends AppCompatActivity
{
    private NotificationManagerCompat notificationManager;
    private EditText title, message;
    private Button channel, channel2;

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
        String my_title = title.getText().toString();
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

       notificationManager.notify(1,notification);
    }

    public void sendOnChannel2(View view)
    {
        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_collapse);
        RemoteViews expandView = new RemoteViews(getPackageName(), R.layout.notification_expand);

        //onClick notification open an activity
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1  = inflater.inflate(R.layout.notification_collapse,(LinearLayout)findViewById(R.id.collapse_notify),true);
        View v2  = inflater.inflate(R.layout.notification_expand,(LinearLayout)findViewById(R.id.expand_notify),true);

        ImageView banner1  = v1.findViewById(R.id.collapse_image_view);
        ImageView banner2  = v2.findViewById(R.id.image_view_expand);

        Glide.with(this)
                .asBitmap()
                .load("http://app.thenextsem.com/images/Jamshedpur.jpg")
                .into(banner1);

        Glide.with(this)
                .asBitmap()
                .load("http://app.thenextsem.com/images/Jamshedpur.jpg")
                .into(banner2);


        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.imnitian)
                .setCustomContentView(collapseView)
                .setContentIntent(contentIntent)
                .setCustomBigContentView(expandView)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1,notification);
    }
}
