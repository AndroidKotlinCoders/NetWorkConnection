package com.kotlincoders.android.networkconnection;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by root on 1/30/18.
 */

interface RetrofitAPI {

    @GET("/repositories")
    Call<ArrayList<Repository>> retrieveRepositories();

}
