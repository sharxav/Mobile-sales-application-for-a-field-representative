package Reports;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.capstone.Forms.Reports;
import com.example.capstone.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TopCustomers extends AppCompatActivity {
    BarChart barChart1;
    DatabaseReference top;
    Map<String,Integer> cust=new TreeMap<>();
    String names[]=new String[5];
    Toolbar toolbar7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_customers);
        toolbar7=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar7.findViewById(R.id.tooltext);
        tv.setText("Top 5 Customers");
        setSupportActionBar(toolbar7);
        barChart1=findViewById(R.id.barTop);
        top= FirebaseDatabase.getInstance().getReference().child("Sales_Order");
        TopCustomer();
    }

    private void TopCustomer()
    {
        top.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name;
                int price;
                for(DataSnapshot ds:snapshot.getChildren()) {
                    price = 0;
                    name = ds.child("customer").getValue(String.class);
                    for (DataSnapshot d : ds.getChildren()) {
                        if (d.child("price").exists()) {
                            price += d.child("price").getValue(Integer.class);
                            //Log.d("Price", "" + d.child("Price"));
                        }
                    }


                   if(cust.containsKey(name))
                    {
                        price=cust.get(name)+price;
                        cust.replace(name,price);
                    }
                    else{
                        cust.put(name,price);
                    }
                }


                List<Map.Entry<String, Integer>> sortedlist=entriesSortedByValues(cust);
                ArrayList<BarEntry> barEntries1=new ArrayList<>();
                int size=0;
                for(Map.Entry<String,Integer> item:sortedlist) {
                    if (size < 5) {
                        barEntries1.add(new BarEntry(size, item.getValue()));
                        names[size] = item.getKey();
                        size++;
                    }
                    else{
                        break;
                    }
                }



                BarDataSet barDataSet=new BarDataSet(barEntries1,"Top 5 Customers");
                BarData barData=new BarData(barDataSet);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextSize(12f);

                Legend l = barChart1.getLegend();
                LegendEntry l0=new LegendEntry(names[0],Legend.LegendForm.CIRCLE,10f,2f,null,ColorTemplate.COLORFUL_COLORS[0]);
                LegendEntry l1=new LegendEntry(names[1],Legend.LegendForm.CIRCLE,10f,2f,null,ColorTemplate.COLORFUL_COLORS[1]);
                LegendEntry l2=new LegendEntry(names[2],Legend.LegendForm.CIRCLE,10f,2f,null,ColorTemplate.COLORFUL_COLORS[2]);
                LegendEntry l3=new LegendEntry(names[3],Legend.LegendForm.CIRCLE,10f,2f,null,ColorTemplate.COLORFUL_COLORS[3]);
                LegendEntry l4=new LegendEntry(names[4],Legend.LegendForm.CIRCLE,10f,2f,null,ColorTemplate.COLORFUL_COLORS[4]);
                l.setCustom(new LegendEntry[]{l0,l1,l2,l3,l4});
                l.setEnabled(true);
                barChart1.getXAxis().setLabelCount(5);
                barChart1.getDescription().setEnabled(false);
                barChart1.setData(barData);
                barChart1.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );


        return sortedEntries;
    }

    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,Reports.class));
        finish();
    }
}