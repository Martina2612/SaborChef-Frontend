package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.Alumno
import com.example.saborchef.models.AlumnoActualizarDTO
import com.example.saborchef.models.ConfirmacionCodigoDTO
import com.example.saborchef.models.ResetPasswordDto
import com.example.saborchef.models.Usuario

interface UsuarioControllerApi {
    /**
     * POST api/usuarios/confirmar-codigo
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param confirmacionCodigoDTO 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/usuarios/confirmar-codigo")
    fun confirmarCuenta(@Body confirmacionCodigoDTO: ConfirmacionCodigoDTO): Call<kotlin.String>

    /**
     * POST api/usuarios/{id}/convertir-alumno
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @param alumnoActualizarDTO 
     * @return [Call]<[Alumno]>
     */
    @POST("api/usuarios/{id}/convertir-alumno")
    fun convertirEnAlumno(@Path("id") id: kotlin.Long, @Body alumnoActualizarDTO: AlumnoActualizarDTO): Call<Alumno>

    /**
     * GET api/usuarios/{id}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[Usuario]>
     */
    @GET("api/usuarios/{id}")
    fun getUsuarioById(@Path("id") id: kotlin.Long): Call<Usuario>

    /**
     * GET api/usuarios/recuperar
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param email 
     * @return [Call]<[kotlin.String]>
     */
    @GET("api/usuarios/recuperar")
    fun recuperarContrasea(@Query("email") email: kotlin.String): Call<kotlin.String>

    /**
     * POST api/usuarios/password/reset
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param resetPasswordDto 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/usuarios/password/reset")
    fun resetearContrasea(@Body resetPasswordDto: ResetPasswordDto): Call<kotlin.String>

}
