package com.example.intagram_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText name;
    EditText email;
    EditText password;
    DatabaseReference mRootRef;
    Button register;
    TextView loginUser;
    FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username  = findViewById(R.id.username);
        name  = findViewById(R.id.fullName);
        email  = findViewById(R.id.email);
        password  = findViewById(R.id.password);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        loginUser = findViewById(R.id.loginUser);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textUserName = username.getText().toString();
                String textName = name.getText().toString();
                String textEmail = email.getText().toString();
                String textPassword = password.getText().toString();
                if(TextUtils.isEmpty(textUserName)||TextUtils.isEmpty(textName) || TextUtils.isEmpty(textEmail)|| TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                }else if(textPassword.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password Too Short!!", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(textUserName , textName , textEmail, textPassword);
                }
            }
        });
    }

    public void registerUser(final String username , final String name , final String email , String password){
        pd.setMessage("Please Wait!");
        pd.show();
       mAuth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
           @Override
           public void onSuccess(AuthResult authResult) {
               HashMap<String,Object> map = new HashMap<>();
               map.put("name",name);
               map.put("email",email);
               map.put("username",username);
               map.put("id",mAuth.getCurrentUser().getUid());
               map.put("bio","");
               map.put("imageURL","default");
               mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           pd.dismiss();
                           Toast.makeText(RegisterActivity.this, "Update Your Profile in Settings for better Experience", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);
                           finish();
                       }
                   }
               });
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               pd.dismiss();
               Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });

    }
}