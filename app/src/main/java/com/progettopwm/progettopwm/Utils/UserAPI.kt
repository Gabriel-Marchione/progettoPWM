package com.progettopwm.progettopwm.Utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import com.google.gson.JsonObject
import retrofit2.Response

interface UserAPI{

    @POST("postSelect/")
    @FormUrlEncoded
    fun select(@Field("query") query: String): Call<JsonObject>

    @POST("postUpdate/")
    @FormUrlEncoded
    fun update(@Field("query") query: String): Call <JsonObject>

    @POST("postRemove/")
    @FormUrlEncoded
    fun remove(@Field("query") query: String): Call <JsonObject>

    @POST("postInsert/")
    @FormUrlEncoded
    fun insert(@Field("query") query: String): Call<JsonObject>

    @GET
    fun getImage(@Url url: String) : Call <ResponseBody>
}