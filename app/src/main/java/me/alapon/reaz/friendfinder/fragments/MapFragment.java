package me.alapon.reaz.friendfinder.fragments;


import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.MODE_PRIVATE;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import me.alapon.reaz.friendfinder.activity.MainActivity;
import me.alapon.reaz.friendfinder.model.CircleMembersResponse;
import me.alapon.reaz.friendfinder.model.CircleMembersResponseResult;
import me.alapon.reaz.friendfinder.model.LoginResponse;
import me.alapon.reaz.friendfinder.services.ApiService;
import me.alapon.reaz.friendfinder.services.FriendFinderService;
import me.alapon.reaz.friendfinder.services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import me.alapon.reaz.friendfinder.R;

/**
 * A simple {@link Fragment} subclass.
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "From Map";

    ///Repeat a task with a time delay?
    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler; /////Run Reapted task http://stackoverflow.com/questions/6242268/repeat-a-task-with-a-time-delay/6242292#6242292

    String circleCode ;

    MapView mMapView;
    private GoogleMap mMap;
    Marker userMarker, marker;

    double latitude;
    double longitude;

    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    SharedPreferences sharedPref;
    LatLng  userLatlang;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        ////mMapView = (MapView) v.findViewById(R.id.mapView);
        sharedPref =  getActivity().getSharedPreferences(MainActivity.UserPref, MODE_PRIVATE);

        circleCode = sharedPref.getString(MainActivity.UserCircle, null);;

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap x) {
                mMap = x;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                ///Toast.makeText(this, ""+latitude, Toast.LENGTH_LONG).show();

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }

            }
        });


        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            getActivity().finish();
        } else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }


        ///Service
        Intent i= new Intent(getActivity(), FriendFinderService.class);
        i.putExtra("token", "Value to be used by the service");
        getActivity().startService(i);




                mHandler = new Handler();
                startRepeatingTask();


        return v;
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "OnCannectec Called");

        /////Requestion Update Location
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30*1000);
        mLocationRequest.setFastestInterval(30*1000);

        //Checking permission
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //if we have permission
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        //Getting last Location
        Location mLocation;
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        //Set user marker at map load
        if (mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            addMarkerforUser(latLng);
            Log.d(TAG,  "Lati "+mLocation.getLatitude()+"\n Logi "+ mLocation.getLongitude() );
            /////Requestion Update Location
        }
        else{

            if(sharedPref.contains(MainActivity.UserLati) && sharedPref.contains(MainActivity.UserLongi)){

                String LastLati = sharedPref.getString(MainActivity.UserLati, null);
                String LastLongi = sharedPref.getString(MainActivity.UserLongi, null);

                Double mLastLati = Double.valueOf(LastLati);
                Double mLastLongi = Double.valueOf(LastLongi);

                LatLng latLng = new LatLng(mLastLati, mLastLongi);
                addMarkerforUser(latLng);
            }


            Toast.makeText(getActivity(), "Location Null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("Error", "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
/*
        Log.d(TAG, "location changed entered");

        ////  addTargetLocation();
*//*
        if(userMarker.isInfoWindowShown())
            userMarker.remove();
*//*

        try {
            //Place current location marker
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //set latlang for map draw
            userLatlang = latLng;


           //// addMarkerforUser(latLng);


        } catch (Exception e) {

        }*/



    }

    public void GetCircleMembers(){

        //get token from shared pref

        String userToken = sharedPref.getString(MainActivity.UserToken, null);

     ////   mMap.clear();
        String circle_id = circleCode;
        ///Set the retrofit client then connect with interface
        ApiService apiService = RetrofitClient.getRetrofitClient().create(ApiService.class);
        Log.d(TAG, "Circle Id : "+circle_id);
        ///set user,pass to interface
        Call<CircleMembersResponse> responseCall = apiService.getCircleMembers("api.php?action=circleMember&circle="+ circle_id + "&token="+userToken);

        responseCall.enqueue(new Callback<CircleMembersResponse>() {
            @Override
            public void onResponse(Call<CircleMembersResponse> call, Response<CircleMembersResponse> response) {

                CircleMembersResponse res = response.body();

                boolean status = res.getStatus();
                if(status){
                    List<CircleMembersResponseResult>  circleMembersResponseResult = res.getResult();

                    for(CircleMembersResponseResult x : circleMembersResponseResult){

                        String name = x.getUser();
                        String lati = x.getLati();
                        String longi = x.getLongi();
                        String time = x.getTimestamp();

                        //add to map

                        try{
                            AddCircleMembersOnMap(name,lati,longi,time);
                        }catch (Exception e){
                            Log.d(TAG, "Circle Member Found Error "+ e.getMessage());
                        }
                        Log.d(TAG, "Circle Member Found : "+ name);


                    }

                    Log.d(TAG, "Success : Collection of cirlce member");
                }
                else{

                    Log.d(TAG, "Failed : Collection of cirlce member");
                }
            }

            @Override
            public void onFailure(Call<CircleMembersResponse> call, Throwable t) {

                Log.d(TAG, "Failed : Connection to Server"+ t.getMessage());

            }
        });

    }

    public void AddCircleMembersOnMap( String name, String lati, String longi,String time) {

        Double  mPlaceLati = Double.parseDouble(lati);
        Double  mPlaceLongi = Double.parseDouble(longi);


        Long serverTime  = Long.valueOf(time);
        Long systemTime = System.currentTimeMillis()/1000;

        Long diff = (systemTime - serverTime);

        Long diffMinute = diff / 60 ;
        Long diffHour = diffMinute / 60;
        Long diffDay = diffHour / 24;

      ///  Log.d(TAG, "Time difference"+ " Name:"+name +" S:"+diffSeconds +" M:"+ diffMinutes +" H:"+diffHours+" D:"+diffDays);
        Log.d(TAG, "Time difference "+name+" M:"+diffMinute+" H:"+diffHour+" D:"+diffDay);

     ///   Toast.makeText(getActivity(), "Time difference "+name+" M:"+diffMinute+" H:"+diffHour+" D:"+diffDay, Toast.LENGTH_SHORT).show();
        //set target location for map draw
        ///targetLatLng = new LatLng(mPlaceLati, mPlaceLongi);

        String selectdTime = "";
        if(diffDay > 0)
            selectdTime = String.valueOf(diffDay) + " Day Ago";
        else if (diffHour > 0)
            selectdTime = String.valueOf(diffHour) + " Hour Ago";
        else if (diffMinute > 0)
            selectdTime = String.valueOf(diffMinute) + " Minute Ago";
        else if (diff > 0)
            selectdTime = String.valueOf(diff) + " Second Ago";


        try {
            ///mMap.clear();
            LatLng latLng = new LatLng(mPlaceLati, mPlaceLongi);

            //For focus camera on my location
            if(name.equals("My Location")){

                //Store the last lati longi of user for re use when the map first load
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(MainActivity.UserLati, String.valueOf(mPlaceLati));
                editor.putString(MainActivity.UserLongi, String.valueOf(mPlaceLongi));
                editor.commit();


                 marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet("Updated: "+selectdTime)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                marker.showInfoWindow(); //show marker title withour click
            }
            else{
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                      .snippet("Updated: "+selectdTime)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }


 /*           MarkerOptions markerOptions = new MarkerOptions();
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title to the Marker
            markerOptions.title(name);
            // Adding Marker to the Camera.
            Marker m = mMap.addMarker(markerOptions);
           /// m.isInfoWindowShown();
            // Adding colour to the marker
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            // move map camera*/
/*
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLang));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));*/

        } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
        }

    }




    public void addMarkerforUser(LatLng latLng) {

        if(userMarker != null )
            userMarker.remove();

        userMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("My Location0")
                /// .snippet("Distance \n"+totalDistanceBetweenTwoPosition)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        userMarker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));
        Log.d("onLocationChanged", "Exit");
    }


    //////////////////////////////////////////////////////////////////////////
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //////////////Asking user to ger permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this.getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        ////  mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                if(mMap != null)
                    mMap.clear();

                if(userMarker != null)
                    userMarker.remove();

                GetCircleMembers(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };


    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

}
