package com.example.capstone.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;


public class AddCustomers extends AppCompatActivity {
    EditText first_name, last_name, cust_address, cust_city, cust_state, cust_zip, email, phone, company;
    DatabaseReference contact;
    long cid;
    private Drawable error_indicator;
    Toolbar toolbar2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_close:
                Save();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);
        toolbar2=findViewById(R.id.MyToolbar4);
        TextView textv=(TextView)toolbar2.findViewById(R.id.tooltext);
        textv.setText("New Contact");
        setSupportActionBar(toolbar2);


        first_name = findViewById(R.id.firstName);
        last_name = findViewById(R.id.lastName);
        company = findViewById(R.id.company);
        cust_address = findViewById(R.id.addcust);
        cust_city = findViewById(R.id.citycust);
        cust_state = findViewById(R.id.statecust);
        cust_zip = findViewById(R.id.zipcust);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        error_indicator = getResources().getDrawable(R.drawable.ic_icons8_error_40);
        int right = error_indicator.getIntrinsicHeight();
        int bottom = error_indicator.getIntrinsicWidth();
        error_indicator.setBounds(new Rect(0, 0, right, bottom));
        first_name.addTextChangedListener(new InputValidator(first_name));
        last_name.addTextChangedListener(new InputValidator(last_name));
        company.addTextChangedListener(new InputValidator(company));
        cust_address.addTextChangedListener(new InputValidator(cust_address));
        cust_city.addTextChangedListener(new InputValidator(cust_city));
        cust_state.addTextChangedListener(new InputValidator(cust_state));
        cust_zip.addTextChangedListener(new InputValidator(cust_zip));
        email.addTextChangedListener(new InputValidator(email));
        phone.addTextChangedListener(new InputValidator(phone));

        contact = FirebaseDatabase.getInstance().getReference().child("Contacts");

    }

    private void clearall() {
        first_name.getText().clear();
        last_name.getText().clear();
        company.getText().clear();
        cust_address.getText().clear();
        cust_city.getText().clear();
        cust_state.getText().clear();
        cust_zip.getText().clear();
        email.getText().clear();
        phone.getText().clear();

        Toast.makeText(this, "Contact Successfully Added!!", Toast.LENGTH_SHORT).show();
    }

    public void back_to_menu(View view) {
        startActivity(new Intent(this, ContactList.class));
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
                    case R.id.firstName: {
                        if (!Pattern.matches("^[a-zA-Z]*$", s)) {
                            et.setError("First name must have only a-z");
                        }
                    }
                    break;

                    case R.id.lastName: {
                        if (!Pattern.matches("^[a-zA-Z]*$", s)) {
                            et.setError("Last name must have only a-z");
                        }
                    }
                    break;
                    case R.id.citycust: {
                        if (!Pattern.matches("^[a-zA-Z ]*$", s)) {
                            et.setError("City must have only a-z");
                        }
                    }
                    break;
                    case R.id.statecust: {
                        if (!Pattern.matches("^[a-zA-Z]*$", s)) {
                            et.setError("State must have only a-z");
                        }
                    }
                    break;
                    case R.id.zipcust: {
                        if (!Pattern.matches("^[0-9]*$", s)) {
                            et.setError("Enter a valid zip code");
                        }
                    }
                    break;
                    case R.id.phone: {
                        if (!Pattern.matches("^[2-9]{2}[0-9]{8}$", s)) {
                            et.setError("Phone number should have ten digits only!");
                        }
                    }
                    break;
                    case R.id.email: {
                        if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", s)) {
                            et.setError("Enter a valid email address");
                        }
                    }

                }
            }
        }
    }

    private void Save() {
        if (first_name.length() == 0) {
            first_name.setError("First name cannot be empty!");
        } else if (last_name.length() == 0) {
            last_name.setError("Last name cannot be empty!");
        } else if (company.length() == 0) {
            company.setError("Please enter a company name!");
        } else if (email.length() == 0) {
            email.setError("Please enter an email address!");
        } else if (cust_address.length() == 0) {
            cust_address.setError("Please enter an address!");
        } else if (cust_city.length() == 0) {
            cust_city.setError("Please enter a city!");
        } else if (cust_state.length() == 0) {
            cust_state.setError("Please enter a state!");
        } else if (cust_zip.length() == 0) {
            cust_zip.setError("Please enter a zip code!");
        } else if (phone.length() == 0) {
            phone.setError("Please enter a phone number!");
        } else {
            contact.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cid = snapshot.getChildrenCount();
                    Contact c = new Contact();
                    c.setCaddress(cust_address.getText().toString());
                    c.setCcity(cust_city.getText().toString());
                    c.setCompany(company.getText().toString());
                    c.setCstate(cust_state.getText().toString());
                    c.setCzip(Integer.valueOf(cust_zip.getText().toString()));
                    c.setEmail(email.getText().toString());
                    c.setFname(first_name.getText().toString());
                    c.setLname(last_name.getText().toString());
                    c.setPhone(phone.getText().toString());

                    contact.child(String.valueOf(cid + 1)).setValue(c);

                    clearall();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    }





