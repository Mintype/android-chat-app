package com.mintype.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsPage extends AppCompatActivity {
    private FirebaseAuth mAuth;


    private EditText nameChangeText;
    private Button nameChangeButton, backButton, signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameChangeText = findViewById(R.id.nameText);
        nameChangeButton = findViewById(R.id.nameChangeButton);
        backButton= findViewById(R.id.backButton);
        signOutButton= findViewById(R.id.signOutButton);

        mAuth = FirebaseAuth.getInstance();

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

//        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
//            Log.d("5hh6", "iqhwefoi");
//            if(task.isSuccessful())
//                Toast.makeText(getApplicationContext(),"Name update successful.",Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(getApplicationContext(),"Name update Failed, try again",Toast.LENGTH_SHORT).show();
//        });
    }
}