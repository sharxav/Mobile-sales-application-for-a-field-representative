package com.example.capstone.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.capstone.R;

import Maps.CurrentLocation;
import Scanner.BarCodeScanner;

public class MenuScreen extends AppCompatActivity {
    Toolbar toolbar;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_screen,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_user:
                Logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        toolbar=findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
    }

    public void Sales_Order(View view) {
        startActivity(new Intent(this,Sales_Order.class));
    }

    public void Inventory(View view) {
        startActivity(new Intent(this,Inventory.class));
    }

    public void Survey(View view) {
        startActivity(new Intent(this, SurveyForm.class));
    }

    public void Reports(View view) {
        startActivity(new Intent(this,Reports.class));
    }

    public void customers(View view) {
        startActivity(new Intent(this,ContactList.class));
    }

    public void scanner(View view) {
        startActivity(new Intent(this, BarCodeScanner.class));
    }

    public void getLocation(View view) {
        startActivity(new Intent(this, CurrentLocation.class));
    }

    private void Logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //builder.setTitle("Lo");
        builder.setMessage("Do you want to logout?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(MenuScreen.this,LoginActivity.class));
                Toast.makeText(MenuScreen.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }



}