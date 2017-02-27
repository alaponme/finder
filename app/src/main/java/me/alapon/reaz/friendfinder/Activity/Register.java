package me.alapon.reaz.friendfinder.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.alapon.reaz.friendfinder.Model.GeneralResponse;
import me.alapon.reaz.friendfinder.Model.LoginResponse;
import me.alapon.reaz.friendfinder.Model.RegisterRequest;
import me.alapon.reaz.friendfinder.R;
import me.alapon.reaz.friendfinder.Services.ApiService;
import me.alapon.reaz.friendfinder.Services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    EditText usernameET, passwordET, emailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        usernameET = (EditText)findViewById(R.id.usernameET);
        passwordET = (EditText)findViewById(R.id.passwordET);
        emailET = (EditText)findViewById(R.id.emailET);
    }

    public void SignUp(View view) {

//emailEditText.setError("Invalid Email");

        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        String email = emailET.getText().toString();

        RegisterRequest reg = new RegisterRequest();

        if (username.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Username/Password is empty", Toast.LENGTH_SHORT).show();
        }

        else if(!reg.validateUsername(username))
            usernameET.setError("Username too short");

        else if(!reg.validatePassword(password))
             passwordET.setError("Password too short");

        else {

            ///Set the retrofit client then connect with interface
            ApiService apiService = RetrofitClient.getRetrofitClient().create(ApiService.class);

            ///set user,pass to interface
            Call<GeneralResponse> registerResponseCall = apiService.register(username, password, email);

            registerResponseCall.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                    GeneralResponse res = response.body();

                    boolean status = res.getStatus();

                    if(status)
                    {
                        Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Register.this, LoginActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(Register.this, "Failed : "+res.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Log.d("response", ""+ status);

                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {

                    Log.d("Error", t.getMessage());
                    Toast.makeText(Register.this, "Connection to the server failed", Toast.LENGTH_LONG).show();

                }
            });

        }
    }
    public void cancle(View view) {
        Intent intent = new Intent(Register.this, LoginActivity.class);
        startActivity(intent);

    }
}
