package me.at.nitsxr;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.RemoteMessage;
import androidx.core.app.NotificationManagerCompat;
import in.co.iamannitian.iamannitian.MainActivity;
import in.co.iamannitian.iamannitian.R;
import static me.at.nitsxr.App.CHANNEL_1_ID;
import static me.at.nitsxr.App.CHANNEL_2_ID;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

   // private NotificationTarget notificationTarget1, notificationTarget2;
    private Bitmap bitmap;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        String message = remoteMessage.getData().get("message");
        String url = remoteMessage.getData().get("url");
        showNotification(message, url);
    }

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);

    }

    private void showNotification(String message, String url)
    {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>(){
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        // Open specific activity on notification click

        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        //Bitmap  art  = BitmapFactory.decodeResource(getResources(), R.drawable.nittrichy);

        Notification builder = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setSmallIcon(R.drawable.imnitian)
                .setContentTitle("I Am An NITian")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(message)
                .setColor(Color.WHITE)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(bitmap)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setSubText("Tap to open")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true).setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap).bigLargeIcon(null))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0,builder);
    }

}