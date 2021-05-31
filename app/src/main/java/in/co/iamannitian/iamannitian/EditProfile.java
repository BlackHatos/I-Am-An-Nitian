package in.co.iamannitian.iamannitian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.datatransport.cct.internal.LogEvent;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class EditProfile extends AppCompatActivity
{
    private Toolbar toolbar;
    private EditText name, email, phone, degree, state, branch, start, end;
    private Button college;
    private ImageView profilePic, editImage;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private Snackbar snackbar;
    private RelativeLayout popupLayout;
    private PopupWindow popupWindow;
    private ImageView previewImage, previewImage2;
    private Bitmap bitmap;
    private String filePath = null;
    private static final String ROOT_URL = "http://app.iamannitian.com/app/upload_picture.php";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST =1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setUpToolbarMenu();
        initVariables();
        setData();

        // Clear the tempData shared preferences
        getSharedPreferences("tempData2", MODE_PRIVATE).edit().clear().apply();

        requestStoragePermission();

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        college.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfile.this, CollegeSuggestions.class);
            intent.putExtra("ACTIVITY_TYPE", 1);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    public void initVariables()
    {
        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        phone = findViewById(R.id.user_phone);
        college = findViewById(R.id.user_college);
        state = findViewById(R.id.user_state);
        degree = findViewById(R.id.user_degree);
        branch = findViewById(R.id.user_branch);
        start = findViewById(R.id.start_year);
        end = findViewById(R.id.end_year);
        profilePic = findViewById(R.id.profile_image);
        editImage = findViewById(R.id.edit_image);
        previewImage = findViewById(R.id.preview_image);

        progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void saveProfile()
    {

        String user_name = name.getText().toString().trim();
        String user_email = email.getText().toString().trim().replaceAll("\\s+","");;
        String user_phone = phone.getText().toString().trim();
        String user_state = state.getText().toString().trim();
        String user_college = college.getText().toString().trim();
        String user_branch = branch.getText().toString().trim();
        String user_degree = degree.getText().toString().trim();
        String start_year = start.getText().toString().trim();
        String end_year = end.getText().toString().trim();

        // mandatory fields

        name.setError(null);
        email.setError(null);

        if(user_name.isEmpty())
        {
            name.requestFocus();
            name.setError("required");
            return;
        }

        if(user_email.isEmpty())
        {
            email.requestFocus();
            email.setError("required");
            return;
        }

        proceedToServer(user_name, user_email, user_phone, user_state, user_college,
                user_degree, user_branch, start_year, end_year);
    }

    private void proceedToServer(String user_name, String user_email, String user_phone,
                                 String user_state, String user_college,
                                 String user_degree, String user_branch,
                                 String start_year, String end_year)
    {
        // Show progress bar
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        // Disable suer interaction
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        final String url = "https://app.iamannitian.com/app/update-profile.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {

                  progressDialog.dismiss();
                    // Enable user interaction
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    try
                    {
                        JSONObject object = new JSONObject(response);
                        String status =  object.getString("status");
                        String message = object.getString("message");

                        if(status.equals("1"))
                        {
                            String name = object.getString("name");
                            String email = object.getString("email");
                            String phone = object.getString("phone");
                            String state = object.getString("state");
                            String college = object.getString("college");
                            String degree = object.getString("degree");
                            String branch = object.getString("branch");
                            String start = object.getString("from");
                            String end = object.getString("to");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userName",name);
                            editor.putString("userEmail",email);
                            editor.putString("userPhone",phone);
                            editor.putString("userState", state);
                            editor.putString("userCollege",college);
                            editor.putString("userDegree",degree);
                            editor.putString("userBranch",branch);
                            editor.putString("userStartYear",start);
                            editor.putString("userEndYear",end);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(EditProfile.this, UserProfile.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch
                    (JSONException e)
                    {
                        Toast.makeText(this, "failed to save changes", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                     Toast.makeText(this, "failed to save changes", Toast.LENGTH_SHORT).show();
                }){
            @Override
            public Map<String, String> getParams() {
                Map<String, String> map =  new HashMap<>();
                map.put("idKey", sharedPreferences.getString("userId", ""));
                map.put("nameKey", user_name);
                map.put("emailKey", user_email);
                map.put("phoneKey", user_phone);
                map.put("stateKey", user_state);
                map.put("collegeKey", user_college);
                map.put("degreeKey", user_degree);
                map.put("branchKey", user_branch);
                map.put("fromKey", start_year);
                map.put("toKey", end_year);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(sr);
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.save_button);
       Button saveProfile =  menuItem.
                getActionView().findViewById(R.id.saveButton);
       saveProfile.setOnClickListener(v -> saveProfile());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(EditProfile.this, UserProfile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handing hardware back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    startActivity(new Intent(EditProfile.this, UserProfile.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setData()
    {
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        String user_name = sharedPreferences.getString("userName", "");
        String user_email = sharedPreferences.getString("userEmail", "");
        String user_phone = sharedPreferences.getString("userPhone", "");
        String imageUrl = sharedPreferences.getString("userPicUrl", "");
        String user_state = sharedPreferences.getString("userState", "");
        String user_college = sharedPreferences.getString("userCollege", "");
        String user_branch = sharedPreferences.getString("userBranch", "");
        String user_degree = sharedPreferences.getString("userDegree", "");
        String start_year = sharedPreferences.getString("userStartYear", "");
        String end_year = sharedPreferences.getString("userEndYear", "");


        name.setText(user_name+" ");

        // put cursor at the end of the edit text
        name.setSelection(name.getText().length());

        email.setText(user_email);

        // Phone
        if(user_phone.equals("null"))
        {
            phone.setText("");
        }
        else
        {
            phone.setText(user_phone);
        }

        // State
        if(user_state.equals("null"))
        {
            state.setText("");
        }
        else
        {
            state.setText(user_state);
        }

        // College
        if(user_college.equals("null"))
        {
            college.setText("");
        }
        else
        {
            college.setText(user_college);
        }

        // Degree
        if(user_degree.equals("null"))
        {
            degree.setText("");
        }
        else
        {
            degree.setText(user_degree);
        }

        // Branch
        if(user_branch.equals("null"))
        {
            branch.setText("");
        }
        else
        {
            branch.setText(user_branch);
        }

        // Start year
        if(start_year.equals("null"))
        {
            start.setText("");
        }
        else
        {
            start.setText(start_year);
        }

        // End yar
        if(end_year.equals("null"))
        {
            end.setText("");
        }
        else
        {
            end.setText(end_year);
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.usericon)
                .fitCenter()
                .centerInside()
                .into(profilePic);
    }

    public void  showPopup(View view)
    {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.upoad_image_popup_layout,null);
        boolean focusable = false;
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView,width,height,focusable);
        popupWindow.setAnimationStyle(R.style.windowAnimationTransition);
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        Button selectPhoto = popupView.findViewById(R.id.select_image);
        popupLayout = popupView.findViewById(R.id.popupLayout);

        // imageview to set image selected from gallery
        previewImage = popupView.findViewById(R.id.preview_image);
        // this imageview is used to see already set image
        previewImage2 = popupView.findViewById(R.id.preview_image2);

        ImageView cancel = popupView.findViewById(R.id.cancel);
        Button uploadImage = popupView.findViewById(R.id.upload_image);
        Button deletePhoto = popupView.findViewById(R.id.deletePhoto);

            Glide.with(this)
                    .load(sharedPreferences.getString("userPicUrl", ""))
                    .placeholder(R.drawable.usericon)
                    .fitCenter()
                    .centerInside()
                    .into(previewImage2);

        selectPhoto.setOnClickListener(v -> selectImage());

        cancel.setOnClickListener(v -> {
            popupWindow.dismiss();
            filePath = null;
        });

        uploadImage.setOnClickListener(v -> uploadPhoto(bitmap));
        deletePhoto.setOnClickListener(v -> deletePicture());
    }

    private void uploadPhoto(final Bitmap bitmap)
    {
        if(filePath != null)
        {
            // show snack bar with message uploading
            showSnackBar();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                    response -> {

                        // dismiss snack bar
                         snackbar.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            Log.d("respos", obj+"");

                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                            if(status.equals("1"))
                            {
                                filePath = null; //set file path to null
                                // update pic url
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userPicUrl",obj.getString("url"));
                                editor.apply();

                                //update picture in edit profile activity
                                Glide.with(this)
                                        .load(sharedPreferences.getString("userPicUrl",obj.getString("url")))
                                        .placeholder(R.drawable.usericon)
                                        .fitCenter()
                                        .centerInside()
                                        .into(profilePic);

                                popupWindow.dismiss();
                            }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(this, "error while uploading image-1", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        Log.e("Errorx", error+"");
                        Toast.makeText(this, "error while uploading image-2", Toast.LENGTH_LONG).show();
                    }) {

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    String idKey = sharedPreferences.getString("userId", "");
                    String imageName = idKey+"-"+System.currentTimeMillis();
                    params.put("image", new DataPart(imageName + ".png", getFileDataFromDrawable(bitmap)));
                    return params;
                }
            };
            Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }
        else
        {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePicture()
    {
        String profile_url = sharedPreferences.getString("userPicUrl", "");
        if(profile_url.equals("") || profile_url.equals("null"))
        {
            Toast.makeText(this, "No picture to delete", Toast.LENGTH_SHORT).show();
            return;
        }

       final String url = "http://app.iamannitian.com/app/delete_picture.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {
                    filePath = null;
                    try
                    {
                        JSONObject object = new JSONObject(response);
                        String status =  object.getString("status");

                        Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();

                        if(status.equals("1"))
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userPicUrl","");
                            editor.apply();

                            //update picture in edit profile activity
                            Glide.with(this)
                                    .load(sharedPreferences.getString("userPicUrl", ""))
                                    .placeholder(R.drawable.usericon)
                                    .fitCenter()
                                    .centerInside()
                                    .into(profilePic);

                            popupWindow.dismiss();
                        }
                        else
                        {
                            Toast.makeText(this, "failed to delete picture", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch
                    (JSONException e)
                    {
                        Toast.makeText(this, "failed to delete image", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
            Toast.makeText(this, "failed to delete image", Toast.LENGTH_SHORT).show();
        }){
            @Override
            public Map<String, String> getParams() {
                Map<String, String> map =  new HashMap<>();
                map.put("idKey", sharedPreferences.getString("userId", ""));
                map.put("codeKey", "J6T32A-Pubs7/=H~");
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(sr);
    }

    // Select Image from gallery
    public void selectImage()
    {
        Intent img = new Intent();
        img.setType("image/*");
        img.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(img,PICK_IMAGE_REQUEST);
    }

    // Set the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!= null)
        {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            Log.i("isNull", filePath+"");
            if(filePath != null)
            {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    // hide imageview containing already set image
                    previewImage2.setVisibility(View.GONE);
                    // show imageview having image from gallery
                    previewImage.setVisibility(View.VISIBLE);
                    //set new image
                    previewImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                  e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        EditProfile.this,"No image selected",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getPath(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS);
    }


    public void showSnackBar()
    {
        snackbar = Snackbar.make(popupLayout,
                Html.fromHtml("<font color=#ffffff>Uploading...</font>"),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        String temp_college = getSharedPreferences("tempData", MODE_PRIVATE)
                .getString("tempCollege", "null");

        String college_name =  getSharedPreferences("appData", MODE_PRIVATE)
                .getString("userCollege", "null");


        if(temp_college.equals("null") && college_name.equals("null"))
        {
            college.setText("");
        }
        else if(temp_college.equals("null") && !college_name.equals("null"))
        {
            college.setText(college_name);
        }
        else if(!temp_college.equals("null") && college_name.equals("null"))
        {
            college.setText(temp_college);
        }
        else if(!temp_college.equals("null") && !college_name.equals("null"))
        {
            college.setText(temp_college);
        }
    }
}
