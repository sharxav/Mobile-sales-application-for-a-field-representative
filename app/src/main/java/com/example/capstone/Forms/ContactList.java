package com.example.capstone.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Maps.DisplayLog;

public class ContactList extends AppCompatActivity {
    RecyclerView recyclerView;
    DataAdapter dataAdapter;
    List<Recycler> list;
    DatabaseReference dbref;
    Toolbar toolbar1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                startActivity(new Intent(this, AddCustomers.class));
                break;

            case R.id.item_search:
                SearchView searchView=(SearchView)item.getActionView();
                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        dataAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        toolbar1=findViewById(R.id.MyToolbar4);
        TextView tv=(TextView)toolbar1.findViewById(R.id.tooltext);
        tv.setText("Contacts");
        setSupportActionBar(toolbar1);

        dbref= FirebaseDatabase.getInstance().getReference().child("Contacts");
        dbref.keepSynced(true);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<>();

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    Recycler data=ds.getValue(Recycler.class);
                    list.add(data);
                }
                dataAdapter=new DataAdapter(list,ContactList.this);
                recyclerView.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back_to_menu(View view)
    {
        startActivity(new Intent(this,MenuScreen.class));
        finish();
    }

}