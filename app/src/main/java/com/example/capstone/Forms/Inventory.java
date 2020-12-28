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

public class Inventory extends AppCompatActivity {
    EditText s_date2;
    TextView s_wholesaler2,s_time2,s_salesman2,s_address2,s_city2,s_state2,s_zip2;
    DatePickerDialog picker2;
    long id2 = 0, inv_id = 0;
    DatabaseReference database2, loc;
    FloatingActionButton fab;
    TableRow row;
    TableLayout tabl;
    Task<Location> task;
    Toolbar toolbar4;
    FusedLocationProviderClient client3;
    Double loc_lat,loc_long;
    SharedPreferences sharedPreferences;


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
        setContentView(R.layout.activity_inventory);
        toolbar4 = findViewById(R.id.MyToolbar4);
        setSupportActionBar(toolbar4);
        tabl=findViewById(R.id.tabl);
        fab = findViewById(R.id.fab_more);
        s_wholesaler2=findViewById(R.id.wholesaler2);
        s_time2=findViewById(R.id.time);
        s_salesman2=findViewById(R.id.salesman);
        s_address2=findViewById(R.id.address_2);
        s_city2=findViewById(R.id.city_2);
        s_state2=findViewById(R.id.state_2);
        s_zip2=findViewById(R.id.zip_2);
        s_date2=findViewById(R.id.dov);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
        loc=FirebaseDatabase.getInstance().getReference().child("Location");
        database2= FirebaseDatabase.getInstance().getReference().child("Inventory");
        client3 = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(Inventory.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            task = client3.getLastLocation();
            findLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


        s_date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker2 = new DatePickerDialog(Inventory.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                s_date2.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker2.show();
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

                    //Toast.makeText(Inventory.this, ""+loc_lat+""+loc_long, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findLocation();
            }
        }
    }

    public void delete(View view) {
        int d= tabl.getChildCount();
        tabl.removeViewAt(d-1);

    }

    private void Save() {

        if(s_date2.length()==0)
        {
            s_date2.setError("Please select a date!");
        }
        else if(tabl.getChildCount()<=1)
        {
            Toast.makeText(this, "Please scan the items!", Toast.LENGTH_SHORT).show();
        }
        else {
            database2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        id2 = snapshot.getChildrenCount();
                    }
                    IMember i = new IMember();
                    i.setAddress2(s_address2.getText().toString());
                    i.setCity2(s_city2.getText().toString());
                    i.setDate2(s_date2.getText().toString());
                    i.setSalesman2(s_salesman2.getText().toString());
                    i.setState2(s_state2.getText().toString());
                    i.setTime2(s_time2.getText().toString());
                    i.setWholesaler2(s_wholesaler2.getText().toString());
                    i.setZip2(Integer.valueOf(s_zip2.getText().toString()));
                    database2.child(String.valueOf(id2 + 1)).setValue(i);

                    for (int k = 1; k < tabl.getChildCount(); k++) {
                        ISubMember ib = new ISubMember();
                        row = (TableRow) tabl.getChildAt(k);
                        TextView tv0 = (TextView) row.getChildAt(0);
                        TextView et1 = (TextView) row.getChildAt(1);
                        EditText tv2 = (EditText) row.getChildAt(2);
                        ib.setProduct(tv0.getText().toString());
                        ib.setCases(Integer.valueOf(et1.getText().toString()));
                        ib.setBottles(Integer.valueOf(tv2.getText().toString()));
                        inv_id = k;
                        database2.child(String.valueOf(id2 + 1)).child(String.valueOf(inv_id)).setValue(ib);
                    }

                    clearall();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void Scan(View view) {
        Intent i = new Intent(getApplicationContext(), BarCodeScanner.class);
        Bundle bundle = new Bundle();
        bundle.putInt("key",2);
        i.putExtras(bundle);
        startActivityForResult(i,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean flag=false;

        if (requestCode ==1&&resultCode==RESULT_OK) {
            String p=data.getStringExtra("product");
            int b=data.getIntExtra("bottles",0);

            View view = getLayoutInflater ().inflate (R.layout.row2, null, false);
            TextView it1=view.findViewById(R.id.inv1);
            TextView it2=view.findViewById(R.id.inv2);
            EditText it3=view.findViewById(R.id.inv3);

            for(int i=1;i<tabl.getChildCount();i++)
            {
                try {
                    TableRow row1= (TableRow)tabl.getChildAt(i);
                    TextView getProd = (TextView) row1.getChildAt(0);
                    if(getProd.getText().toString().equals(p))
                    {
                        TextView et2=(TextView) row1.getChildAt(1);
                        EditText et3=(EditText)row1.getChildAt(2);
                        //Log.d("Vlaues",""+et2.getText().toString()+" "+et3.getText().toString());
                        et2.setText(String.valueOf(Integer.parseInt(et2.getText().toString())+1));
                        et3.setText(String.valueOf(Integer.parseInt(et3.getText().toString())+b));
                        flag=true;
                        break;
                    }
            } catch (NumberFormatException e) {
                    Log.d("Error",""+e);
                }}
            if(!flag)
            {
                it1.setText(p);
                it2.setText("1");
                it3.setText(String.valueOf(b));
                tabl.addView(view);

            }


        }
    }

    private void clearall() {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        s_date2.getText().clear();
        s_time2.setText(currentTime);
        tabl.removeViews(1,tabl.getChildCount()-1);
        Toast.makeText(this, "Items Added Successfully", Toast.LENGTH_SHORT).show();
    }


    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,MenuScreen.class));
        finish();
    }

    private void setValues(double loc_lat,double loc_long)
    {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        //Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        //Log.d("values",""+loc_lat+" "+loc_long);
        String salesperson=sharedPreferences.getString("salesman",null);
        loc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("latitude").getValue(Double.class).equals(loc_lat)&&ds.child("longitude").getValue(Double.class).equals(loc_long))
                    {
                        s_wholesaler2.setText(String.valueOf(ds.child("ven_no").getValue(Integer.class)));
                        s_time2.setText(currentTime);
                        s_address2.setText(ds.child("address").getValue(String.class));
                        s_city2.setText(ds.child("city").getValue(String.class));
                        s_state2.setText(ds.child("state").getValue(String.class));
                        s_zip2.setText(String.valueOf(ds.child("zip").getValue(Integer.class)));
                        s_salesman2.setText(salesperson);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}