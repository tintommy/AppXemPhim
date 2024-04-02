package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Country(
    @SerializedName("countryId")
    val countryId: Int,
    @SerializedName("name")
    val name: String
):Serializable