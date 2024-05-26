package com.mintype.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.graphics.drawable.GradientDrawable;

public class ChatsPage extends AppCompatActivity {

    private static final String TAG = "ChatsPage";
    private FirebaseAuth mAuth;
    private TextView helloText;
    private Button createRoomButton;
    private EditText roomNameText;
    private String displayName;
    private FirebaseFirestore db;
    private LinearLayout roomsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_page);

        helloText = findViewById(R.id.HelloText);
        createRoomButton = findViewById(R.id.createRoomButton);
        roomNameText = findViewById(R.id.roomNameText);
        roomsLayout = findViewById(R.id.collections);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "No user is authenticated.");
            // send user back to login page.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "User is authenticated: " + currentUser.getEmail());

            displayName = currentUser.getDisplayName();
            helloText.setText("Hello " + displayName + "!");

            // add colections to the linearlayout
//            db.collection("rooms")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d("TAG", document.getId() + " => " + document.getData());
//                                    TextView t = new TextView(getApplicationContext());
//                                    t.setText(document.getId().toString().trim());
//                                    roomsLayout.addView(t);
//                                }
//                            } else {
//                                Log.d("TAG", "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });

            db.collection("rooms").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    roomsLayout.removeAllViews();

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL; // Align the TextView to the right side
                    for (QueryDocumentSnapshot doc : value) {
                        TextView t = new TextView(getApplicationContext());
                        t.setLayoutParams(layoutParams);
                        t.setTextSize(16f);
                        t.setBackgroundColor(Color.GRAY);
                        t.setId(View.generateViewId());
                        t.setTextColor(Color.WHITE);

                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setShape(GradientDrawable.RECTANGLE);
                        drawable.setColor(Color.GRAY); // Set background color

                        // Set the background drawable to the textview
                        t.setBackground(drawable);

                        int paddingDp = 10;
                        float density = getApplicationContext().getResources().getDisplayMetrics().density;
                        int paddingPixel = (int)(paddingDp * density);
                        t.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);

                        t.setText(doc.getId().trim());

                        // set the onclick!
                        t.setOnClickListener(v -> {
                            Intent intent = new Intent(getApplicationContext(), RoomPage.class);
                            intent.putExtra("ROOM_NAME", doc.getId().trim());
                            startActivity(intent);
                        });

                        roomsLayout.addView(t);
                    }

                }
            });

            createRoomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newRoomName = roomNameText.getText().toString().trim();
                    if(!newRoomName.isEmpty()) {
                        createCollection(newRoomName);
                    }
                }
            });
        }
    }
    public void createCollection(final String collectionName) {
        // Get reference to the collection
        final CollectionReference collection = db.collection("rooms");

        // Check if the collection already exists
        collection.document(collectionName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Firestore", "Collection already exists: " + collectionName);
                        } else {
                            Log.d("Firestore", "Creating collection: " + collectionName);
                            //collection.document("welcome-msg").set(new ChatMessage(collectionName));
                            CollectionReference rooms = db.collection("rooms");
                            rooms.document(collectionName).set(new Room(collectionName, mAuth.getCurrentUser().getDisplayName().toString(), mAuth.getCurrentUser().getUid().toString()));
                            addMessageToRoom(collectionName, mAuth.getCurrentUser().getDisplayName().toString(), "this is a message!");
                        }
                    } else {
                        Log.e("Firestore", "Error checking collection existence", task.getException());
                    }
                });
    }
    public static void addMessageToRoom(String collectionName, String userName, String messageContent) {
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
                        Message newMessage = new Message(userName, messageContent, Timestamp.now());

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