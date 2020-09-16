package com.example.dronetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText mServer;
    private EditText mUsername;
    private EditText mPassword;

    private Button mButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sign_up);


        mServer = findViewById(R.id.edit_server);
        mServer.setText("ws://10.0.2.2:9003/test-ws");
        mServer.setEnabled(false);
        mUsername = findViewById(R.id.edit_username);
        mPassword = findViewById(R.id.edit_password);

        mButton = findViewById(R.id.button_login);

        mAuth = FirebaseAuth.getInstance();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login(){
        final String server = mServer.getText().toString().trim();
        //Log.i("serverstring", server);
        String email = mUsername.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        Log.i("email", email);
        Log.i("password", password);

        Log.i("clickedlogin","clicklogin");


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //mAuth = FirebaseAuth.getInstance();
                        //FirebaseUser user = mAuth.getCurrentUser();
                        //Log.i("user", user.getEmail());
                        //mAuth.updateCurrentUser(mAuth.getCurrentUser());
                        //MainActivity.updateUi(user);

                        //Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        //Log.i("taskwascomplete", "taskwascomplete");

                        //startActivity(intent);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.i("serverstring", server);
                            //Bundle bundle = new Bundle();
                            //bundle.putString("server",server);
                            //intent.putExtras(bundle);
                            //extras.putString("server", server);
                            //intent.putExtra("server", server);
                            Log.i("taskwascomplete", "taskwascomplete");
                            startActivity(intent);
                        }

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,"Incorrect username or password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}