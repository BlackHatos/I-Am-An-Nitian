package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CompleteProfile extends AppCompatActivity
{
    private AutoCompleteTextView state_auto_complete;
    private EditText  start_year, end_year, user_phone,user_branch,user_degree;;
    private Button setCollege;
    private SharedPreferences sharedPreferences;
    private Button proceed;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        state_auto_complete = findViewById(R.id.state_auto_complete);
        setCollege = findViewById(R.id.setCollege);
        user_phone = findViewById(R.id.user_phone);
        user_degree = findViewById(R.id.user_degree);
        user_branch = findViewById(R.id.user_branch);
        start_year = findViewById(R.id.start_year);
        end_year = findViewById(R.id.end_year);
        proceed  = findViewById(R.id.proceed); //button

        // Clear the tempData shared preferences
        getSharedPreferences("tempData", MODE_PRIVATE).edit().clear().apply();

        // Get-set shared preferences
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        setPreferences();

        // Initializing progress dialog
        progressDialog = progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false); //prevent disappearing

        // Autocomplete
        String state_array[] = getResources().getStringArray(R.array.states);
        String degree_array[] = getResources().getStringArray(R.array.degree);

        ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, R.layout.custom_drop_down_list,
                R.id.custom_drop_down_text_view, state_array);
        state_auto_complete.setAdapter(state_adapter);

         // On Click the college edit text go to College Suggestion activity
         setCollege.setOnClickListener(v -> {
             startActivity(new Intent(CompleteProfile.this, CollegeSuggestions.class));
             overridePendingTransition(0, 0);
         });

         // Insert data into the server
         proceed.setOnClickListener(v -> {
             // Setting errors
             state_auto_complete.setError(null);
             user_phone.setError(null);
             setCollege.setError(null);
             user_degree.setError(null);
             user_branch.setError(null);
             start_year.setError(null);
             end_year.setError(null);

             // Getting credentials on click the sign up button
             String phone = user_phone.getText().toString().trim().replaceAll("\\s+","");
             String state = state_auto_complete.getText().toString().trim();
             String college = setCollege.getText().toString().trim();
             String degree = user_degree.getText().toString().trim();
             String branch = user_branch.getText().toString().trim();
             String from = start_year.getText().toString().trim().replaceAll("\\s+","");
             String to = end_year.getText().toString().trim().replaceAll("\\s+","");

             // If every thing is OK
             proceedToServer(phone, state, college, degree, branch, from, to);
         });

    }

    private void proceedToServer(final String phone, final  String state,
                                 final String college, final String degree,
                                 final  String branch, final String from, final  String to)
    {
        //===> show progress bar first
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        // Disable user interaction when progress dialog appears
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

       final String url = "https://app.iamannitian.com/app/complete-profile.php";
        //error
        StringRequest sr = new StringRequest(1, url,
                response -> {

                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    try
                    {
                        JSONObject object = new JSONObject(response);
                        String status =  object.getString("status");

                        if(status.equals("1"))
                        {
                            String phonex = object.getString("phone");
                            String statex= object.getString("state");
                            String collegex = object.getString("college");
                            String degreex = object.getString("degree");
                            String branchx = object.getString("branch");
                            String startx = object.getString("start");
                            String endx = object.getString("end");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userPhone",phonex);
                            editor.putString("userState", statex);
                            editor.putString("userCollege",collegex);
                            editor.putString("userDegree",degreex);
                            editor.putString("userBranch",branchx);
                            editor.putString("userStartYear",startx);
                            editor.putString("userEndYear",endx);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "profile updated successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CompleteProfile.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch
                    (JSONException e)
                    {
                        Toast.makeText(this, "failed to update profile-2", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }){
            @Override
            public Map<String, String> getParams() {
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

    public void goToMain(View view) // On click skip button
    {
        Intent intent = new Intent(CompleteProfile.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    // On back press clear preferences
    @Override
    public void onBackPressed()
    {
        setPreferences();
        super.onBackPressed();
    }

    // Since activity is declared single instance
    // on returning back to this activity this method is called
    // so that we can update the college edit text

    @Override
    public void onResume()
    {
        super.onResume();

        String college = getSharedPreferences("tempData", MODE_PRIVATE)
                .getString("tempCollege", "null");

        String college_name =  getSharedPreferences("appData", MODE_PRIVATE)
                .getString("userCollege", "null");

        if(college.equals("null") && college_name.equals("null"))
        {
            setCollege.setText("");
        }
        else if(college.equals("null") && !college_name.equals("null"))
        {
            setCollege.setText(college_name);
        }
        else if(!college.equals("null") && college_name.equals("null"))
        {
            setCollege.setText(college);
        }
        else if(!college.equals("null") && !college_name.equals("null"))
        {
            setCollege.setText(college);
        }
    }

    public void setPreferences()
    {
        String phone = sharedPreferences.getString("userPhone", "null");
        String state = sharedPreferences.getString("userState", "null");
        String college = sharedPreferences.getString("userCollege", "null");
        String degree = sharedPreferences.getString("userDegree", "null");
        String branch = sharedPreferences.getString("userBranch", "null");
        String start = sharedPreferences.getString("userStartYear","null");
        String end =  sharedPreferences.getString("userEndYear","null");

        // Phone
        if(phone.equals("null"))
        {
            user_phone.setText("");
        }
        else
        {
            user_phone.setText(phone);
        }

        // State
        if(state.equals("null"))
        {
            state_auto_complete.setText("");
        }
        else
        {
            state_auto_complete.setText(state);
        }

        // College
        if(college.equals("null"))
        {
            setCollege.setText("");
        }
        else
        {
            setCollege.setText(college);
        }

        // Degree
        if(degree.equals("null"))
        {
            user_degree.setText("");
        }
        else
        {
            user_degree.setText(degree);
        }

        // Branch
        if(branch.equals("null"))
        {
            user_branch.setText("");
        }
        else
        {
            user_branch.setText(branch);
        }

        // Start year
        if(start.equals("null"))
        {
            start_year.setText("");
        }
        else
        {
            start_year.setText(start);
        }

        // End yar
        if(end.equals("null"))
        {
            end_year.setText("");
        }
        else
        {
            end_year.setText(end);
        }
    }
}


