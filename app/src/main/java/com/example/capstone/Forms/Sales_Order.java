package com.example.capstone.Forms;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import Scanner.BarCodeScanner;

public class Sales_Order extends AppCompatActivity {
    EditText s_cust,s_ship, s_date,s_city,s_state,s_zip,s_address,ship_address,ship_city,ship_zip,ship_state;
    DatePickerDialog picker;
    Spinner s_type;
    long id = 0,id1=0,sub_id=0;
    FloatingActionButton fab;
    DatabaseReference database,database1,database2;
    TableLayout tl;
    TableRow tr;
    Task<Location> task;
    Double loc_lat,loc_long;
    FusedLocationProviderClient client4;
    Toolbar toolbar3;
    private Drawable error_indicator;
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
        setContentView(R.layout.activity_sales__order);
        toolbar3=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar3.findViewById(R.id.tooltext);
        tv.setText("Sales Order");
        setSupportActionBar(toolbar3);
        fab = findViewById(R.id.fab_more);
        s_cust = findViewById(R.id.customer);
        s_city = findViewById(R.id.city);
        s_state = findViewById(R.id.state);
        s_zip = findViewById(R.id.zip);
        s_ship = findViewById(R.id.ship);
        s_address = findViewById(R.id.address);
        s_date = findViewById(R.id.date);
        s_type = findViewById(R.id.type);
        ship_address=findViewById(R.id.address1);
        ship_city=findViewById(R.id.city1);
        ship_state=findViewById(R.id.state1);
        ship_zip=findViewById(R.id.zip1);
        tl = findViewById(R.id.tableLayout);

