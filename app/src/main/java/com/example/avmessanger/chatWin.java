package com.example.avmessanger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {
    String reciverimg, reciveruid, reciverName, SenderUID;
    CircleImageView profile;
    TextView recivername;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public  static  String senderImg;
    public  static  String reciverIImg;
    String senderRoom,reciverRoom;
    RecyclerView mmessageAdapter;
    ArrayList<msgmodel> messagessArraylist;
    messagesAdapter messagesAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_win);

        reciverName = getIntent().getStringExtra("nameee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciveruid = getIntent().getStringExtra("uid");
        profile = findViewById(R.id.profilechat);
        recivername = findViewById(R.id.recivername);
        messagessArraylist = new ArrayList<>();
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        mmessageAdapter = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessageAdapter.setLayoutManager(linearLayoutManager);
        messagesAdapter = new messagesAdapter(chatWin.this,messagessArraylist);
        mmessageAdapter.setAdapter(messagesAdapter);

        Picasso.get().load(reciverimg).into(profile);
        recivername.setText(""+reciverName);

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference = database.getReference().child("user").child(senderRoom).child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArraylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgmodel messages = dataSnapshot.getValue(msgmodel.class);
                    messagessArraylist.add(messages);

                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue().toString();
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SenderUID = firebaseAuth.getUid();
        senderRoom = SenderUID+reciveruid;
        reciverRoom = reciveruid+SenderUID;

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String message = textmsg.getText().toString();
             if (message.isEmpty()){
                 Toast.makeText(chatWin.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
             }
             textmsg.setText("");
             Date date = new Date();
             msgmodel messagess = new msgmodel(message,SenderUID,date.getTime());
             database = FirebaseDatabase.getInstance();
             database.getReference().child("chats").child("senderRoom").child("messages").push()
                     .setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             database.getReference().child("chats").child("reciverRoom").child("messages").push()
                                     .setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {

                                         }
                                     });
                         }
                     });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}