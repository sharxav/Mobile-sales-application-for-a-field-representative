package Reports;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.capstone.Forms.Reports;
import com.example.capstone.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OverdueSalesReport extends AppCompatActivity {
    BarChart barChart;
    Spinner barSpinner;
    DatabaseReference overdb;
    Map<Integer,Integer> barmap1=new TreeMap<>();
    Map<Integer,Integer> barmap2=new TreeMap<>();
    ArrayList<BarEntry> barEntries=new ArrayList<>();
    int[] colors=new int[]{Color.GREEN,Color.RED};
    String[] arr=new String[]{"On-time","Overdue"};
    Toolbar toolbar10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overdue_sales_report);
        toolbar10=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar10.findViewById(R.id.tooltext);
        tv.setText("Delivery Report");
        setSupportActionBar(toolbar10);
        barChart=findViewById(R.id.barChart);
        barSpinner=findViewById(R.id.bar_spinner);
        overdb= FirebaseDatabase.getInstance().getReference().child("Delivery");

        List<String> cat_bar = new ArrayList<String>();
        cat_bar.add("By Month");
        cat_bar.add("By Year");
        ArrayAdapter<String> barAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat_bar);
        barAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barSpinner.setAdapter(barAdapter);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        barSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawBarGraph(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void drawBarGraph(int pos)
    {
        barmap1.clear();
        barmap2.clear();
        barEntries.clear();
        overdb.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sDate1,sDate2;
                Date date1,date2;
                int count1=1,count2=1,year,getyear,getval1,getval2,month;
                if(pos==1) {


                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("order").exists() && ds.child("del").exists()) {

                        sDate1 = ds.child("order").getValue(String.class);
                        sDate2 = ds.child("del").getValue(String.class);
                        year = Integer.parseInt(sDate1.substring(6));
                        try {
                            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                            date2 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate2);
                            if (date2.before(date1) || date2.equals(date1)) {
                                if (barmap1.containsKey(year)) {
                                    count1 = barmap1.get(year) + 1;
                                    barmap1.replace(year, count1);
                                } else {
                                    barmap1.put(year, count1);
                                    if (!barmap2.containsKey(year)) {
                                        barmap2.put(year, 0);
                                    }
                                }
                            } else {
                                if (barmap2.containsKey(year)) {
                                    count2 = barmap2.get(year) + 1;
                                    barmap2.replace(year, count2);
                                } else {
                                    barmap2.put(year, count2);
                                    if (!barmap1.containsKey(year)) {
                                        barmap1.put(year, 0);
                                    }
                                }

                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }}
                else{
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("order").exists() && ds.child("del").exists()) {

                            sDate1 = ds.child("order").getValue(String.class);
                            sDate2 = ds.child("del").getValue(String.class);
                            month = Integer.parseInt(sDate1.substring(3, 5));
                            try {
                                date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                                date2 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate2);
                                if (date2.before(date1) || date2.equals(date1)) {
                                    if (barmap1.containsKey(month)) {
                                        count1 = barmap1.get(month) + 1;
                                        barmap1.replace(month, count1);
                                    } else {
                                        barmap1.put(month, count1);
                                        if (!barmap2.containsKey(month)) {
                                            barmap2.put(month, 0);
                                        }
                                    }
                                } else {
                                    if (barmap2.containsKey(month)) {
                                        count2 = barmap2.get(month) + 1;
                                        barmap2.replace(month, count2);
                                    } else {
                                        barmap2.put(month, count2);
                                        if (!barmap1.containsKey(month)) {
                                            barmap1.put(month, 0);
                                        }
                                    }

                                }


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                for (Map.Entry<Integer, Integer> entry : barmap1.entrySet()) {
                    getyear=entry.getKey();
                    getval1=entry.getValue();
                    if(barmap2.containsKey(getyear))
                    {
                        getval2=barmap2.get(getyear);
                    }
                    else{
                        getval2=0;
                    }
                    barEntries.add(new BarEntry(getyear,new float[]{getval1,getval2}));

                }
                BarDataSet barDataSet=new BarDataSet(barEntries,"Orders");
                BarData barData=new BarData(barDataSet);
                barDataSet.setColors(colors);
                barDataSet.setValueTextSize(12f);
                List<LegendEntry> temp=createLabels();
                Legend l=barChart.getLegend();
                l.setCustom(temp);
                barChart.getDescription().setEnabled(false);
                barChart.getXAxis().setLabelCount(4);
                barChart.setData(barData);
                barChart.invalidate();
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

    private List<LegendEntry> createLabels()
    {
        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = arr[i];
            entries.add(entry);
        }

        return entries;
    }
}