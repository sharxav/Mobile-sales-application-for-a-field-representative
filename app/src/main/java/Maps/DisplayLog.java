package Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import com.example.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayLog extends AppCompatActivity {
    Spinner log_spin;
    DatabaseReference log_db2;
    TableLayout tab_log;
    Toolbar toolbar13;
    SharedPreferences sharedPreferences;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_log);
        toolbar13=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar13.findViewById(R.id.tooltext);
        tv.setText("Logs");
        setSupportActionBar(toolbar13);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
        name=sharedPreferences.getString("user",null);


        log_spin = findViewById(R.id.logSpinner);
        tab_log = findViewById(R.id.logtable);
        log_db2 = FirebaseDatabase.getInstance().getReference().child("Logs");

        log_db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> locs = new ArrayList<String>();
           for(DataSnapshot ds:snapshot.getChildren())
           {
               if(ds.getKey().equals(name))
               {
                   for(DataSnapshot d:ds.getChildren())
                   {
                       locs.add(d.getKey());
                   }
               }
           }
                ArrayAdapter<String> dataAdapterlog = new ArrayAdapter<String>(DisplayLog.this, R.layout.spinner_item, locs);
                dataAdapterlog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                log_spin.setAdapter(dataAdapterlog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        log_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               displaydetails(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void displaydetails(int pos) {
        String store = log_spin.getItemAtPosition(pos).toString();
        tab_log.removeViewsInLayout(1, tab_log.getChildCount() - 1);

        log_db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child(name).child(store).getChildren()) {
                    long id=0;
                    View view = getLayoutInflater().inflate(R.layout.row_log, null, false);
                    TextView tv1 = view.findViewById(R.id.textView_1);
                    tv1.setText(ds.getValue(String.class));
                    tab_log.addView(view);
                    Log.d("values..",""+ds.getValue(String.class));

            }

                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,CurrentLocation.class));
        finish();

    }

}