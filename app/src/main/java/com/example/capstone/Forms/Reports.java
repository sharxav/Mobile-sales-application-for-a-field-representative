package com.example.capstone.Forms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import Reports.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.capstone.R;

public class Reports extends AppCompatActivity {
    Toolbar toolbar6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        toolbar6=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar6.findViewById(R.id.tooltext);
        tv.setText("Reports");
        setSupportActionBar(toolbar6);
    }

    public void TopCustomers(View view) {
        startActivity(new Intent(getApplicationContext(),TopCustomers.class));
    }

    public void InventoryReport(View view) {
        startActivity(new Intent(this, InventoryReport.class));
    }

    public void overdue(View view) {
        startActivity(new Intent(this, OverdueSalesReport.class));
    }

    public void SalesOrder(View view) {
        startActivity(new Intent(this, SalesOrderReport.class));
    }

    public void Reason(View view) {
        startActivity(new Intent(this, ReasonReport.class));
    }

    public  void back_to_menu(View view)
    {
        startActivity(new Intent(this,MenuScreen.class));
        finish();
    }
}