package me.alapon.reaz.friendfinder.services;



import java.util.List;

import me.alapon.reaz.friendfinder.model.CircleMembersResponse;
import me.alapon.reaz.friendfinder.model.GeneralResponse;
import me.alapon.reaz.friendfinder.model.LoginResponse;
import me.alapon.reaz.friendfinder.model.getCircles;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

import static com.google.gson.internal.bind.TypeAdapters.URI;


public interface ApiService {


   @POST("api.php?action=login")
   @FormUrlEncoded
   Call<LoginResponse> login(@Field("username") String username,
                             @Field("password") String password
         );

   @POST("api.php?action=register")
   @FormUrlEncoded
   Call<GeneralResponse> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email
         );


   @POST("api.php?action=insertLocation")
   @FormUrlEncoded
   Call<GeneralResponse> insertLocation(
           @Field("lati") Double lati,
           @Field("longi") Double longi,
           @Field("token") String token,
           @Field("time") String time
         );

    @GET
    Call<CircleMembersResponse> getCircleMembers(@Url String url) ;



    @POST("api.php?action=joinCircle")
    @FormUrlEncoded
    Call<GeneralResponse> joinCircle(
            @Field("token") String token,
            @Field("circle") String circle,
            @Field("time") String time
    );

    @POST("api.php?action=createCircle")
    @FormUrlEncoded
    Call<GeneralResponse> createCircle(
            @Field("name") String name,
            @Field("token") String token,
            @Field("time") String time
    );

    @GET
    Call<getCircles> getCircle(@Url String url) ;


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
