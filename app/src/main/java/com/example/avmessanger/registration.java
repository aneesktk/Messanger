package com.example.avmessanger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 10;

    Button button;
    EditText name, email, password, re_enterpassword;
    TextView loginbut;
    CircleImageView profile_image;
    private FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_registration);

        // Initialize FirebaseAuth here
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating...");
        progressDialog.setCancelable(false);
        button = findViewById(R.id.button2);
        email = findViewById(R.id.editTextTextEmailAddress2);
        name = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextNumberPassword2);
        re_enterpassword = findViewById(R.id.editTextNumberPassword3);
        profile_image = findViewById(R.id.profile_image);
        loginbut = findViewById(R.id.loginbut);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        loginbut.setOnClickListener(v -> {
            startActivity(new Intent(registration.this, MainActivity2.class));
            finish();
        });

        profile_image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
        });

        button.setOnClickListener(v -> {
            String namee = name.getText().toString();
            String emaill = email.getText().toString();
            String Password = password.getText().toString();
            String cPassword = re_enterpassword.getText().toString();
            String status = "Hey I am Using This Application";

            if (TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) ||
                    TextUtils.isEmpty(Password) || TextUtils.isEmpty(cPassword)) {
                progressDialog.dismiss();
                Toast.makeText(this, "Please Enter a Valid Information", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!emaill.matches(emailPattern)) {
                progressDialog.dismiss();
                email.setError("Give proper Email Address");
                return;
            }
            if (Password.length() < 6) {
                progressDialog.dismiss();
                password.setError("More Than Six Characters");
                return;
            }
            if (!Password.equals(cPassword)) {
                progressDialog.dismiss();
                re_enterpassword.setError("Password Doesn't Match");
                return;
            }

            // auth is now guaranteed to be initialized
            auth.createUserWithEmailAndPassword(emaill, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);

                                if (imageURI != null) {
                                    storageReference.putFile(imageURI)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task2) {
                                                    if (task2.isSuccessful()) {
                                                        storageReference.getDownloadUrl()
                                                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Uri> urlTask) {
                                                                        if (urlTask.isSuccessful()) {
                                                                            Uri uri = urlTask.getResult();
                                                                            imageuri = uri.toString();
                                                                            Users users = new Users(id, namee, emaill, Password, imageuri, status);
                                                                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> refTask) {
                                                                                    if (refTask.isSuccessful()) {
                                                                                        startActivity(new Intent(registration.this, Home.class));
                                                                                        finish();
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(registration.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(registration.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    imageuri = ""; // or some default placeholder
                                    Users users = new Users(id, namee, emaill, Password, imageuri, status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> refTask) {
                                            if (refTask.isSuccessful()) {
                                                progressDialog.show();
                                                startActivity(new Intent(registration.this, Home.class));
                                                finish();
                                            } else {
                                                Toast.makeText(registration.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v1, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v1.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageURI = data.getData();
            profile_image.setImageURI(imageURI);
        }
    }
}
