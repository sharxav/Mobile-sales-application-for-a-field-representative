package Reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.capstone.Forms.Reports;
import com.example.capstone.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryReport extends AppCompatActivity {
    Spinner spinner3,spinner4;
    PieChart pieChart;
    DatabaseReference db;
    Map<String,Integer> piemap=new HashMap<>();
    ArrayList<PieEntry> pieEntries=new ArrayList<>();
   Toolbar toolbar11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        toolbar11=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar11.findViewById(R.id.tooltext);
        tv.setText("Inventory Report");
        setSupportActionBar(toolbar11);
        pieChart=findViewById(R.id.piechart);
        spinner3=findViewById(R.id.spin3);
        spinner4=findViewById(R.id.spin4);
        db= FirebaseDatabase.getInstance().getReference().child("Inventory");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               List<String> wholesalers = new ArrayList<String>();

                for(DataSnapshot ds:snapshot.getChildren()) {
                    if (!wholesalers.contains(ds.child("wholesaler2").getValue(String.class))) {
                        wholesalers.add(ds.child("wholesaler2").getValue(String.class));
                    }
                }

               ArrayAdapter<String> dw = new ArrayAdapter<String>(InventoryReport.this, android.R.layout.simple_spinner_item, wholesalers);
                dw.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(dw);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawPieChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

  private void setSpinnerValues()
    {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> date=new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("wholesaler2").getValue(String.class).equals(spinner3.getSelectedItem().toString()))
                    {
                        date.add(ds.child("date2").getValue(String.class));
                    }
                }

                ArrayAdapter<String> d = new ArrayAdapter<String>(InventoryReport.this, android.R.layout.simple_spinner_item, date);
                d.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner4.setAdapter(d);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void drawPieChart()
    {
        piemap.clear();
        pieEntries.clear();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    for(DataSnapshot d:ds.getChildren()) {

                        if ((ds.child("wholesaler2").getValue(String.class).equals(spinner3.getSelectedItem())) && (ds.child("date2").getValue(String.class).equals(spinner4.getSelectedItem()))&&(d.child("product").exists())) {
                            int cal=d.child("bottles").getValue(Integer.class)*d.child("cases").getValue(Integer.class);
                            piemap.put(d.child("product").getValue(String.class),cal);

                        }
                    }

                }

                for (Map.Entry<String, Integer> entry : piemap.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue(),entry.getKey()));
                    //Log.d("Value", "" + entry.getKey() +" "+ entry.getValue());
                }

                PieDataSet pieDataSet=new PieDataSet(pieEntries,"Inventory");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData=new PieData(pieDataSet);
                pieData.setValueTextSize(12f);
                pieChart.setDrawHoleEnabled(false);
                pieChart.setUsePercentValues(true);
                pieChart.getDescription().setEnabled(false);
                pieChart.setData(pieData);
                pieChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,Reports.class));
        finish();
    }
}