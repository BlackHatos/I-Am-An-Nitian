package in.co.iamannitian.iamannitian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.util.HashMap;
import java.util.Map;
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

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
        {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task)
            {
                if (task.isSuccessful())
                {
                    String token=task.getResult().getToken();
                   registerToken(token);
                }
            }
        });

        notificationManager = NotificationManagerCompat.from(this);

        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        channel = findViewById(R.id.send1);
        channel2 = findViewById(R.id.send2);
    }

    private void registerToken(final String token)
    {
        final String url =  "http://app.thenextsem.com/register.php";

        StringRequest sr = new StringRequest(1, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                         //do something with response
                    }
                }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("Token",token);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(Notification.this);
        rq.add(sr);
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
