package com.example.saborchef.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://saborchef-backend-production.up.railway.app/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val recetaApi: RecetaApi = retrofit.create(RecetaApi::class.java)


    val usuarioApi: UsuarioApi = retrofit.create(UsuarioApi::class.java)
}

