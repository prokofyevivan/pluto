package com.sampleapp.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @Headers("Authorization: Bearer e42ed4a6-f346-4f0b-ad49-02c66dcca91a")
    @POST("post?scope=points,preferences")
    suspend fun form(@Field("title") title: String, @Field("diff") diff: String): JsonObject

    @Headers("Authorization: Bearer e42ed4a6-f346-4f0b-ad49-02c66dcca91a")
    @POST("https://run.mocky.io/v3/3326a4c7-370d-4f82-8b72-7a7cda85524d")
    suspend fun post(@Body hashMapOf: Any): JsonObject

    @Headers("Authorization: Bearer e42ed4a6-f346-4f0b-ad49-02c66dcca91a")
    @GET("get")
    suspend fun get(): JsonObject

    @Headers(
        "type: xml",
        "x-app-theme: light",
        "Authorization: Bearer e42ed4a6-f346-4f0b-ad49-02c66dcca91a"
    )
    @POST("xml")
    suspend fun xml(@Body hashMapOf: RequestBody): JsonObject
}
