package me.alapon.reaz.friendfinder.Services;



import me.alapon.reaz.friendfinder.Model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ApiService {


   @POST("login")
   @FormUrlEncoded
   Call<LoginResponse> login(@Field("email") String username,
                             @Field("password") String password);

/*    @GET("android/sample.php")
    Call<List<Person>> getAllPerson() ;*/


/*    @POST("reaz/android/tourmate/index.php?action=login")
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("username") String username,
                              @Field("password") String password);


    @POST("reaz/android/tourmate/index.php?action=register")
    @FormUrlEncoded
    Call<RegisterResponse> register(
            @Field("name") String name,
            @Field("username") String username,
            @Field("address") String address,
            @Field("password") String password);*/
}
