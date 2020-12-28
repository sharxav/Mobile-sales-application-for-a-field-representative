package Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.Forms.MenuScreen;
import com.example.capstone.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class CurrentLocation extends AppCompatActivity implements LocationListener {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    Task<Location> task;
    GoogleMap mMap;
    LatLng start = null, end = null;
    TextView mapText;
    LatLng latLng;
    PolylineOptions polylineOptions = null;
    DatabaseReference mapdb, loc_log, mapdb1;
    Toolbar toolbar12;
    SharedPreferences sharedPreferences;
    LocationManager locationManager;
    String name;
    Date then,now;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mapmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_more:
                startActivity(new Intent(this, DisplayLog.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        toolbar12 = findViewById(R.id.MyToolbar4);
        TextView tv = (TextView) toolbar12.findViewById(R.id.tooltext);
        tv.setText("GPS Location");
        setSupportActionBar(toolbar12);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
        name = sharedPreferences.getString("user", null);
        mapText = findViewById(R.id.maptext);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(this);
        mapdb = FirebaseDatabase.getInstance().getReference().child("Location");
        mapdb1 = FirebaseDatabase.getInstance().getReference().child("Users");
        loc_log = FirebaseDatabase.getInstance().getReference().child("Logs");
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

        visited();
        if (ActivityCompat.checkSelfPermission(CurrentLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            task = client.getLastLocation();
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);


    }

    private void getCurrentLocation() {
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            start = latLng;
                            // Log.d("current",location.getLatitude()+" "+location.getLongitude());
                            checkStore(location.getLatitude(), location.getLongitude());
                            addMarkers();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6f));
                            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    Double latitude = marker.getPosition().latitude;
                                    Double longitude = marker.getPosition().longitude;
                                    end = new LatLng(latitude, longitude);
                                    String url = getRequestUrl(start, end);
                                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                                    taskRequestDirections.execute(url);
                                    return false;
                                }
                            });

                        }
                    });
                }


            }
        });

    }

    private void addLog(Location location2)
    {
        //Toast.makeText(this, "location not changed", Toast.LENGTH_SHORT).show();
        mapdb1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.child("username").getValue(String.class).equals(name))
                    {
                        //Toast.makeText(CurrentLocation.this, ""+ds.child("username").getValue(String.class), Toast.LENGTH_SHORT).show();
                       //Log.d("Latitude",""+location2.getLatitude());
                       //Log.d("Longitude",""+location2.getLongitude());
                        for(DataSnapshot d:ds.child("Location").getChildren())
                        {
                           // if(d.child("latitude").getValue(Double.class).equals(location2.getLatitude())&&d.child("longitude").getValue(Double.class).equals(location2.getLongitude()))
                            if(d.child("latitude").getValue(Double.class).equals(40.38961333333334)&&d.child("longitude").getValue(Double.class).equals(-74.53805833333334))
                            {
                               // Toast.makeText(CurrentLocation.this, ""+d.child("storename").getValue(String.class), Toast.LENGTH_SHORT).show();
                                Log.d("Store",""+d.child("storename").getValue(String.class));
                                addtodatabase(d.child("storename").getValue(String.class));
                                break;
                            }
                        }
                    }
                }


            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

    private void addtodatabase(String storename) {
        //
        loc_log.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(name)) {
                        long id=ds.child(storename).getChildrenCount();
                        if(id==0) {
                            loc_log.child(ds.getKey()).child(storename).child("1").setValue(String.valueOf(Calendar.getInstance().getTime()));
                            break;
                        }
                        else{
                            //Toast.makeText(CurrentLocation.this, ""+ds.child(storename).child(String.valueOf(id)).getValue(String.class), Toast.LENGTH_SHORT).show();
                                now = Calendar.getInstance().getTime();
                                then = new Date(ds.child(storename).child(String.valueOf(id)).getValue(String.class));
                                long diff=now.getTime()-then.getTime();
                                //Log.d("Time",""+diff);
                            if(diff>5*60*1000)
                            {
                                loc_log.child(ds.getKey()).child(storename).child(String.valueOf(id+1)).setValue(String.valueOf(Calendar.getInstance().getTime()));
                                break;
                            }

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {

        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + "AIzaSyBgrOm08Hd9sBr0oF4h0eiSZtfcc3GiBig";
        //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        Log.d("URL", url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }


    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
            TaskParseDuration taskParseDuration = new TaskParseDuration();
            taskParseDuration.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionParser directionsParser = new DirectionParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            if (polylineOptions != null) {
                mMap.clear();
                polylineOptions = null;
                visited();
                addMarkers();
            }


            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                //Toast.makeText(CurrentLocation.this, ""+polylineOptions, Toast.LENGTH_SHORT).show();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void addMarkers() {
        mMap.addMarker(new MarkerOptions()
                .position((start))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mapdb1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("username").getValue(String.class).equals(name)) {
                        for (DataSnapshot d : ds.child("Location").getChildren()) {
                            if (d.child("visited").getValue(Boolean.class) == true) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(d.child("latitude").getValue(Double.class), d.child("longitude").getValue(Double.class)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            } else{
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(d.child("latitude").getValue(Double.class), d.child("longitude").getValue(Double.class)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                        }
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public class TaskParseDuration extends AsyncTask<String, Void, HashMap<String, String>> {
        String duration = "", distance = "";
        HashMap<String, String> directionsList = new HashMap<>();

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            // String googleDirectionsData="";

            //googleDirectionsData=requestDirection(strings[0]);
            DirectionParser directionParser = new DirectionParser();
            directionsList = directionParser.parseDirections(strings[0]);

            return directionsList;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> lists) {
            duration = directionsList.get("duration");
            distance = directionsList.get("distance");
            //Log.d("Duration", duration);
            //Log.d("Distance", distance);
            mapText.setText("Distance: " + distance + "\nDuration: " + duration);
        }


    }

    private void checkStore(double latitude, double longitude) {

        Log.d("Values", "" + latitude + " " + longitude);
        mapdb1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("username").getValue(String.class).equals(name)) {
                        for (DataSnapshot d : ds.child("Location").getChildren()) {
                            //Toast.makeText(CurrentLocation.this, ""+ d.getKey(), Toast.LENGTH_SHORT).show();
                            if (d.child("latitude").getValue(Double.class).equals(latitude) && d.child("longitude").getValue(Double.class).equals(longitude)) {
                                mapdb1.child(ds.getKey()).child("Location").child(d.getKey()).child("visited").setValue(true);
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void visited() {


        mapdb1.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean flag = true;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("username").getValue(String.class).equals(name)) {
                        for (DataSnapshot d : ds.child("Location").getChildren()) {
                            if ((d.child("visited").getValue(Boolean.class)) == false) {
                                flag = false;
                                break;
                            }
                        }
                        break;
                    }
                }
                if (flag) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("username").getValue(String.class).equals(name)) {
                            for (DataSnapshot d : ds.child("Location").getChildren()) {
                                mapdb1.child(ds.getKey()).child("Location").child(d.getKey()).setValue(false);
                            }
                            break;
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void back_to_menu(View view) {
        startActivity(new Intent(this, MenuScreen.class));
        finish();
    }

@Override
public void onLocationChanged(@NonNull Location location) {
    Log.d("Location", "" + location.getLongitude() + " " + location.getLatitude());
    addLog(location);
}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }



}






