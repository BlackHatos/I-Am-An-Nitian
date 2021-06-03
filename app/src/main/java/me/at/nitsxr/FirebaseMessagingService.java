package me.at.nitsxr;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.RemoteMessage;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.co.iamannitian.iamannitian.LoginOrSignUpActivity;
import in.co.iamannitian.iamannitian.MainActivity;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;
import static me.at.nitsxr.App.CHANNEL_1_ID;
import static me.at.nitsxr.App.CHANNEL_2_ID;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private PendingIntent pendingIntent;
    private SharedPreferences sharedPreferences;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        String title = remoteMessage.getData().get("title");
        String url = remoteMessage.getData().get("url");
        String message = remoteMessage.getData().get("message");
        String date = remoteMessage.getData().get("date");
        String id = remoteMessage.getData().get("id");
        String count = remoteMessage.getData().get("count");
        String status = remoteMessage.getData().get("status");


        // set news data
        final NewsGetterSetter getterSetter = new NewsGetterSetter();
        getterSetter.setImageUrl(url);
        getterSetter.setNewsTitle(title);
        getterSetter.setNewsDate(date);
        getterSetter.setNewsId(id);
        getterSetter.setNewsDescp(message);
        getterSetter.setStatus(status);
        getterSetter.setCount(count);

        showNotification(title, message, url, getterSetter);
    }

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
    }

    private void showNotification(String title, String message, String url, NewsGetterSetter getterSetter)
    {
        // processing the title
        String final_title = "";
        String str[] =  title.split(" ");

        if(str.length <= 5)
            final_title  = title;
        else
            final_title  = str[0]+" "+str[1]+" "+str[2]+" "+str[3]+" "+str[4]+"..";

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        // Open specific activity on notification click
        Intent intent =  new Intent(this,OnViewPagerClick.class);
        Bundle b = new Bundle();
        b.putSerializable("sampleObject", getterSetter);
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("temp", "1");


       if(!userId.equals(""))
           pendingIntent = PendingIntent.getActivity(this,0,intent,
                   PendingIntent.FLAG_UPDATE_CURRENT);
       else
           pendingIntent = PendingIntent.getActivity(this,0,
                   new Intent(this, LoginOrSignUpActivity.class)
                   ,PendingIntent.FLAG_UPDATE_CURRENT);

        // convert app logo into bitmap
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.imnitian);

        Notification builder = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notifications_active2)
                .setContentTitle(final_title)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(message)
                .setColor(Color.WHITE)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(largeIcon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setSubText("Tap to open")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true).setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(getBitmapFromURL(url)).bigLargeIcon(null))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0,builder);
        //notificationManager.notify(1,builder);
    }

    //  Convert image from url into bitmap
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}