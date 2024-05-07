package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Person(
    @SerializedName("country")
    val country: Country,
    @SerializedName("countryId")
    val countryId: Any,
    @SerializedName("dayOfBirth")
    val dayOfBirth: String,
    @SerializedName("describe")
    val describe: String,
    @SerializedName("gender")
    val gender: Boolean,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("personId")
    val personId: Int
):Serializable