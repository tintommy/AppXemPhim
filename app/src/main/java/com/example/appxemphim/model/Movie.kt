package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Movie(
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("country")
    val country: Country,
    @SerializedName("countryId")
    val countryId: Int,
    @SerializedName("episodeList")
    val episodeList: List<Episode>,
    @SerializedName("episodes")
    val episodes: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("movieContent")
    val movieContent: String,
    @SerializedName("movieId")
    val movieId: Int,
    @SerializedName("movieSchedule")
    val movieSchedule: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("persons")
    val persons: List<Person>,
    @SerializedName("price")
    val price: Int,
    @SerializedName("star")
    val star: Double,
    @SerializedName("status")
    val status: Int,
    @SerializedName("views")
    val views: Int
): Serializable