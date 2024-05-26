package com.mintype.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RoomPage extends AppCompatActivity {

    private static final String TAG = "RoomPage";
    private FirebaseAuth mAuth;
    private String ROOM_ID;

    private LinearLayout chatLayout;
    private EditText userMessageText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        chatLayout = findViewById(R.id.linearlayout);
        userMessageText = findViewById(R.id.editTextText);
        sendButton = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        ROOM_ID = intent.getStringExtra("ROOM_NAME");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add message to room if not null or anything.
                String usermsg = userMessageText.getText().toString().trim();
                if(!usermsg.isEmpty()) {
                    addMessageToRoom(ROOM_ID, mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName(), usermsg);
                }
            }
        });


    }
    public static void addMessageToRoom(String collectionName, String userId, String userName, String messageContent) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the specific room document
        DocumentReference roomRef = db.collection("rooms").document(collectionName);

        // Retrieve the existing Room document
        roomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, retrieve the Room object
                    Room room = document.toObject(Room.class);
                    if (room != null) {
                        // Create a new Message object
                        Message newMessage = new Message(userId, userName, messageContent, Timestamp.now());

                        // Add the new message to the list of messages
                        room.messages.add(newMessage);

                        // Save the updated Room object back to Firestore
                        roomRef.set(room)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Message successfully added to Room!"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error adding message to Room", e));
                    }
                } else {
                    Log.d("Firestore", "No such document!");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }
}