package com.example.electedperson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private ActionBarDrawerToggle nToggle;
    NavigationView navigationView;

    DatabaseReference locationRef;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        @SuppressLint("WrongViewCast") Toolbar my_toolbar = findViewById(R.id.actionBar);
        my_toolbar.setTitle("");
        setSupportActionBar(my_toolbar);

        DrawerLayout nDrawerLayout = findViewById(R.id.navigationMenu);
        nToggle = new ActionBarDrawerToggle(this, nDrawerLayout, R.string.open, R.string.close);

        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        AtomicReference<SupportMapFragment> supportMapFragment = new AtomicReference<>((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map));
        supportMapFragment.get().getMapAsync(HomeActivity.this);

        locationRef = FirebaseDatabase.getInstance().getReference("Reports");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (nToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                break;
//            case R.id.about_us:
//                startActivity(new Intent(HomeActivity.this, AboutUs.class));
//                break;
//            case R.id.help:
//                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vishwamshukla.intelaedu.com/"));
//                startActivity(Getintent);
//                break;
//            case R.id.chats:
//                startActivity(new Intent(HomeActivity.this, ChatsActivity.class));
//                break;
//        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        locationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                LatLng newLocation = new LatLng(
                        dataSnapshot.child("mlat").getValue(Double.class),
                        dataSnapshot.child("mlang").getValue(Double.class)
                );
                mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .title(dataSnapshot.getKey()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}