package Reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.capstone.Forms.Reports;
import com.example.capstone.R;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReasonReport extends AppCompatActivity {
    private ScatterChart chart;
    DatabaseReference sc,sc1;
    long value;
    Spinner ss;
    ArrayList<Entry> values1 = new ArrayList<>();
    ArrayList<Entry> values2 = new ArrayList<>();
    ArrayList<Entry> values3 = new ArrayList<>();
    ArrayList<Entry> values4 = new ArrayList<>();
    Map<Integer,String> names=new HashMap();
    Map<Integer,Long> val=new HashMap();
    Toolbar toolbar9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reason_report);
        toolbar9=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar9.findViewById(R.id.tooltext);
        tv.setText("Overdue Sales Report");
        setSupportActionBar(toolbar9);
        ss=findViewById(R.id.scatter_spin);
        sc= FirebaseDatabase.getInstance().getReference().child("Delivery");
        sc1=FirebaseDatabase.getInstance().getReference().child("Sales_Order");

        List<String> cat_sc = new ArrayList<String>();
        cat_sc.add("By Month");
        cat_sc.add("By Year");
        ArrayAdapter<String> scAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat_sc);
        scAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ss.setAdapter(scAdapter);

        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setMaxHighlightDistance(50f);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(true);

        chart.invalidate();

        ss.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onProgress(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                CustomMarkerView mv = new CustomMarkerView(ReasonReport.this, R.layout.marker_layout);
                chart.setMarkerView(mv);


            }

            @Override
            public void onNothingSelected() {

            }
        });


    }
    private void onProgress(int pos){
        values1.clear();values2.clear();values3.clear();values4.clear();
        chart.clear();

        sc1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    value=0;
                    String name=ds.child("customer").getValue(String.class);
                    for(DataSnapshot d:ds.getChildren()) {
                        if (d.child("qty").getValue(Long.class) != null && d.child("price").getValue(Long.class) != null) {
                            long qty = d.child("qty").getValue(Long.class);
                            long price = d.child("price").getValue(Long.class);
                            value += qty * price;
                        }
                    }
                    names.put(Integer.valueOf(ds.getKey()),name);
                    val.put(Integer.valueOf(ds.getKey()),value);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(pos==1) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("del").exists()) {
                            String del = ds.child("del").getValue(String.class);
                            String req = ds.child("order").getValue(String.class);
                            int year = Integer.parseInt(del.substring(6,10));
                            try {
                                Date del_date = new SimpleDateFormat("dd/MM/yyyy").parse(del);
                                Date req_date = new SimpleDateFormat("dd/MM/yyyy").parse(req);
                                if (del_date.before(req_date) || del_date.equals(req_date)) {

                                } else {

                                    String reason = ds.child("reason").getValue(String.class);
                                    int id = Integer.parseInt(ds.getKey());

                                    switch (reason) {
                                        case "materials unavailable":
                                            values1.add(new Entry(year, id));
                                            break;
                                        case "peak time":
                                            values2.add(new Entry(year, id));
                                            break;

                                        case "machine malfunction":
                                            values3.add(new Entry(year, id));
                                            break;

                                        case "incorrect customs":
                                            values4.add(new Entry(year,id));
                                            break;
                                            
                                    }
                                   // Log.d("Values",""+values1+" "+values2+" "+values3+" "+values4);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                else{
                    for(DataSnapshot ds:snapshot.getChildren()) {
                        if (ds.child("del").exists()) {
                            String del = ds.child("del").getValue(String.class);
                            String req = ds.child("order").getValue(String.class);
                            int month = Integer.parseInt(del.substring(3,5));
                            try {
                                Date del_date = new SimpleDateFormat("dd/MM/yyyy").parse(del);
                                Date req_date = new SimpleDateFormat("dd/MM/yyyy").parse(req);
                                if (del_date.before(req_date) || del_date.equals(req_date)) {

                                } else {

                                    String reason = ds.child("reason").getValue(String.class);
                                    int id = Integer.parseInt(ds.getKey());

                                    switch (reason) {
                                        case "materials unavailable":
                                            values1.add(new Entry(month, id));
                                            break;
                                        case "peak time":
                                            values2.add(new Entry(month, id));
                                            break;

                                        case "machine malfunction":
                                            values3.add(new Entry(month, id));
                                            break;
                                        case "incorrect customs":
                                            values4.add(new Entry(month,id));
                                            break;


                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }

                ScatterDataSet set1 = new ScatterDataSet(values1, "Materials Unavailable");
                set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
                set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);

                ScatterDataSet set2 = new ScatterDataSet(values2, "Peak Time");
                set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                set2.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
                set2.setScatterShapeHoleRadius(3f);
                set2.setColor(ColorTemplate.COLORFUL_COLORS[3]);

                ScatterDataSet set3 = new ScatterDataSet(values3, "Malfunction");
                set3.setScatterShape(ScatterChart.ScatterShape.TRIANGLE);
                set3.setColor(ColorTemplate.COLORFUL_COLORS[2]);

                ScatterDataSet set4 = new ScatterDataSet(values4, "Incorrect Customs");
                set4.setScatterShape(ScatterChart.ScatterShape.CROSS);
                set4.setColor(ColorTemplate.COLORFUL_COLORS[1]);



                set1.setScatterShapeSize(30f);
                set2.setScatterShapeSize(30f);
                set3.setScatterShapeSize(30f);
                set4.setScatterShapeSize(30f);


                ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1); // add the data sets
                dataSets.add(set2);
                dataSets.add(set3);
                dataSets.add(set4);


                // create a data object with the data sets
                ScatterData data = new ScatterData(dataSets);
                if(pos==1)
                {
                    chart.getXAxis().setAxisMinimum(2000);
                    chart.getXAxis().setAxisMaximum(2025);
                }
                else{
                    chart.getXAxis().setAxisMinimum(0);
                    chart.getXAxis().setAxisMaximum(14);
                }
                chart.setData(data);
                chart.getXAxis().setLabelCount(6);
                chart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = (TextView) findViewById(R.id.tvContent);
        }
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            int num= (int) e.getY();
            String s="Order No: "+num+"\n"+"Customer: "+names.get(num)+"\n"+"Value: "+val.get(num);
            tvContent.setText(s);

            super.refreshContent(e, highlight);
        }
        private MPPointF mOffset;
        @Override
        public MPPointF getOffset() {
            if(mOffset == null) {
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }
    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,Reports.class));
        finish();
    }

}
