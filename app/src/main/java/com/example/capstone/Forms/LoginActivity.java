package com.example.capstone.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import Maps.CurrentLocation;
import Maps.DisplayLog;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    SharedPreferences sharedPreferences;
    DatabaseReference db_login;
    private Drawable error_indicator;
    boolean flag=false;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button2);

        error_indicator = getResources().getDrawable(R.drawable.ic_icons8_error_40);
        int right = error_indicator.getIntrinsicHeight();
        int bottom = error_indicator.getIntrinsicWidth();
        error_indicator.setBounds(new Rect(0, 0, right, bottom));
        username.addTextChangedListener(new LoginActivity.InputValidator(username));
        password.addTextChangedListener(new LoginActivity.InputValidator(password));

        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
        db_login = FirebaseDatabase.getInstance().getReference().child("Users");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.length() == 0) {
                    username.setError("Please enter a username!");
                } else if (password.length() == 0) {
                    password.setError("Please enter a password!");
                } else {
                    db_login.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.child("username").getValue(String.class).equals(username.getText().toString()) && ds.child("password").getValue(String.class).equals(password.getText().toString())) {

                                    editor.putString("user", username.getText().toString());
                                    editor.putString("salesman",ds.child("salesman").getValue(String.class));
                                    editor.apply();
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                Toast.makeText(LoginActivity.this, "Login Successful!!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, "Welcome " +sharedPreferences.getString("salesman",null), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MenuScreen.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Credentials!!", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }


    private class InputValidator implements TextWatcher {
        private EditText et;

        private InputValidator(EditText editText) {
            this.et = editText;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (s.length() != 0) {
                switch (et.getId()) {
                    case R.id.username: {
                        if (!Pattern.matches("^[a-zA-Z0-9._@]*$", s)) {
                            et.setError("Enter a valid username!");
                        }
                    }
                    break;

                    case R.id.password: {
                        if (!Pattern.matches("^[a-zA-Z0-9@_]*$", s)) {
                            et.setError("Enter a valid password!");
                        }
                    }
                    break;
                }
            }
        }
    }
}

