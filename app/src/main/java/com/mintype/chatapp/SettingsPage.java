package com.mintype.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SettingsPage extends AppCompatActivity {
    private FirebaseAuth mAuth;


    private EditText nameChangeText;
    private Button nameChangeButton, backButton, signOutButton, addPFPButton;
    private FrameLayout frameLayout;
    private Camera mCamera;
    private CameraPreview mPreview;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private ImageView imageView;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameChangeText = findViewById(R.id.nameText);
        nameChangeButton = findViewById(R.id.nameChangeButton);
        backButton = findViewById(R.id.backButton);
        signOutButton= findViewById(R.id.signOutButton);
        addPFPButton = findViewById(R.id.addPFPButton);
        imageView = findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(imageView);
        }

        // button that changes name
        nameChangeButton.setOnClickListener(v -> {
            //get name from edittext lol
            String newName = nameChangeText.getText().toString().trim();

            // make sure new name is not empty.
            if(!newName.isEmpty()) {
                // now try to do it.
                changeUserName(newName);
                nameChangeText.setText("");
            }
        });

        // code for back button
        backButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ChatsPage.class);
            startActivity(intent1);
            finish();
        });

        // code for sign out button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        addPFPButton.setOnClickListener(v -> {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, PICK_IMAGE);
//                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//                    Toast.makeText(getApplicationContext(), "No camera available", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "camera available", Toast.LENGTH_SHORT).show();
//                }
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "No camera available", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(SettingsPage.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }


            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {} else{
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTO && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap croppedPhoto = cropToSquare(photo);
                imageView.setImageBitmap(croppedPhoto);
                setFirebaseProfilePicture(croppedPhoto);
            }
        }
    }

    private void setFirebaseProfilePicture(Bitmap photo) {
        if (photo == null) {
            Log.e("settings page", "Bitmap is null.");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("settings page", "User is not logged in.");
            return;
        }

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference pfpRef = mStorageRef.child("profile_pictures/" + user.getUid() + ".jpg");

        // Upload the file
        UploadTask uploadTask = pfpRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.e("settings page", "Upload failed: " + exception.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            pfpRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(downloadUrl))
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("settings page", "User profile updated.");
                    } else {
                        Log.e("settings page", "Error updating profile: " + task.getException().getMessage());
                    }
                });
            }).addOnFailureListener(exception -> {
                Log.e("settings page", "Failed to get download URL: " + exception.getMessage());
            });
        });
    }
    private Bitmap cropToSquare(Bitmap srcBmp) {
        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();
        int newWidth = Math.min(width, height);
        int newHeight = Math.min(width, height);

        int cropW = (width - newWidth) / 2;
        int cropH = (height - newHeight) / 2;

        return Bitmap.createBitmap(srcBmp, cropW, cropH, newWidth, newHeight);
    }


    public void changeUserName(String newName) {
        // try to change name in firebase
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        FirebaseUser user = mAuth.getCurrentUser();

        user.updateProfile(profileUpdates)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("tacobellzzes", "User profile updated.");
                } else {
                    Log.d("tacobellzzes", "ewfusjoi");
                }
        });
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    /** A basic Camera preview class */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("TAG", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("TAG", "Error starting camera preview: " + e.getMessage());
            }
        }
    }

}
