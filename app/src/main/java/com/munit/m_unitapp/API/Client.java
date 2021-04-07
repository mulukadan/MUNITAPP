package com.munit.m_unitapp.API;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * by Mulu Kadan on 20/03/2019.
 */

public class Client {
    static String username = "mulukadan";
    static String api_key = "nnkLxSWOtXA2lKfOdLlWg6J7d8pJeCuirMTEJs4E9VrjHQhlGP";
    static String sender = "SMARTLINK";

    //SMS
    public static final String MAIN_SMS_URL = "https://sms.movesms.co.ke/api/compose/";
    public static final String SMS_BAL_URL = "https://sms.movesms.co.ke/api/balance/";

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();


    //Sent SMS
    public static  Retrofit SendSMS(String Recepient, String Msg){
        String URL = MAIN_SMS_URL + "?username="+username+"&api_key="+api_key+"&sender="+sender+"&to="+Recepient+"&message="+Msg+"&msgtype=5&dlr=0";
        Retrofit retrofit = null;
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    //Sent SMS
    public static  Retrofit getSMSBal(){
        String URL = SMS_BAL_URL + "?api_key="+api_key;
        Retrofit retrofit = null;
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
