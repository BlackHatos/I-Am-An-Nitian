package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutUs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    public void goToFb(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/iamannitian"));
        startActivity(intent);
    }

    public void goToInsta(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/i_am_an_nitian/"));
        startActivity(intent);
    }

    public void goToYoutube(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.youtube.com/iamannitian"));
        startActivity(intent);
    }
}
