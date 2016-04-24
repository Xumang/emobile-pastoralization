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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

// new imports

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import com.google.android.gms.maps.model.CameraPosition;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private static final String TAG_SAT = "Satellite";
    private static final String TAG_HYB = "Hybrid";
    private static final String TAG_TER = "Terrain";
    private static final String TAG_NOR = "Normal";
    String url = null;
    Double distance = 10.0;
    Spinner spinner;

    JSONArray features = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //For Spinner
        spinner = (Spinner)findViewById(R.id.spinnerid);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Resources,android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        // for fab button
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.mipmap.eye);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0){
            url = "http://nepal.piensa.co/data/village_green.json";
            mMap.clear();
        }
        if(position == 1){
            url = "http://nepal.piensa.co/data/riverbanks.json";
            mMap.clear();
        }
        if(position == 2){
            url = "http://nepal.piensa.co/data/medical_point.json";
            mMap.clear();
        }
        new JSONParse().execute();
        //String url = ;
        //String url = "http://nepal.piensa.co/data/riverbanks.json";
        //String url = "http://nepal.piensa.co/data/lakes.json";
        //String url = "http://nepal.piensa.co/data/tracks.json";
        //String url = "http://nepal.piensa.co/data/farms.json";

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            String title = url.split("/")[4].replace(".json", "").replace("_", " ");
            ArrayList<Double> lats = new ArrayList<>();
            ArrayList<Double> lngs = new ArrayList<>();
            JSONArray coordinates = null, coordinatesArray = null;
            JSONObject geometry = null;
            String type = null;
            System.out.println("json inside post  " + json);
            Double lat = 0.00, lng = 0.00;

            double lon = 85.314288;
            double latt = 27.726360;
            double R = 6371;  // earth radius in km

            double radius = distance;//50; // km

            double x1 = lon - Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat)));

            double x2 = lon + Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat)));

            double y1 = latt + Math.toDegrees(radius/R);

            double y2 = latt - Math.toDegrees(radius/R);

            try {
                // Getting JSON Array
                if (json != null) {
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
                                mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                System.out.println("point lat,long  " + lat + "," + lng);
                                break;
                            case "Polygon":
                                PolygonOptions rectOptions = null;
                                ArrayList<LatLng> val = new ArrayList<>();
                                lats = new ArrayList<>();
                                lngs = new ArrayList<>();
                                coordinatesArray = coordinates.getJSONArray(0);
                                for (int j = 0; j < coordinatesArray.length(); j++) {
                                    lng = coordinatesArray.getJSONArray(j).getDouble(0);
                                    lat = coordinatesArray.getJSONArray(j).getDouble(1);
                                    System.out.println("lat = " + lng);
                                    System.out.println("x1 = " + x1);
                                    System.out.println("x2 = " + x2);

                                    System.out.println("lat = " + lat);
                                    System.out.println("y1 = " + y1);
                                    System.out.println("y2 = " + y2);
                                    if((x1<=lng && lng<=x2) && (y2<=lat && lat<=y1)){
                                        System.out.println("polygon lat,long  " + lat + "," + lng);
                                        val.add(new LatLng(lat, lng));
                                        lats.add(lat);
                                        lngs.add(lng);
                                    }

                                }
                                if(val.size()>0) {
                                    Double area = area(lats, lngs);
                                    System.out.println("area111111 = " + area);
                                    rectOptions = new PolygonOptions().addAll(val).fillColor(0x4F00FF00)
                                            .strokeColor(0x2F00FF00);
                                    Polygon polygon = mMap.addPolygon(rectOptions);
                                    System.out.println("title =111111 " + title);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lats.get(0), lngs.get(0))).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setSnippet("area is " + area.toString() + " sq Ft");
                                }
                                break;

                            case "default":
                                System.out.println(" miss match type " + type);
                                break;
                        }
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
                if (addressList.size() > 0) {
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

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(27.692122, 85.319822))      // Sets the center of the map to location user
                .zoom(12)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
//        mMap.addMarker(new MarkerOptions().position(new LatLng(27.707845, 85.314689)).title("Rani Pokhari").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(27.749986, 85.261760)).title("Chhatre Deurali").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Grazing Land"));
//        final LatLng KATHMANDU = new LatLng(27.749986, 85.261760);
//        Marker kathmandu = mMap.addMarker(new MarkerOptions()
//                .position(KATHMANDU)
//                .title("Kathmandu"));
//        kathmandu.showInfoWindow();




// Instantiates a new Polygon object and adds points to define a rectangle
//        PolygonOptions rectOptions = new PolygonOptions()
//                .add(new LatLng(27.769126, 85.268626),
//                        new LatLng(27.773476, 85.255464),
//                        new LatLng(27.762188, 85.245379),
//                        new LatLng(27.757194, 85.235981),
//                        new LatLng(27.750918, 85.218607),
//                        new LatLng(27.728072, 85.276822),
//                        new LatLng(27.735364, 85.301262))
//                .fillColor(0x4F00FF00)
//                .strokeColor(0x2F00FF00);
////        polygon.setFillColor(0x7F00FF00);
//
//// Get back the mutable Polygon
//        Polygon polygon = mMap.addPolygon(rectOptions);



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
    private double area(ArrayList<Double> lats,ArrayList<Double> lons)
    {
        double sum=0;
        double prevcolat=0;
        double prevaz=0;
        double colat0=0;
        double az0=0;
        for (int i=0;i<lats.size();i++)
        {
            double colat=2*Math.atan2(Math.sqrt(Math.pow(Math.sin(lats.get(i)*Math.PI/180/2), 2)+ Math.cos(lats.get(i)*Math.PI/180)*Math.pow(Math.sin(lons.get(i)*Math.PI/180/2), 2)),Math.sqrt(1-  Math.pow(Math.sin(lats.get(i)*Math.PI/180/2), 2)- Math.cos(lats.get(i)*Math.PI/180)*Math.pow(Math.sin(lons.get(i)*Math.PI/180/2), 2)));
            double az=0;
            if (lats.get(i)>=90)
            {
                az=0;
            }
            else if (lats.get(i)<=-90)
            {
                az=Math.PI;
            }
            else
            {
                az=Math.atan2(Math.cos(lats.get(i)*Math.PI/180) * Math.sin(lons.get(i)*Math.PI/180),Math.sin(lats.get(i)*Math.PI/180))% (2*Math.PI);
            }
            if(i==0)
            {
                colat0=colat;
                az0=az;
            }
            if(i>0 && i<lats.size())
            {
                sum=sum+(1-Math.cos(prevcolat  + (colat-prevcolat)/2))*Math.PI*((Math.abs(az-prevaz)/Math.PI)-2*Math.ceil(((Math.abs(az-prevaz)/Math.PI)-1)/2))* Math.signum(az-prevaz);
            }
            prevcolat=colat;
            prevaz=az;
        }
        sum=sum+(1-Math.cos(prevcolat  + (colat0-prevcolat)/2))*(az0-prevaz);
        return 5.10072E14* Math.min(Math.abs(sum)/4/Math.PI,1-Math.abs(sum)/4/Math.PI);
    }
}
