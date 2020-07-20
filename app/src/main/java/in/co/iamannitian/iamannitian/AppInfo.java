package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class AppInfo extends AppCompatActivity {
    private TextView app_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_info);

        app_version =  findViewById(R.id.app_version);

        //=================getting version name
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            String version = packageInfo.versionName;
            app_version.setText("Version "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
