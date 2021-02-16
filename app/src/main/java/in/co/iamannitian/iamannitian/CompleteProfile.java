package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static in.co.iamannitian.iamannitian.CollegeSuggestions.NAME_COLLEGE;

public class CompleteProfile extends AppCompatActivity
{
    private AutoCompleteTextView state_auto_complete,user_degree;
    private EditText  start_year, end_year, user_phone,user_branch,college_auto_complete;
    public static final String COLLEGE_NAME = "";
    private SharedPreferences sharedPreferences;
    private Button proceed;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        state_auto_complete = findViewById(R.id.state_auto_complete);
        college_auto_complete = findViewById(R.id.college_auto_complete);
        user_phone = findViewById(R.id.user_phone);
        user_degree = findViewById(R.id.user_degree);
        user_branch = findViewById(R.id.user_branch);
        start_year = findViewById(R.id.start_year);
        end_year = findViewById(R.id.end_year);
        proceed  = findViewById(R.id.proceed); //button

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        //===> initializing progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false); //prevent disappearing

        String state_array[] = getResources().getStringArray(R.array.states);
        String degree_array[] = getResources().getStringArray(R.array.degree);

        ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, R.layout.custom_drop_down_list,
                R.id.custom_drop_down_text_view, state_array);
        state_auto_complete.setAdapter(state_adapter);

        ArrayAdapter<String> degree_adapter = new ArrayAdapter<>(this, R.layout.custom_drop_down_list,
                R.id.custom_drop_down_text_view, degree_array);
        user_degree.setAdapter(degree_adapter);

        //===> get the college name reverse back from CollegeSuggestion activity
         Intent intent = getIntent();
         String name_college = intent.getStringExtra(NAME_COLLEGE);
         college_auto_complete.setText(name_college);

        setPreferences();
         //===> on click the college edit text go to CollegeSuggestion activity
         college_auto_complete.setOnTouchListener((v, event) -> {
             //=========> creating shared preferences
             SharedPreferences.Editor editor = sharedPreferences.edit();
             editor.putString("userPhone",user_phone.getText().toString().trim());
             editor.putString("userState", state_auto_complete.getText().toString().trim());
             editor.putString("userCollege",college_auto_complete.getText().toString().trim());
             editor.putString("userDegree",user_degree.getText().toString().trim());
             editor.putString("userBranch",user_branch.getText().toString().trim());
             editor.putString("userStartYear",start_year.getText().toString().trim());
             editor.putString("userEndYear",end_year.getText().toString().trim());
             editor.apply();

             Intent intent1 = new Intent(getApplicationContext(), CollegeSuggestions.class);
             intent1.putExtra(COLLEGE_NAME,college_auto_complete.getText().toString().trim());
             startActivity(intent1);
             overridePendingTransition(0, 0);
             return true;
         });

         //===> insert data into the server
         proceed.setOnClickListener(v -> {
             //setting errors
             state_auto_complete.setError(null);
             user_phone.setError(null);
             college_auto_complete.setError(null);
             user_degree.setError(null);
             user_branch.setError(null);
             start_year.setError(null);
             end_year.setError(null);

             //getting credentials on click the sign up button
             String phone = user_phone.getText().toString().trim().replaceAll("\\s+","");
             String state = state_auto_complete.getText().toString().trim();
             String college = college_auto_complete.getText().toString().trim();
             String degree = user_degree.getText().toString().trim();
             String branch = user_branch.getText().toString().trim();
             String from = start_year.getText().toString().trim().replaceAll("\\s+","");
             String to = end_year.getText().toString().trim().replaceAll("\\s+","");

             if(phone.isEmpty())
             {
                 user_phone.requestFocus();
                 user_phone.setError("required");
                 return;
             }

             if(state.isEmpty())
             {
                 state_auto_complete.requestFocus();
                 state_auto_complete.setError("required");
                 return;
             }

             if(college.isEmpty())
             {
                 college_auto_complete.requestFocus();
                 college_auto_complete.setError("required");
                 return;
             }

             if(degree.isEmpty())
             {
                 user_degree.requestFocus();
                 user_degree.setError("required");
                 return;
             }

             if(branch.isEmpty())
             {
                 user_branch.requestFocus();
                 user_branch.setError("required");
                 return;
             }

             if(from.isEmpty())
             {
                 start_year.requestFocus();
                 start_year.setError("required");
                 return;
             }

             if(to.isEmpty())
             {
                 end_year.requestFocus();
                 end_year.setError("required");
                 return;
             }

             //========>> if all is well
             proceedToServer(phone, state, college, degree, branch, from, to);
         });

    }

    public void setPreferences()
    {
        //====> set the the values in the complete profile edit text
        user_phone.setText(sharedPreferences.getString("userPhone",""));
        user_degree.setText(sharedPreferences.getString("userDegree",""));
        user_branch.setText(sharedPreferences.getString("userBranch",""));
        start_year.setText(sharedPreferences.getString("userStartYear",""));
        end_year.setText(sharedPreferences.getString("userEndYear",""));
        state_auto_complete.setText(sharedPreferences.getString("userState",""));
        college_auto_complete.setText(sharedPreferences.getString("userCollege",""));
    }

    private void proceedToServer(final String phone, final  String state,
                                 final String college, final String degree,
                                 final  String branch, final String from, final  String to)
    {

        //===> show progress bar first
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        //===> disable user interaction when progress dialog appears
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

       final String url = "https://app.thenextsem.com/app/complete_profile.php";
        StringRequest sr = new StringRequest(1, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response: ", response);

                        String response_array[] = response.split(",");

                        if(response_array[0].equals("1"))
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userPhone",response_array[1]);
                            editor.putString("userState", response_array[2]);
                            editor.putString("userCollege",response_array[3]);
                            editor.putString("userDegree",response_array[4]);
                            editor.putString("userBranch",response_array[5]);
                            editor.putString("userStartYear",response_array[6]);
                            editor.putString("userEndYear",response_array[7]);
                            editor.apply();

                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else if(response_array[0].equals("0"))
                        {
                            progressDialog.dismiss();
                            //===> on dialog dismiss back to interaction mode
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(),response_array[1], Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                //===> on dialog dismiss back to interaction mode
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map =  new HashMap<>();
                map.put("idKey", sharedPreferences.getString("userId", ""));
                map.put("phoneKey", phone);
                map.put("stateKey", state);
                map.put("collegeKey", college);
                map.put("degreeKey", degree);
                map.put("branchKey", branch);
                map.put("fromKey", from);
                map.put("toKey", to);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(sr);
    }

    public void goToMain(View view) //===> on clicking skip button
    {
        setPreferences();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    //===> on back press clear preferences
    @Override
    public void onBackPressed()
    {
        setPreferences();
        super.onBackPressed();
    }
}


