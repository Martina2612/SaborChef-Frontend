/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.example.saborchef.models

import com.example.saborchef.models.Contenido

import com.google.gson.annotations.SerializedName

/**
 * 
 *
 * @param nroPaso 
 * @param texto 
 * @param contenidos 
 */


data class PasoDetalle (

    @SerializedName("nroPaso")
    val nroPaso: kotlin.Int? = null,

    @SerializedName("texto")
    val texto: kotlin.String? = null,

    @SerializedName("contenidos")
    val contenidos: kotlin.collections.List<Contenido>? = null

) {


}

