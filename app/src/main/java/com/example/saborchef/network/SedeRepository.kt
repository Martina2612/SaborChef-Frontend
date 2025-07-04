package com.example.saborchef.network

import com.example.saborchef.model.Sede
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface SedeApi{
    @GET("api/sedes/{id}")
    suspend fun obtenerSedePorId(@Path("id") id: Long): Response<Sede>

}
class SedeRepository {
    private val api: SedeApi = Retrofit.Builder()
        .baseUrl("https://saborchef-backend-production.up.railway.app/") // ← localhost para emulador Android
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SedeApi::class.java)

    suspend fun getSede(id: Long): Sede? {
        return try {
            val response = api.obtenerSedePorId(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
