package com.sumang.mapsgoogle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

// new imports

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import com.sumang.mapsgoogle.helpers.JSONParser;

// new imports ends here


import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private static final String TAG_SAT = "Satellite";
    private static final String TAG_HYB = "Hybrid";
    private static final String TAG_TER = "Terrain";
    private static final String TAG_NOR = "Normal";


    JSONArray features = null;
  //  String url = "http://nepal.piensa.co/data/helipads.json";
    //String url = "http://nepal.piensa.co/data/medical_point.json";
    String url = "http://nepal.piensa.co/data/village_green.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.mipmap.eye);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        new JSONParse().execute();

        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageResource(R.mipmap.hybmap);

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.mipmap.satimg);

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageResource(R.mipmap.terrmap);

        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageResource(R.mipmap.normap);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        SubActionButton buttonitemIcon1 = itemBuilder.setContentView(itemIcon1).build();
        SubActionButton buttonitemIcon2 = itemBuilder.setContentView(itemIcon2).build();
        SubActionButton buttonitemIcon3 = itemBuilder.setContentView(itemIcon3).build();
        SubActionButton buttonitemIcon4 = itemBuilder.setContentView(itemIcon4).build();

        buttonitemIcon1.setTag(TAG_HYB);
        buttonitemIcon2.setTag(TAG_SAT);
        buttonitemIcon3.setTag(TAG_TER);
        buttonitemIcon4.setTag(TAG_NOR);


        buttonitemIcon1.setOnClickListener(this);
        buttonitemIcon2.setOnClickListener(this);
        buttonitemIcon3.setOnClickListener(this);
        buttonitemIcon4.setOnClickListener(this);

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonitemIcon1)
                .addSubActionView(buttonitemIcon2)
                .addSubActionView(buttonitemIcon3)
                .addSubActionView(buttonitemIcon4)
                // ...
                .attachTo(actionButton)
                .build();

    }

    // async task class starts here >>>>>>>>>>>>>>

    private class JSONParse extends android.os.AsyncTask<String, String, org.json.JSONObject> {
        private android.app.ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            System.out.println("inside pre execute");
            // titleImage = (android.widget.ImageView) findViewById(pas.pranav.uniglobecollege.R.id.ivTitleImage);
            //super.onPreExecute();
        }

        @Override
        protected org.json.JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            org.json.JSONObject json = jParser.getJSONFromUrl(url);
            System.out.println("inside background");
            System.out.println("json contents : " + json);
            return json;
        }

        @Override
        protected void onPostExecute(org.json.JSONObject json) {
            JSONObject feature = null;
            JSONArray coordinates = null, coordinatesArray = null;
            JSONObject geometry = null;
            String type = null;
            System.out.println("json inside post  " + json);
            Double lat = 0.00, lng = 0.00;
            try {
                // Getting JSON Array
                features = json.getJSONArray("features");
                for (int i = 0; i < features.length(); i++) {
                    feature = features.getJSONObject(i);
                    geometry = (JSONObject) feature.get("geometry");
                    type = geometry.getString("type");
                    coordinates = (JSONArray) geometry.get("coordinates");
                    switch (type) {
                        case "Point":
                            lng = coordinates.getDouble(0);
                            lat = coordinates.getDouble(1);
                            LatLng latLng = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Rani Pokhari").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            System.out.println("point lat,long  " + lat + "," + lng);
                            break;
                        case "Polygon":
                            PolygonOptions rectOptions = null;
                            ArrayList<LatLng> val = new ArrayList<>();
                            coordinatesArray = coordinates.getJSONArray(0);
                            for (int j = 0; j < coordinatesArray.length(); j++) {
                                lng = coordinatesArray.getJSONArray(j).getDouble(0);
                                lat = coordinatesArray.getJSONArray(j).getDouble(1);
                                System.out.println("polygon lat,long  " + lat + "," + lng);
                                val.add(new LatLng(lat, lng));
                            }
                            rectOptions = new PolygonOptions().addAll(val).fillColor(Color.RED);
                            Polygon polygon = mMap.addPolygon(rectOptions);
                            break;

                        case "default":
                            System.out.println(" miss match type " + type);
                            break;
                    }
                }

            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }

        }

    }


    // <<<<<<<<<<<<<<<<<<< async task ends here


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

//    public void onSat(View view){
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//
//
//    }
//
//    public void onTer(View view){
//
//
//     mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//    }
    public void onSearch(View view) {
        EditText location_sg = (EditText) findViewById(R.id.editlatt);
        String location = location_sg.getText().toString();
        List<android.location.Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if(addressList.size()>0) {
                    android.location.Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(" your Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        tileProvider = new GoogleMapOfflineTileProvider(file);
//        offlineOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(3000));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.addMarker(new MarkerOptions().position(new LatLng(27.707845, 85.314689)).title("Rani Pokhari").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(27.749986, 85.261760)).title("Chhatre Deurali").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Grazing Land"));
//        final LatLng MELBOURNE = new LatLng(27.749986, 85.261760);
//        Marker melbourne = mMap.addMarker(new MarkerOptions()
//                .position(MELBOURNE)
//                .title("Melbourne"));
//        melbourne.showInfoWindow();



// Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(27.769126, 85.268626),
                        new LatLng(27.773476, 85.255464),
                        new LatLng(27.762188, 85.245379),
                        new LatLng(27.757194, 85.235981),
                        new LatLng(27.750918, 85.218607),
                        new LatLng(27.728072, 85.276822),
                        new LatLng(27.735364, 85.301262))
                .fillColor(0x4F00FF00)
                .strokeColor(0x2F00FF00);
//        polygon.setFillColor(0x7F00FF00);

// Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(rectOptions);





    }

    @Override
    public void onClick(View v) {
            if(v.getTag().equals(TAG_HYB)) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(getApplicationContext(), "HYBRID MAP", Toast.LENGTH_SHORT).show();
            }

        if(v.getTag().equals(TAG_SAT)){
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Toast.makeText(getApplicationContext(), "SATELLITE MAP", Toast.LENGTH_SHORT).show();
        }

        if(v.getTag().equals(TAG_TER)){
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            Toast.makeText(getApplicationContext(), "TERRAIN MAP", Toast.LENGTH_SHORT).show();
        }

        if (v.getTag().equals(TAG_NOR)) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(getApplicationContext(), "NORMAL MAP", Toast.LENGTH_SHORT).show();
        }


    }
}
