package me.alapon.reaz.friendfinder.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.alapon.reaz.friendfinder.Model.LoginResponse;
import me.alapon.reaz.friendfinder.R;
import me.alapon.reaz.friendfinder.Services.ApiService;
import me.alapon.reaz.friendfinder.Services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText usernameET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);


        /*Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);*/


    }

    public void Login(View view) {

        String username, password;


        username = usernameET.getText().toString();
        password = passwordET.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Username/Password is empty", Toast.LENGTH_SHORT).show();
        }
        else{

            ///Set the retrofit client then connect with interface
            ApiService loginApi= RetrofitClient.getRetrofitClient().create(ApiService.class);

            ///set user,pass to interface
            Call<LoginResponse> loginResponseCall=loginApi.login(username,password);

            loginResponseCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    LoginResponse res =response.body();

                    boolean status = res.getStatus();

                    if(status==true)
                    {

                        String token = res.getToken();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);

                        Log.d("id", ""+ token);

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Username/Password Incorrect", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("response", ""+ status);
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    Log.d("Error", t.getMessage());
                    Toast.makeText(LoginActivity.this, "Connection to the server failed", Toast.LENGTH_LONG).show();

                }
            });

        }

    }

    public void SignUp(View view) {

         Intent main = new Intent(LoginActivity.this, Register.class);
        startActivity(main);
    }
}
