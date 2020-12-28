package com.example.capstone.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.capstone.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import Scanner.BarCodeScanner;

public class SurveyForm extends AppCompatActivity {
    EditText s_date3;
    TextView s_wholesaler3,s_time3,s_salesman3,s_address3,s_city3,s_state3,s_zip3;
    DatePickerDialog picker3;
    long id3=0,s_id=0;
    DatabaseReference database3,loc;
    FloatingActionButton fab;
    TableLayout tabs;
    TableRow srow;
    Toolbar toolbar5;
    Task<Location> task;
    FusedLocationProviderClient client4;
    Double loc_lat,loc_long;
    SharedPreferences sharedPreferences;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.close,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_close:
                Save();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form);
        toolbar5=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar5.findViewById(R.id.tooltext);
        tv.setText("Survey");
        setSupportActionBar(toolbar5);
        tabs=findViewById(R.id.tabs);
        fab = findViewById(R.id.fab_more);

        s_wholesaler3=findViewById(R.id.wholesaler3);
        s_time3=findViewById(R.id.time3);
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        s_time3.setText(currentTime);
        s_salesman3=findViewById(R.id.salesman3);
        s_address3=findViewById(R.id.address3);
        s_city3=findViewById(R.id.city3);
        s_state3=findViewById(R.id.state3);
        s_zip3=findViewById(R.id.zip3);
        s_date3=findViewById(R.id.date3);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
        loc=FirebaseDatabase.getInstance().getReference().child("Location");
        client4 = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(SurveyForm.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            task = client4.getLastLocation();
            findLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        database3= FirebaseDatabase.getInstance().getReference().child("Survey");
        s_date3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker3 = new DatePickerDialog(SurveyForm.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                s_date3.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker3.show();
            }
        });
    }
    private void findLocation() {
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    loc_lat=location.getLatitude();
                    loc_long=location.getLongitude();
                    //setValues(loc_lat,loc_long);
                    setValues(40.38961333333334,-74.53805833333334);
                    Log.d("Location",""+loc_lat+" "+loc_long);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1&&resultCode==RESULT_OK) {

            String p=data.getStringExtra("product");
            String m=data.getStringExtra("manufacturer");
            View view = getLayoutInflater ().inflate (R.layout.row3, null, false);
            TextView st1=view.findViewById(R.id.sur1);
            TextView st2=view.findViewById(R.id.sur2);
           // EditText st3=view.findViewById(R.id.sur3);
            st1.setText(p);
            st2.setText(m);
            tabs.addView(view);

        }
    }


    public void Scan(View view) {
        Intent i = new Intent(getApplicationContext(), BarCodeScanner.class);
        Bundle bundle = new Bundle();
        bundle.putInt("key",3);
        i.putExtras(bundle);
        startActivityForResult(i,1);
    }

    public void delete(View view) {
        int s= tabs.getChildCount();
        // Toast.makeText(this, ""+i, Toast.LENGTH_SHORT).show();
        tabs.removeViewAt(s-1);

    }


    private void Save() {

        if(s_date3.length()==0)
        {
            s_date3.setError("Please select a date!");
        }
        else if(tabs.getChildCount()<=1)
        {
            Toast.makeText(this, "Scan items into the table!", Toast.LENGTH_SHORT).show();
        }
        else{

        database3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    id3 = snapshot.getChildrenCount();
                }
                SMember s = new SMember();
                s.setAddress3(s_address3.getText().toString());
                s.setCity3(s_city3.getText().toString());
                s.setDate3(s_date3.getText().toString());
                s.setSalesman3(s_salesman3.getText().toString());
                s.setState3(s_state3.getText().toString());
                s.setTime3(s_time3.getText().toString());
                s.setWholesaler3(s_wholesaler3.getText().toString());
                s.setZip3(Integer.valueOf(s_zip3.getText().toString()));
                database3.child(String.valueOf(id3 + 1)).setValue(s);

                for(int k=1;k<tabs.getChildCount();k++)
                {
                    SSubMember sb=new SSubMember();
                    srow = (TableRow) tabs.getChildAt(k);
                    TextView tv0=(TextView)srow.getChildAt(0);
                    TextView tv1=(TextView) srow.getChildAt(1);
                    EditText et2=(EditText) srow.getChildAt(2);
                    sb.setProduct(tv0.getText().toString());
                    sb.setManufacturer(tv1.getText().toString());
                    sb.setCases_sold(Integer.valueOf(et2.getText().toString()));

                    s_id=k;
                    database3.child(String.valueOf(id3+1)).child(String.valueOf(s_id)).setValue(sb);
                }
                clearall();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }}

    private void clearall() {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        s_date3.getText().clear();
        s_time3.setText(currentTime);
        tabs.removeViews(1,tabs.getChildCount()-1);
        Toast.makeText(this, "Data Recorded Successfully", Toast.LENGTH_SHORT).show();
    }
    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,MenuScreen.class));
    }

    private void setValues(double loc_lat,double loc_long)
    {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String salesperson=sharedPreferences.getString("salesman",null);
        loc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("latitude").getValue(Double.class).equals(loc_lat)&&ds.child("longitude").getValue(Double.class).equals(loc_long))
                    {
                        s_wholesaler3.setText(String.valueOf(ds.child("ven_no").getValue(Integer.class)));
                        s_time3.setText(currentTime);
                        s_address3.setText(ds.child("address").getValue(String.class));
                        s_city3.setText(ds.child("city").getValue(String.class));
                        s_state3.setText(ds.child("state").getValue(String.class));
                        s_zip3.setText(String.valueOf(ds.child("zip").getValue(Integer.class)));
                        s_salesman3.setText(salesperson);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}