package me.alapon.reaz.friendfinder.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationListener;

import me.alapon.reaz.friendfinder.activity.LoginActivity;
import me.alapon.reaz.friendfinder.activity.MainActivity;
import me.alapon.reaz.friendfinder.activity.Register;
import me.alapon.reaz.friendfinder.model.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendFinderService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "From Service";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String token;

    public FriendFinderService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ///Get user token
        SharedPreferences prefs = getApplication().getSharedPreferences(MainActivity.UserPref, MODE_PRIVATE);
        token = prefs.getString(MainActivity.UserToken, null);
        Log.i(TAG, "Service Started" + token);
        ///Initilize api
        buildGoogleApiClient();

        return Service.START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {

        //Getting last Location
        Location mLocation;
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null){

            Double lati = mLocation.getLatitude();
            Double longi =  mLocation.getLongitude();

            //Send data to server
            SendDataToServer(lati,longi);

            Log.i(TAG, "Lati " + lati + "\n Logi " +longi);
        }

        ///  Toast.makeText(this, "Lati "+mLocation.getLatitude()+"\n Logi "+ mLocation.getLongitude(), Toast.LENGTH_SHORT).show();

        /////Requestion Update Location
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10*1000);
        mLocationRequest.setFastestInterval(10*1000);

        //Checking permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //if we have permission
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onLocationChanged(Location location) {

        Double lati = location.getLatitude();
        Double longi =  location.getLongitude();

        //Send data to server
        SendDataToServer(lati,longi);
        Log.i(TAG, ": " + lati + " " + longi);

    }


    public void SendDataToServer(Double lati, Double longi){

        Long tsLong = System.currentTimeMillis()/1000;
        String time = tsLong.toString();

        ///Set the retrofit client then connect with interface
        ApiService apiService = RetrofitClient.getRetrofitClient().create(ApiService.class);

        ///set user,pass to interface
        Call<GeneralResponse> location = apiService.insertLocation(lati,longi,token, time);

        location.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                GeneralResponse res = response.body();

                boolean status = res.getStatus();

                if(status)
                {
                    Log.i(TAG, "Location Insert Successful");
                }
                else
                {
                    Log.i(TAG, "Location Insert Failed");
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                Log.i(TAG, "Connection Failed \n"+ t.getMessage());

            }
        });

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }


}
