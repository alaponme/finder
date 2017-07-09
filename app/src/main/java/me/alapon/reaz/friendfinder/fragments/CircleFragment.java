package me.alapon.reaz.friendfinder.fragments;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.alapon.reaz.friendfinder.R;
import me.alapon.reaz.friendfinder.activity.LoginActivity;
import me.alapon.reaz.friendfinder.activity.MainActivity;
import me.alapon.reaz.friendfinder.model.GeneralResponse;
import me.alapon.reaz.friendfinder.model.circleResult;
import me.alapon.reaz.friendfinder.model.getCircles;
import me.alapon.reaz.friendfinder.services.ApiService;
import me.alapon.reaz.friendfinder.services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CircleFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "From Circle";
    ListView listView;
    Button joinBtn, createBtn;
    SharedPreferences sharedPref;
    String userToken;




   LayoutInflater inflater = null;

    public CircleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_circle, container, false);

        //send the inflater to global
        this.inflater = inflater;

        //get token from shared pref
        sharedPref =  getActivity().getSharedPreferences(MainActivity.UserPref, MODE_PRIVATE);
        userToken = sharedPref.getString(MainActivity.UserToken, null);


        joinBtn = (Button) v.findViewById(R.id.joinBtn);
        createBtn = (Button) v.findViewById(R.id.createBtn);

       try{
           joinBtn.setOnClickListener(this);
           createBtn.setOnClickListener(this);

       }catch (Exception e){

       }

         listView = (ListView) v.findViewById(R.id.circle_list_view);
        getCircles();

        return v;
    }


    public void getCircles(){

        ApiService apiService = RetrofitClient.getRetrofitClient().create(ApiService.class);
        Call<getCircles> call =apiService.getCircle("api.php?action=allCircle&token="+ userToken);
        call.enqueue(new Callback<getCircles>() {
            @Override
            public void onResponse(Call<getCircles> call, Response<getCircles> response) {

                getCircles res = response.body();
                boolean status = res.getStatus();

                if(status){
                  List<circleResult> result  = res.getResult();

                    ArrayList<String> name = new ArrayList<String>();

                    for (circleResult x : result){
                        name.add(x.getCircleId());
                      ///  Toast.makeText(getActivity(), ""+x.getCircleId(), Toast.LENGTH_SHORT).show();
                    }
                    ///Set Adapter
                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, name);
                    //set adapter to listview
                    listView.setAdapter(arrayAdapter);


                    boolean isCircleIdExits = sharedPref.contains(MainActivity.UserCircleID);

                    if(!isCircleIdExits){
                        ///  listView.setItemChecked(0, true);
                    }
                    else{
                        String circleId = sharedPref.getString(MainActivity.UserCircleID, null);
                        listView.setItemChecked(Integer.parseInt(circleId), true);
                    }


                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            // i = current position, so adapter.getItem(i) gets the current iteam, and convert it to stirg we have the iteam
                            String circleName = String.valueOf(adapterView.getItemAtPosition(i));

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(MainActivity.UserCircle, circleName);
                            editor.putString(MainActivity. UserCircleID, String.valueOf(i));

                            editor.commit();

                        }
                    });

                }
                else{

                    Toast.makeText(getActivity(), "Found Nothing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<getCircles> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        View mView;
        AlertDialog.Builder alertDialogBuilderUserInput;
        AlertDialog alertDialogAndroid;

        switch (v.getId()){

            case R.id.joinBtn :

                mView = inflater.inflate(R.layout.user_input_dialog_box, null);
                alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here

                                String circleId = userInputDialogEditText.getText().toString();


                                if (circleId.isEmpty()) {
                                    Toast.makeText(getActivity(), "Should not be Empty", Toast.LENGTH_SHORT).show();
                                } else {

                                   joinCircle(circleId);
                                }

                                ///Toast.makeText(getActivity(), "hi "+circleId, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "text "+userInputDialogEditText.getText().toString());
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();

                break;

            case R.id.createBtn :


                mView = inflater.inflate(R.layout.user_create_circle_box, null);
                alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
                alertDialogBuilderUserInput.setView(mView);

                final EditText circleET = (EditText) mView.findViewById(R.id.userInputDialog);


                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here

                                String circle = circleET.getText().toString();


                                if (circle.isEmpty()) {
                                    Toast.makeText(getActivity(), "Should not be Empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    createCircle(circle);
                                }
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                        alertDialogAndroid = alertDialogBuilderUserInput.create();
                        alertDialogAndroid.show();


                                break;
        }


    }


    public void joinCircle(String circleId) {
        ApiService api = RetrofitClient.getRetrofitClient().create(ApiService.class) ;

        Long tsLong = System.currentTimeMillis()/1000;
        String time = tsLong.toString();

        Toast.makeText(getActivity(), ""+time, Toast.LENGTH_SHORT).show();

        Call<GeneralResponse> call = api.joinCircle(userToken, circleId, time);

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                GeneralResponse res = response.body();
                boolean status = res.getStatus();
                if(status){
                    Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();

                    getCircles();


                }
                else{
                    Log.d(TAG, "Error: "+res.getMessage());
                    Toast.makeText(getActivity(), ""+res.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                Log.d("Error", t.getMessage());
                Toast.makeText(getActivity(), "Connection to the server failed", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void createCircle(String circle) {


        ApiService api = RetrofitClient.getRetrofitClient().create(ApiService.class) ;

        Long tsLong = System.currentTimeMillis()/1000;
        String time = tsLong.toString();

        Toast.makeText(getActivity(), ""+time, Toast.LENGTH_SHORT).show();

        Call<GeneralResponse> call = api.createCircle(circle,userToken,time);

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                GeneralResponse res = response.body();
                boolean status = res.getStatus();
                if(status){
                    Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "Error: "+res.getMessage());
                    Toast.makeText(getActivity(), ""+res.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                Log.d("Error", t.getMessage());
                Toast.makeText(getActivity(), "Connection to the server failed", Toast.LENGTH_LONG).show();

            }
        });

    }
}