        client4 = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(Sales_Order.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            task = client4.getLastLocation();
            findLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        error_indicator = getResources().getDrawable(R.drawable.ic_icons8_error_40);
        int right = error_indicator.getIntrinsicHeight();
        int bottom = error_indicator.getIntrinsicWidth();
        error_indicator.setBounds(new Rect(0,0, right, bottom));
        s_cust.addTextChangedListener(new Sales_Order.InputValidator(s_cust));
        s_ship.addTextChangedListener(new Sales_Order.InputValidator(s_ship));

        database = FirebaseDatabase.getInstance().getReference().child("Sales_Order");
        database1=FirebaseDatabase.getInstance().getReference().child("Delivery");
        database2=FirebaseDatabase.getInstance().getReference().child("Location");

        List<String> categories = new ArrayList<String>();
        categories.add("OR");
        categories.add("RE");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_type.setAdapter(dataAdapter);

      s_date.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(Sales_Order.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        s_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }
    });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode ==1&&resultCode==RESULT_OK) {

                String p=data.getStringExtra("product");
                String d=data.getStringExtra("desc");
                String pi=data.getStringExtra("price");
                Toast.makeText(Sales_Order.this,"Product: "+ p+"\n"+"Price: "+pi, Toast.LENGTH_SHORT).show();

                View view = getLayoutInflater ().inflate (R.layout.row, null, false);
                TextView et1=view.findViewById(R.id.editText1);
                TextView et2=view.findViewById(R.id.editText2);
                TextView et3=view.findViewById(R.id.editText3);
                EditText et4=view.findViewById(R.id.editText4);
                TextView et5=view.findViewById(R.id.editText5);
                et1.setText(String.valueOf(tl.getChildCount()));
                et2.setText(p);
                et3.setText(d);
                et5.setText(pi);
                tl.addView(view);


        }
    }
    private void findLocation() {
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    loc_lat=location.getLatitude();
                    loc_long=location.getLongitude();
                    setValues(40.38961333333334,-74.53805833333334);
                   // setValues(loc_lat,loc_long);
                    Log.d("Location",""+loc_lat+" "+loc_long);

                }
            }
        });
    }

    public void Scan(View view) {

        Intent i = new Intent(getApplicationContext(), BarCodeScanner.class);
        Bundle bundle = new Bundle();
        bundle.putInt("key",1);
        i.putExtras(bundle);
        startActivityForResult(i,1);
    }

    private void Save() {
        if (s_cust.length() == 0) {
            s_cust.setError("Please fill in the customer name!!");
        }else if (s_ship.length() == 0) {
            s_ship.setError("Please fill in the shipment name!!");
        } else if (s_date.length() == 0) {
            s_date.setError("Please select a date!!");
        }else if(s_address.length()==0||s_city.length()==0||s_zip.length()==0||s_state.length()==0)
        {
            Toast.makeText(this, "Please enter a customer Id!", Toast.LENGTH_SHORT).show();
        }else if(ship_address.length()==0||ship_city.length()==0||ship_zip.length()==0||ship_state.length()==0)
        {
            Toast.makeText(this, "Please enter a shipment Id!", Toast.LENGTH_SHORT).show();
        }
        else if(tl.getChildCount()<=1)
        {
            Toast.makeText(this, "Scane items to place order!", Toast.LENGTH_SHORT).show();
        }
                else {
            database1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    id1 = snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        id = snapshot.getChildrenCount();
                    }
                    Member m = new Member();
                    m.setAddress(s_address.getText().toString());
                    m.setCity(s_city.getText().toString());
                    m.setCustomer(s_cust.getText().toString());
                    m.setDate(s_date.getText().toString());
                    m.setShip(s_ship.getText().toString());
                    m.setState(s_state.getText().toString());
                    m.setType(s_type.getSelectedItem().toString());
                    m.setZip(Integer.valueOf(s_zip.getText().toString()));
                    m.setShip_address(ship_address.getText().toString());
                    m.setShip_city(ship_city.getText().toString());
                    m.setShip_state(ship_state.getText().toString());
                    m.setShip_zip(Integer.valueOf(ship_zip.getText().toString()));
                    database.child(String.valueOf(id + 1)).setValue(m);
                    database1.child(String.valueOf(id1 + 1)).child("order").setValue(m.getDate());

                    for (int i = 1; i < tl.getChildCount(); i++) {
                        SubMember sb = new SubMember();
                        tr = (TableRow) tl.getChildAt(i);
                        TextView tv0 = (TextView) tr.getChildAt(0);
                        TextView tv1 = (TextView) tr.getChildAt(1);
                        TextView tv2 = (TextView) tr.getChildAt(2);
                        TextView tv4 = (TextView) tr.getChildAt(4);
                        EditText editText = (EditText) tr.getChildAt(3);
                        sb.setS_no(tv0.getText().toString());
                        sb.setItem(tv1.getText().toString());
                        sb.setDesc(tv2.getText().toString());
                        sb.setQty(Integer.valueOf(editText.getText().toString()));
                        sb.setPrice(Integer.valueOf(tv4.getText().toString()));
                        sub_id = i;
                        database.child(String.valueOf(id + 1)).child(String.valueOf(sub_id)).setValue(sb);
                    }

                    clearall();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    public void delete(View view) {
        int i= tl.getChildCount();
       // Toast.makeText(this, ""+i, Toast.LENGTH_SHORT).show();
        tl.removeViewAt(i-1);
    }

    private void clearall() {
        s_cust.getText().clear();
        s_city.getText().clear();
        s_state.getText().clear();
        s_address.getText().clear();
        s_date.getText().clear();
        s_ship.getText().clear();
        s_zip.getText().clear();
        ship_state.getText().clear();
        ship_city.getText().clear();
        ship_address.getText().clear();
        ship_zip.getText().clear();
        tl.removeViews(1,tl.getChildCount()-1);
        Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
    }

    public void Add(View view) {

        database2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag=false;
                for(DataSnapshot ds:snapshot.getChildren())
                {

                    if(ds.child("ven_no").getValue(Integer.class).equals(Integer.parseInt(s_ship.getText().toString())))
                    {
                        ship_address.setText(ds.child("address").getValue(String.class));
                        ship_city.setText(ds.child("city").getValue(String.class));
                        ship_state.setText(ds.child("state").getValue(String.class));
                        ship_zip.setText(String.valueOf(ds.child("zip").getValue(Integer.class)));
                        flag=true;
                        break;
                    }

                }
                if(!flag)
                {
                    Toast.makeText(Sales_Order.this, "Enter a valid ID!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void Add1(View view) {
        database2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag=false;
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("ven_no").getValue(Integer.class).equals(Integer.parseInt(s_cust.getText().toString())))
                    {
                        s_address.setText(ds.child("address").getValue(String.class));
                        s_city.setText(ds.child("city").getValue(String.class));
                        s_state.setText(ds.child("state").getValue(String.class));
                        s_zip.setText(String.valueOf(ds.child("zip").getValue(Integer.class)));
                        flag=true;
                        break;

                    }
                }
                if(!flag)
                {
                    Toast.makeText(Sales_Order.this, "Enter a valid ID!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    case R.id.customer: {
                        if (!Pattern.matches("^[0-9]*$", s)) {
                            et.setError("Customer ID must be numeric");
                        }
                    }
                    break;

                    case R.id.ship: {
                        if (!Pattern.matches("^[0-9]*$", s)) {
                            et.setError("Vendor ID must be numeric!");
                        }
                    }
                    break;
                }
            }
        }
    }

    private void setValues(double lati,double longi)
    {
        database2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("latitude").getValue(Double.class).equals(lati)&&ds.child("longitude").getValue(Double.class).equals(longi))
                    {
                        s_cust.setText(String.valueOf(ds.child("ven_no").getValue(Integer.class)));
                        s_ship.setText(String.valueOf(ds.child("ven_no").getValue(Integer.class)));
                        s_address.setText(ds.child("address").getValue(String.class));
                        ship_address.setText(ds.child("address").getValue(String.class));
                        s_zip.setText(String.valueOf(ds.child("zip").getValue(Integer.class)));
                        ship_zip.setText(String.valueOf(ds.child("zip").getValue(Integer.class)));
                        s_state.setText(ds.child("state").getValue(String.class));
                        ship_state.setText(ds.child("state").getValue(String.class));
                        s_city.setText(ds.child("city").getValue(String.class));
                        ship_city.setText(ds.child("city").getValue(String.class));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,MenuScreen.class));
        Toast.makeText(this, "Order cancelled!", Toast.LENGTH_SHORT).show();
        finish();
    }

}