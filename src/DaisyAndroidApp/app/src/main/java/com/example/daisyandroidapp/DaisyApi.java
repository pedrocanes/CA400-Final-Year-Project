package com.example.daisyandroidapp;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DaisyApi {

    @GET("user")
    Call<DBUserList> getUsers();

    @POST("user/")
    Call<DBUser> createUser(@Body DBUser dbUser);

    @PUT("user/{id}")
    Call<DBPutResponse> updatePairPin(@Path("id") String id, @Query("pair_pin") String pairPin);

    @POST("phone/")
    Call<DBPhone> createPhone(@Body DBPhone dbPhone);

    @PUT("phone/{id}")
    Call<DBPutResponse> updatePhoneCoords(@Path("id") String id, @Query("lat_long") String gpsCoords);

    @POST("researcher/")
    Call<DBResearcher> createResearcher(@Body DBResearcher dbResearcher);

    @GET("home-assistant")
    Call<DBHomeAssistantList> getHomeAssistants();

    @POST("question/")
    Call<DBPutResponse> createQuestion(@Body DBQuestion question);

    @GET("question/{id}")
    Call<DBQuestion> getQuestionWithId(@Path("id") String id);

    @POST("answer/")
    Call<DBPutResponse> createAnswer(@Body DBAnswer answer);

    @GET("answer/question/{questionId}")
    Call<List<DBAnswer>> getAnswersWithQuestionId(@Path("questionId") String questionId);

    @POST("user-details/")
    Call<DBPutResponse> createUserDetails(@Body DBUserDetails dbUserDetails);

    @GET("user-details/user/{id}")
    Call<List<DBUserDetails>> getUserDetails(@Path("id") String id);

    @PUT("user-details/user/{user_id}")
    Call<DBPutResponse> updateUserDetails(@Path("user_id") String userId, @Query("ask_question") Boolean askQuestion, @Query("device_to_use") String deviceToUse, @Query("user_available") String userAvailable);

    @DELETE("user-details/{id}")
    Call<DBPutResponse> deleteUserDetails(@Path("id") String id);

    @POST("answer-returned/")
    Call<DBAnswerReturned> createAnswerReturned(@Body DBAnswerReturned dbAnswerReturned);

    @GET("answer-returned")
    Call<DBAnswerReturnedList> getAnswerReturned();

}
