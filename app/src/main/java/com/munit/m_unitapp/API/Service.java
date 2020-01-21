package com.munit.m_unitapp.API;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Mulu Kadan on 05/06/2019.
 */

public interface Service {
    //Post Request & Get Response AS String
    @POST(" ")
    Call<String>postRequest(@Body RequestBody Body);

    //Get getRequestResponse AS String
    @GET(" ")
    Call<String> getRequestResponse();
}
