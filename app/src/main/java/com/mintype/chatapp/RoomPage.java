package com.mintype.chatapp;

import static android.graphics.Color.rgb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RoomPage extends AppCompatActivity {

    private static final String TAG = "RoomPage";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String ROOM_NAME;

    private LinearLayout chatLayout;
    private ScrollView scrollView;
    private EditText userMessageText;
    private Button sendButton, backButton;
    private TextView roomTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        chatLayout = findViewById(R.id.linearlayout);
        scrollView = findViewById(R.id.chat);
        userMessageText = findViewById(R.id.editTextText);
        sendButton = findViewById(R.id.button);
        backButton = findViewById(R.id.leaveRoomButton);
        roomTitleText = findViewById(R.id.roomTitleText);

        // firebase nonsense
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //FirebaseUser currentUser = mAuth.getCurrentUser(); // not needed i think

        Intent intent = getIntent();
        ROOM_NAME = intent.getStringExtra("ROOM_NAME");

        roomTitleText.setText(ROOM_NAME);

        // back button to go back to chats page
        backButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ChatsPage.class);
            startActivity(intent1);
            finish();
        });

        sendButton.setOnClickListener(v -> {
            // add message to room if not null or anything.
            String usermsg = userMessageText.getText().toString().trim();
            if(!usermsg.isEmpty()) {
                addMessageToRoom(ROOM_NAME, mAuth.getCurrentUser().getUid().toString(), mAuth.getCurrentUser().getDisplayName(), usermsg);
                userMessageText.setText("");
            }
        });

        DocumentReference roomRef = db.collection("rooms").document(ROOM_NAME);

        roomRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Room room = documentSnapshot.toObject(Room.class);
                if (room != null) {
                    updateChatLayout(room.getMessages());
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    private void updateChatLayout(ArrayList<Message> messages) {
        chatLayout.removeAllViews();
        Log.d("wefibhfe", "" + messages.toString());
        String lastSender = null;

        /*
        *
        *   TODO:
        *       1. TURN ENCHANCED FOR LOOP INTO NORMAL FOR LOOP SO U CAN SEE THE INDEX AND PUT 10DP MARGIN BOTTOM ON LAST MESSAGE IN ARRAYLIST.
        *       2. REMOVE THE 10DP MARGIN FROM THE XML.
        *
        * */

        for(Message message : messages) {
            if(!message.getSender().equals(mAuth.getCurrentUser().getDisplayName())) {
                boolean isitcurentuserbruh = message.getSender().equals(mAuth.getCurrentUser().getDisplayName());
                //determine here if u should put a name above the textview or not.
                boolean addnamebeforehand = !isitcurentuserbruh && !message.getSender().equals(lastSender);


                if(addnamebeforehand) {
                    //testingimage();
                    addPFPbeforehand(message.getUserID());
                    addMessageName(message.getSender());
                }

                //Log.d("uhwnj", "length: " + messages.size());
                for(int i = 0; i < messages.size(); i++) {
                    //TextView textView1 = (TextView) chatLayout.getChildAt(i);
                    //Log.d("uhwnj", messages.get(i).getMessage());
                }


                TextView textView = new TextView(getApplicationContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                if(addnamebeforehand)
                    layoutParams.setMargins(20, 5, 20, 0); // left, top, right, bottom
                else
                    layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom


                // THIS CODE BELOW IS IMPORTANTE!!!!
                layoutParams.gravity = Gravity.START; // Align the TextView to the left side
                textView.setLayoutParams(layoutParams);

                textView.setBackgroundColor(Color.GRAY);
                textView.setId(View.generateViewId());

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setColor(Color.GRAY); // Set background color
                //drawable.setCornerRadius(10); // Set corner radius
                drawable.setCornerRadii(new float[] {50, 50, 50, 50, 50, 50, 0, 0}); // idk how this works

                textView.setBackground(drawable);

                int paddingDp = 10;
                float density = getApplicationContext().getResources().getDisplayMetrics().density;
                int paddingPixel = (int)(paddingDp * density);
                textView.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);

                textView.setText(message.getMessage().trim());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16f);

                chatLayout.addView(textView);
            } else {
                TextView textView = new TextView(getApplicationContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom


                // THIS CODE BELOW IS IMPORTANTE!!!!
                layoutParams.gravity = Gravity.END; // Align the TextView to the right side
                textView.setLayoutParams(layoutParams);

                textView.setBackgroundColor(Color.GRAY);
                textView.setId(View.generateViewId());

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setColor(Color.GRAY); // Set background color
                //drawable.setCornerRadius(10); // Set corner radius
                drawable.setCornerRadii(new float[] {50, 50, 50, 50, 0, 0, 50, 50}); // idk how this works

                textView.setBackground(drawable);

                int paddingDp = 10;
                float density = getApplicationContext().getResources().getDisplayMetrics().density;
                int paddingPixel = (int)(paddingDp * density);
                textView.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);

                textView.setText(message.getMessage().trim());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16f);
                chatLayout.addView(textView);
            }
            lastSender = message.getSender();
        }
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void addPFPbeforehand(String userID) {
        ImageView pfp = new ImageView(getApplicationContext());

        pfp.setId(View.generateViewId());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                60, // width
                60  // height
        );
        layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom
        layoutParams.gravity = Gravity.START; // Align the ImageView to the left side

        pfp.setLayoutParams(layoutParams);
        //pfp.setBackgroundColor(Color.BLUE);

        StorageReference pfpRef = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + userID + ".jpg");

        GlideApp.with(getApplicationContext())
                .load(pfpRef)
                .into(pfp);

        chatLayout.addView(pfp);
    }


    private void testingimage() {
        ImageView pfp = new ImageView(getApplicationContext());

        pfp.setId(View.generateViewId());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom
        layoutParams.gravity = Gravity.START; // Align the TextView to the left side

        pfp.setLayoutParams(layoutParams);
        pfp.getLayoutParams().width = 60;
        pfp.getLayoutParams().height = 60;

        pfp.setImageResource(R.drawable.ic_launcher_background);
        chatLayout.addView(pfp);
    }

    private void addMessageName(String sender) {
        Log.d("tacos", "wefwef:   " + sender);
        TextView textView = new TextView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 20, 20, 0); // left, top, right, bottom


        // THIS CODE BELOW IS IMPORTANTE!!!!
        layoutParams.gravity = Gravity.START; // Align the TextView to the left side
        textView.setLayoutParams(layoutParams);

        //textView.setBackgroundColor(Color.GRAY);
        textView.setId(View.generateViewId());

