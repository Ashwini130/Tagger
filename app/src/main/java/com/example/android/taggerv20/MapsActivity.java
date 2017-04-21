package com.example.android.taggerv20;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button showLocation,sharethelocation,goToGroup,goToChatRoom;
    GPSTracker mGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        addListenerOnButton();

        /*goToChatRoom=(Button) findViewById(R.id.chatroom);

        goToChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_chat = new Intent(MapsActivity.this,NameActivity.class);
                startActivity(intent_chat);
            }
        });*/

        showLocation=(Button) findViewById(R.id.button);

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=getIntent();
                finish();
                startActivity(intent);
            }
        });

        sharethelocation=(Button) findViewById(R.id.share);

        sharethelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIt();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void addListenerOnButton(){
        final Context context=this;

        goToGroup=(Button) findViewById(R.id.group);
        goToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Login.class);
                startActivity(intent);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    char a[] = new char[100];

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mGPS = new GPSTracker(MapsActivity.this);

        if (mGPS.canGetLocation()) {
            // Add a marker in Current Position and move the camera
            LatLng currentLocation = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());

            mMap.addMarker(new MarkerOptions().position(currentLocation).title(" Current Location"));

            float zoomLevel = 16;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,zoomLevel));

            Geocoder gc = new Geocoder(MapsActivity.this);
            if (gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(mGPS.getLatitude(),mGPS.getLongitude(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = list.get(0);
                StringBuffer str = new StringBuffer();
                str.append(" "+ address.getSubLocality());
                str.append(", " + address.getLocality());
                str.append(", "+ address.getSubAdminArea());
                String strAddress = str.toString();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.notification_icon);
                mBuilder.setContentTitle("Your Location");
                mBuilder.setContentText(strAddress);

                NotificationManager mNotificationManager = ( NotificationManager ) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify( 1 , mBuilder.build());
            }
        }
        else mGPS.showSettingsAlert();
    }

    private void shareIt() {

        mGPS = new GPSTracker(MapsActivity.this);

        if (mGPS.canGetLocation()) {
            // Add a marker in Current Position and move the camera
            LatLng currentLocation = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());

            Geocoder gc = new Geocoder(MapsActivity.this);
            if (gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = list.get(0);
                StringBuffer str = new StringBuffer();
                str.append(" " + address.getSubLocality());
                str.append(", " + address.getLocality());
                str.append(", " + address.getSubAdminArea());
                String strAddress = str.toString();


                //sharing implementation

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, strAddress);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
        else mGPS.showSettingsAlert();
    }
}


