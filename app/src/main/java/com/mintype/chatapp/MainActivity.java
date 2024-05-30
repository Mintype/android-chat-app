package com.mintype.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    EditText editTextText;
//    Button button;
//    ScrollView scrollView;
//    LinearLayout linearLayout;
//    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_HOME = 1;
    private EditText emailEditText, passwordEditText, nameEditText;
    private Button signInButton, swtichButton;
    private TextView signInTitleText;
    private LinearLayout nameView;
    private boolean isSigningIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_page);

//        FirebaseAuth.AuthStateListener authStateListener = auth -> {
//            FirebaseUser firebaseUser = auth.getCurrentUser();
//            if (firebaseUser != null) {
//                //Do what you need to do
//                Intent intent = new Intent(getApplicationContext(), ChatsPage.class);
//                startActivity(intent);
//                finish();
//            }
//        };

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        nameEditText = findViewById(R.id.nameText);
        signInButton = findViewById(R.id.signinbutton);
        swtichButton = findViewById(R.id.swtichButton);
        signInTitleText = findViewById(R.id.signintitle);
        nameView = findViewById(R.id.nameView);
        
        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        isSigningIn = false;

        signInButton.setOnClickListener(v -> {
            animateSignInButton();
            Log.d("efwef", " + " + isSigningIn);
            if(!isSigningIn && emailEditText.getText().toString().trim().isEmpty() && passwordEditText.getText().toString().trim().isEmpty() && nameEditText.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Email, Password, & Name cannot be empty.", Toast.LENGTH_SHORT).show();
            else if(emailEditText.getText().toString().trim().isEmpty() && passwordEditText.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Email & Password cannot be empty.", Toast.LENGTH_SHORT).show();
            else if(emailEditText.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Email cannot be empty.", Toast.LENGTH_SHORT).show();
            else if(passwordEditText.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            else if(!isSigningIn && nameEditText.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            else {
                if (isSigningIn)
                    loginUser(emailEditText.getText().toString().trim(), passwordEditText.getText().toString());
                else
                    createUser(nameEditText.getText().toString().trim(), emailEditText.getText().toString().trim(), passwordEditText.getText().toString());
            }
        });

        swtichButton.setOnClickListener(v -> {
            animateLoginButton();
            isSigningIn = !isSigningIn;
            Log.d("wef","" + isSigningIn);
            if(isSigningIn) {
                signInTitleText.setText("Log In");
                signInButton.setText("Log In");
                swtichButton.setText("Sign Up?");

                int animationDuration = 200;
                float startScale = 1.0f;
                float endScale = 0.0f;
                Animation animation = new ScaleAnimation(startScale, endScale, startScale, endScale,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(animationDuration);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        nameView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                nameView.startAnimation(animation);

            } else {
                signInTitleText.setText("Sign Up");
                signInButton.setText("Sign Up");
                swtichButton.setText("Login?");

                int animationDuration = 200;
                float startScale = 0.0f;
                float endScale = 1.0f;
                Animation animation = new ScaleAnimation(startScale, endScale, startScale, endScale,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(animationDuration);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        nameView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                nameView.startAnimation(animation);
            }
        });
//        editTextText = findViewById(R.id.editTextText);
//        button = findViewById(R.id.button);
//        scrollView = findViewById(R.id.chat);
//        linearLayout = findViewById(R.id.linearlayout);
//
//        db = FirebaseFirestore.getInstance();
//
//        button.setOnClickListener(v -> {
//            if(!editTextText.getText().toString().trim().isEmpty()) {
//                TextView textView = new TextView(getApplicationContext());
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                );
//                layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom
//
//
//                // THIS CODE BELOW IS IMPORTANTE!!!!
//                layoutParams.gravity = Gravity.END; // Align the TextView to the right side
//
//                // Create a new user with a first and last name
//                Map<String, Object> user = new HashMap<>();
//                user.put("message", editTextText.getText().toString().trim());
//
//                // Add a new document with a generated ID
//                db.collection("users")
//                        .add(user)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w("TAG", "Error adding document", e);
//                            }
//                        });
//
//
//
//                textView.setLayoutParams(layoutParams);
//
//                textView.setBackgroundColor(Color.GRAY);
//                textView.setId(View.generateViewId());
//
//                GradientDrawable drawable = new GradientDrawable();
//                drawable.setShape(GradientDrawable.RECTANGLE);
//                drawable.setColor(Color.GRAY); // Set background color
//
//                // Set the background drawable to the TextView
//                textView.setBackground(drawable);
//
//                int paddingDp = 10;
//                float density = getApplicationContext().getResources().getDisplayMetrics().density;
//                int paddingPixel = (int)(paddingDp * density);
//                textView.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);
//
//                textView.setText(editTextText.getText().toString().trim());
//                textView.setTextColor(Color.WHITE);
//                textView.setTextSize(16f);
//
//
//
//                linearLayout.addView(textView);
//                drawable.setCornerRadius(20); // Set corner radius
//            }
//        });
    }

    private void animateSignInButton() {
        int animationDuration = 100;
        float startScale = 1.1f;
        float endScale = 1.0f;
        Animation animation = new ScaleAnimation(startScale, endScale, startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(animationDuration);
        signInButton.startAnimation(animation);
    }

    private void animateLoginButton() {
        int animationDuration = 100;
        float startScale = 1.1f;
        float endScale = 1.0f;
        Animation animation = new ScaleAnimation(startScale, endScale, startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(animationDuration);
        swtichButton.startAnimation(animation);
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Registration Successful.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), ChatsPage.class);
                    startActivity(intent);
                    finish();
                } else
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Registration Failed.", Toast.LENGTH_SHORT).show();
        });
    }
    private void createUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Registration Successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ChatsPage.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    } else
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Registration Failed.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser(String email, String password) {
        Log.d("fwef", "wef " + (mAuth == null));
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                Log.d("fwef", "wef");
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("fwef", "wef");
                    Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), ChatsPage.class);
                    startActivity(intent);
                    finish();
                } else
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Login Failed. Invalid email or password.", Toast.LENGTH_SHORT).show();
        });
        Log.d("fwef", "wwefewef");
    }
}
