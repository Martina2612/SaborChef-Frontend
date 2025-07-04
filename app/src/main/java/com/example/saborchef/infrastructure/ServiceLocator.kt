package com.example.saborchef.infrastructure

import com.example.saborchef.apis.CalificacionControllerApi
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.apis.TipoRecetaControllerApi

object ServiceLocator {
    private val apiClient by lazy {
        ApiClient(baseUrl = "https://saborchef-backend-production.up.railway.app/")
        // El interceptor ya inyecta el token de SessionManager
    }

    val recetaService        by lazy { apiClient.createService(RecetaControllerApi::class.java) }
    val tipoService          by lazy { apiClient.createService(TipoRecetaControllerApi::class.java) }
    val calificacionService  by lazy { apiClient.createService(CalificacionControllerApi::class.java) }
}
