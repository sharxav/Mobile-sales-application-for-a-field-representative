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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class SalesOrderReport extends AppCompatActivity {
    LineChart mpLineChart;
    DatabaseReference dbline;
    Map<Integer,Integer> map1=new TreeMap<>();
    Map<Integer,Integer> map2=new TreeMap<>();
    Spinner spinner1,spinner2;
    int position1=0,position2=0;

    ArrayList<Entry> dataVals1=new ArrayList<Entry>();
    ArrayList<Entry> dataVals2=new ArrayList<Entry>();
    Toolbar toolbar8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_report);
        toolbar8=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar8.findViewById(R.id.tooltext);
        tv.setText("Sales Order Report");
        setSupportActionBar(toolbar8);
        mpLineChart=findViewById(R.id.line_chart);
        dbline= FirebaseDatabase.getInstance().getReference().child("Sales_Order");
        spinner1=findViewById(R.id.spin1);
        spinner2=findViewById(R.id.spin2);
        List<String> cat1 = new ArrayList<String>();
        cat1.add("By Month");
        cat1.add("By Year");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        List<String> cat2 = new ArrayList<String>();
        cat2.add("By Value");
        cat2.add("By Number");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat2);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position1=position;
                drawChart(position1,position2);

                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        position2=position;
                        drawChart(position1,position2);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void drawChart(int pos1,int pos2)
    {
        dataVals1.clear();
        dataVals2.clear();
        mpLineChart.invalidate();
        map1.clear();map2.clear();
        dbline.addValueEventListener(new ValueEventListener() {
            int val,total1,total2,count1,count2;
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(pos1==1&&pos2==0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        total1 = 0;total2=0;
                        String date = ds.child("date").getValue(String.class);
                        int year = Integer.parseInt(date.substring(6));
                        for (DataSnapshot d : ds.getChildren()) {
                            if (d.child("price").exists()) {
                                val = d.child("price").getValue(Integer.class);
                                if (ds.child("type").getValue(String.class).equals("OR")) {

                                    if (map1.containsKey(year)) {
                                        total1 = map1.get(year) + val;
                                        map1.replace(year, total1);
                                    } else {
                                        map1.put(year, val);
                                    }
                                }
                                else if(ds.child("type").getValue(String.class).equals("RE")){

                                        if (map2.containsKey(year)) {
                                            total2 = map2.get(year) + val;
                                            map2.replace(year, total2);
                                        } else {
                                            map2.put(year, val);
                                        }

                                }
                            }
                            }
                        }
                    }

                else if(pos1==1&&pos2==1) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        count1 = 1;count2=1;
                        String date = ds.child("date").getValue(String.class);
                        int year = Integer.parseInt(date.substring(6));
                        if(ds.child("type").getValue(String.class).equals("OR")) {
                            if (map1.containsKey(year)) {
                                count1 = map1.get(year) + 1;
                                map1.replace(year, count1);
                            } else {
                                map1.put(year, count1);
                            }
                        }else if(ds.child("type").getValue(String.class).equals("RE")){
                            if (map2.containsKey(year)) {
                                count2 = map2.get(year) + 1;
                                map2.replace(year, count2);
                            } else {
                                map2.put(year, count2);
                            }

                        }
                    }
                }
                else if(pos1==0&&pos2==1) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        count1 = 1;count2=1;
                        String date = ds.child("date").getValue(String.class);
                        int month = Integer.parseInt(date.substring(3, 5));
                        if(ds.child("type").getValue(String.class).equals("OR")) {
                            if (map1.containsKey(month)) {
                                count1 = map1.get(month) + 1;
                                map1.replace(month, count1);
                            } else {
                                map1.put(month, count1);
                            }
                        }else if(ds.child("type").getValue(String.class).equals("RE")){
                            if (map2.containsKey(month)) {
                                count2 = map2.get(month) + 1;
                                map2.replace(month, count2);
                            } else {
                                map2.put(month, count2);
                            }
                        }
                    }

                }
                else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        total1 = 0;total2=0;
                        String date = ds.child("date").getValue(String.class);
                        int month = Integer.parseInt(date.substring(3,5));
                        for (DataSnapshot d : ds.getChildren()) {
                            if (d.child("price").exists()) {
                                val = d.child("price").getValue(Integer.class);
                                if(ds.child("type").getValue(String.class).equals("OR")) {

                                    if (map1.containsKey(month)) {
                                        total1 = map1.get(month) + val;
                                        map1.replace(month, total1);
                                    } else {
                                        map1.put(month, val);
                                    }
                                }
                                else if(ds.child("type").getValue(String.class).equals("RE")){

                                    if (map2.containsKey(month)) {
                                        total2 = map2.get(month) + val;
                                        map2.replace(month, total2);
                                    } else {
                                        map2.put(month, val);
                                    }
                                }
                            }
                        }
                    }

                }

                for (Map.Entry<Integer, Integer> entry : map1.entrySet()) {
                    dataVals1.add(new Entry(entry.getKey(), entry.getValue()));
                    //Log.d("Value", "" + entry.getKey() +" "+ entry.getValue());
                }
                for (Map.Entry<Integer, Integer> entry : map2.entrySet()) {
                    dataVals2.add(new Entry(entry.getKey(), entry.getValue()));
                    //Log.d("Value", "" + entry.getKey() +" "+ entry.getValue());
                }
                LineDataSet lineDataSet1=new LineDataSet(dataVals1,"Sales Order");
                LineDataSet lineDataSet2=new LineDataSet(dataVals2,"Return Order");
                lineDataSet2.setColor(Color.RED);
                lineDataSet1.setColor(Color.CYAN);
                lineDataSet1.setValueTextSize(12f);
                lineDataSet2.setValueTextSize(12f);
                lineDataSet1.setLineWidth(3f);
                lineDataSet2.setLineWidth(3f);
                ArrayList<ILineDataSet> dataSets=new ArrayList<>();
                dataSets.add(lineDataSet1);
                dataSets.add(lineDataSet2);
                LineData data=new LineData(dataSets);
                mpLineChart.getXAxis().setLabelCount(5);
                mpLineChart.getDescription().setEnabled(false);
                mpLineChart.setData(data);
                mpLineChart.invalidate();
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