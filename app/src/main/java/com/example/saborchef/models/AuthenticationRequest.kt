package com.example.saborchef.models

import com.google.gson.annotations.SerializedName

/**
 * Este modelo se generó con OpenAPI Generator o manualmente para que coincida
 * con el JSON que tu backend devuelve o espera en "/auth/authenticate".
 */
data class AuthenticationRequest(

    @SerializedName("alias")
    val alias: String? = null,

    @SerializedName("password")
    val password: String? = null
)