//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setShape(GradientDrawable.RECTANGLE);
//        drawable.setColor(rgb(148, 148, 148)); // Set background color
//        //drawable.setCornerRadius(10); // Set corner radius
//        drawable.setCornerRadii(new float[] {50, 50, 50, 50, 50, 50, 50, 50}); // idk how this works
//
//        textView.setBackground(drawable);

        int paddingDp = 2;
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        textView.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);

        textView.setText(sender);
        textView.setTextColor(rgb(36, 36, 36));
        textView.setTextSize(12f);
//        textView.setTypeface(null, Typeface.BOLD); // add this for BOLD text!!!!!!!
        chatLayout.addView(textView);
    }

    public void addMessageToRoom(String collectionName, String userId, String userName, String messageContent) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference roomRef = db.collection("rooms").document(collectionName);

        roomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Room room = document.toObject(Room.class);
                    if (room != null) {
                        Message newMessage = new Message(userId, userName, messageContent, Timestamp.now());

                        room.messages.add(newMessage);

                        roomRef.set(room)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Message successfully added to Room!");
                                    runOnUiThread(() -> {
                                        userMessageText.setText("");

                                        addMessageToLayout(newMessage);
                                    });
                                }).addOnFailureListener(e -> Log.w("Firestore", "Error adding message to Room", e));
                    }
                } else {
                    Log.d("Firestore", "No such document!");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    private void addMessageToLayout(Message newMessage) {
//        TextView messageView = new TextView(this);
//        messageView.setText(newMessage.getMessage());
//        chatLayout.addView(messageView);
    }
}