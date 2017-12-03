package com.blackangel.baskettogether.user.service;

import com.blackangel.baskettogether.user.domain.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public interface UserService {

    @GET("/bt/user/login")
    Call<ResponseBody> getUserSession();

    @FormUrlEncoded
    @POST("/bt/user/login")
    Call<User> login(@Field("userId") String userId, @Field("password") String password);


    @FormUrlEncoded
    @POST("/bt/user/signup")
    Call<User> signUp(@Field("userId") String userId,
                      @Field("password") String password,
                      @Field("nickname") String nickname,
                      @Field("regType") int regType);
}
