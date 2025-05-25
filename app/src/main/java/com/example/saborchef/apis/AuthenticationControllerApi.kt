package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.AuthenticationRequest
import com.example.saborchef.models.AuthenticationResponse
import com.example.saborchef.models.RegisterRequest

interface AuthenticationControllerApi {
    /**
     * POST api/auth/authenticate
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authenticationRequest 
     * @return [Call]<[AuthenticationResponse]>
     */
    @POST("api/auth/authenticate")
    fun authenticate(@Body authenticationRequest: AuthenticationRequest): Call<AuthenticationResponse>

    /**
     * POST api/auth/register
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param registerRequest 
     * @return [Call]<[AuthenticationResponse]>
     */
    @POST("api/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<AuthenticationResponse>

}
