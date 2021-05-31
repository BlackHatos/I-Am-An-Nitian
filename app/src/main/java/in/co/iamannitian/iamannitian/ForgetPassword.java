package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import me.at.nitsxr.OtpFragment;

public class ForgetPassword extends AppCompatActivity
{
    private Toolbar toolbar;
    private Button proceed; // button
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        proceed = findViewById(R.id.proceed);
        email = findViewById(R.id.email);

        proceed.setOnClickListener(v -> {
             String mail = email.getText().toString().trim().replaceAll("\\s+","");
              email.setError(null);

             if(mail.isEmpty())
             {
                email.requestFocus();
                email.setError("required");
                return;
             }

            sendOtp(mail);
        });

        setUpToolbarMenu();
    }

    public void sendOtp(final String mail)
    {
        final String url = "https://app.iamannitian.com/php-mailer/reset-pass-otp.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {

                    try
                    {
                        JSONObject object = new JSONObject(response);

                        String status = object.getString("status");

                        if(status.equals("0"))
                        {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // here we are taking valid email from server
                            // because someone can alter it in between
                            openFragment(object.getString("email"));
                        }
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), "failed to send otp", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
            Toast.makeText(ForgetPassword.this, "failed to send otp", Toast.LENGTH_SHORT).show();
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("emailKey", mail);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(ForgetPassword.this);
        rq.add(sr);
    }


    private void openFragment(String mail)
    {
        OtpFragment fragment = OtpFragment.onNewInstance(mail);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragmentContainer, fragment, "OTP_FRAGMENT").commit();
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Back");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }
}
