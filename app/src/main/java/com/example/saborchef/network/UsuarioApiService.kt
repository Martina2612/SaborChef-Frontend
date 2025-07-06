package com.example.saborchef.network

import com.example.saborchef.model.PerfilUsuarioDTO
import com.example.saborchef.models.AlumnoActualizarDTO
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface UsuarioApiService {

    /**
     * Obtiene el perfil completo de un usuario
     * GET /api/usuarios/perfil/{userId}
     */
    @GET("usuarios/perfil/{userId}")
    suspend fun obtenerPerfil(@Path("userId") userId: Long): Response<PerfilUsuarioDTO>

    /**
     * Actualiza el perfil de un usuario
     * PUT /api/usuarios/perfil/{userId}
     */
    @PUT("usuarios/perfil/{userId}")
    suspend fun actualizarPerfil(
        @Path("userId") userId: Long,
        @Body perfil: PerfilUsuarioDTO
    ): Response<PerfilUsuarioDTO>

    /**
     * Subir foto de perfil CON TOKEN
     * POST /api/usuarios/perfil/{userId}/foto
     */
    @Multipart
    @POST("usuarios/perfil/{userId}/foto")
    suspend fun subirFotoPerfil(
        @Path("userId") userId: Long,
        @Part foto: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<Map<String, String>>

    @POST("usuarios/{userId}/convertir-alumno")
    suspend fun convertirEnAlumno(
        @Path("userId") userId: Int,
        @Body alumnoDto: AlumnoActualizarDTO,
        @Header("Authorization") authorization: String
    ): Response<Unit>
}