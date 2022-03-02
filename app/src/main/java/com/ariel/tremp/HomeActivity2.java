package com.ariel.tremp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity2 extends AppCompatActivity implements DateDialog.GetTime, OnMapReadyCallback, RoutingListener {

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final String TAG = "HomeActivity2";

    private enum Status {
        FROM, TO, CLEAR
    }

    private TextView tvTime;
    private GoogleMap mMap;
    private FloatingActionButton fabAddress, fabClear;
    private TextInputLayout etFrom, etTo;

    private boolean opened = false;
    private Status status = Status.FROM;
    private LatLng fromPoint, toPoint;
    private Time time;
    private Tremp tremp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);

        tvTime = findViewById(R.id.tvTime);
        fabAddress = findViewById(R.id.addresses);
        fabClear = findViewById(R.id.clear);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // public methods
    public void addDate(View view) {
        DialogFragment fragment = new DateDialog();
        fragment.show(getSupportFragmentManager(), "Date Dialog");
    }

    public void clearPoints(View view) {
        mMap.clear();
        status = Status.FROM;
        etFrom.getEditText().setText("");
        etTo.getEditText().setText("");
        tremp = null;
        tvTime.setText("Date: none");
        fabAddress.setVisibility(View.GONE);
        fabClear.setVisibility(View.GONE);
        toast("Points cleared");
    }

    public void setAddresses(View view) {
        getAddress();
    }

    public void openTrempss(View view) {
        Intent openTremps = new Intent(this, TrempsActivity.class);
        startActivity(openTremps);
    }

    public void findTremp(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tremp.setTime(time);

        db.collection("tremps")
                .document(tremp.getFromAddress())
                .set(tremp).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        toast("tremp added!");
                        Intent openTremps = new Intent(this, TrempsActivity.class);
                        startActivity(openTremps);
                    } else {
                        toast("Error on adding tremp");
                        clearPoints(null);
                    }
                });
    }

    // general methods
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // map methods
    private void addMarker(LatLng latLng, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getResources().getString(R.string.api_key);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.i(TAG, "getDirectionsUrl: "  + url);

        return url;
    }

    private void getAddress() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getDirectionsUrl(fromPoint, toPoint);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject routes = (JSONObject) object.getJSONArray("routes").get(0);
                JSONObject legs = (JSONObject) routes.getJSONArray("legs").get(0);
                String duration = legs.getJSONObject("duration").getString("text");
                String distance = legs.getJSONObject("distance").getString("text");

                tremp = new Tremp(new PointData(fromPoint), new PointData(toPoint),
                        legs.getString("start_address"),
                        legs.getString("end_address"),
                        duration, distance);

                etFrom.getEditText().setText(tremp.getFromAddress());
                etTo.getEditText().setText(tremp.getToAddress());

//                toast(tremp.toString());
                findRoutes(fromPoint, toPoint);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.i("TAG", "onErrorResponse: " + error.toString())) {
            @Override
            public String getBodyContentType() { return super.getBodyContentType(); }

            @Override
            public byte[] getBody() throws AuthFailureError { return super.getBody(); }
        };
        queue.add(stringRequest);
    }

    private void setMapMethods() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(location -> {
            if (!opened) {
                LatLng ll =new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 16f);
                mMap.animateCamera(cameraUpdate);
                opened = true;
            }
        });

        mMap.setOnMapClickListener(latLng -> {
            switch (status) {
                case FROM:
                    fromPoint = latLng;
                    addMarker(fromPoint, "From");
                    status = Status.TO;
                    break;

                case TO:
                    toPoint = latLng;
                    addMarker(toPoint, "To");
                    getDirectionsUrl(fromPoint, toPoint);
                    status = Status.CLEAR;
                    fabAddress.setVisibility(View.VISIBLE);
                    fabClear.setVisibility(View.VISIBLE);
                    break;

                case CLEAR:
                    toast("Clear points to change");
                    break;
            }
        });
    }

    // function to find Routes.
    public void findRoutes(LatLng from, LatLng to) {
        if (from == null || to == null)
            toast("Unable to get location");
        else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(from, to)
                    .key(getResources().getString(R.string.api_key))
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
        toast("Finding Route...");
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        ArrayList<Polyline> polylines = new ArrayList<>();
        PolylineOptions polyOptions = new PolylineOptions();

        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            if(i == shortestRouteIndex) {
                polyOptions.color(Color.RED);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                polylines.add(mMap.addPolyline(polyOptions));
            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(fromPoint, toPoint);
    }

    @Override
    public void get(Time time) {
        this.time = time;
        tvTime.setText("Date: " + time);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setMapMethods();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            Intent openProfile = new Intent(this, ProfileActivity.class);
            startActivity(openProfile);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setMapMethods();
            } else {
                toast("PERMISSION DENIED");
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }
}
