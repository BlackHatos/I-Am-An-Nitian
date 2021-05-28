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
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import net.gotev.uploadservice.data.UploadNotificationConfig;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity
{

    private static final int IMAGE_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private EditText name, email, phone, college, degree, state, branch, start, end;
    private ImageView profilePic, editImage;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private  Uri picturePath;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setUpToolbarMenu();
        initVariables();
        setData();
        requestStoragePermission();

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
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
                        Toast.makeText(this, "failed to save changes-1", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                     Toast.makeText(this, "failed to save changes-2", Toast.LENGTH_SHORT).show();
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


        name.setText(user_name);
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
        final PopupWindow popupWindow = new PopupWindow(popupView,width,height,focusable);
        popupWindow.setAnimationStyle(R.style.windowAnimationTransition);
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        Button selectPhoto = popupView.findViewById(R.id.select_image);
        previewImage = popupView.findViewById(R.id.preview_image);
        ImageView cancel = popupView.findViewById(R.id.cancel);
        Button uploadImage = popupView.findViewById(R.id.upload_image);
        Button deletePhoto = popupView.findViewById(R.id.deletePhoto);

        selectPhoto.setOnClickListener(v -> selectImage());
        cancel.setOnClickListener(v -> popupWindow.dismiss());
        uploadImage.setOnClickListener(v -> uploadPhoto());
        deletePhoto.setOnClickListener(v -> deletePicture());
    }

    private void uploadPhoto()
    {
        String path = getPath(picturePath);
        final String my_url = "http://app.iamannitian.com/app/upload_picture.php";
        try {
           //String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this, my_url).addFileToUpload(path, "uploadFile")
                    //.setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();

        } catch (Exception ex) {
            Log.d("errorx", ex+"");

        }
    }

    private void deletePicture()
    {
        // delete profile picture
    }

    // Select Image from gallery
    public void selectImage()
    {
        Intent img = new Intent();
        img.setType("image/*");
        img.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(img,IMAGE_REQUEST_CODE);
    }

    // Set the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            picturePath  = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picturePath);
                previewImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                4655);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4655) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

}
