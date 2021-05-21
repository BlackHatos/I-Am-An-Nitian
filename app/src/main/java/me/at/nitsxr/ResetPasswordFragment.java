package me.at.nitsxr;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.co.iamannitian.iamannitian.LoginActivity;
import in.co.iamannitian.iamannitian.R;

public class ResetPasswordFragment extends Fragment
{
    private static String user_mail="";
    private Toolbar toolbar;
    private EditText password1, password2;
    private Button reset_pass; // button

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reset_password, container, false);
        password1 = rootView.findViewById(R.id.password1);
        password2 = rootView.findViewById(R.id.password2);
        reset_pass = rootView.findViewById(R.id.reset_pass); // button

        setUpToolbarMenu(rootView);

        //On click back navigation move to previous fragment
       toolbar.setNavigationOnClickListener(v -> {
            getFragmentManager().popBackStackImmediate();
        });

        reset_pass.setOnClickListener(v -> {
            password1.setError(null);
            password2.setError(null);

            String password = password1.getText().toString().trim().replaceAll("\\s+","");
            String again_pass = password2.getText().toString().trim().replaceAll("\\s+","");

            if(password.isEmpty())
            {
                password1.requestFocus();
                password1.setError("required");
                return;
            }

            if(again_pass.isEmpty())
            {
                password2.requestFocus();
                password2.setError("required");
                return;
            }

            // Check if password are same
            if(!password.equals(again_pass))
            {
                Toast.makeText(getActivity(), "password did not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // If everything OK
            changePassword(password);
        });

        return rootView;
    }

    private void changePassword(final String password)
    {
        final String url = "https://app.iamannitian.com/app/reset-password.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {

                    try
                    {
                        JSONObject object = new JSONObject(response);

                        String status = object.getString("status");
                        String message = object.getString("message");

                        if(status.equals("0"))
                        {
                            Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);
                              getActivity().finish();
                        }
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(getActivity(), "an error occurred", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
            Toast.makeText(getActivity(), "an error occurred", Toast.LENGTH_SHORT).show();
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("passwordKey", password);
                map.put("emailKey", user_mail);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(sr);
    }

    public static ResetPasswordFragment onNewInstance2(String mail)
    {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        user_mail =  mail;
        return fragment;
    }


    private void openFragment()
    {
        OtpFragment fragment = OtpFragment.onNewInstance("null");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragmentContainer, fragment, "OTP_FRAGMENT").commit();
    }


    private void setUpToolbarMenu(View rootView)
    {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Back");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar =  ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }
}
