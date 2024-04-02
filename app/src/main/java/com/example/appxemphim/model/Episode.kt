package com.example.appxemphim.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Episode(
    @SerializedName("daySubmit")
    val daySubmit: String,
    @SerializedName("episode")
    val episode: Int,
    @SerializedName("episodeId")
    val episodeId: Int,
    @SerializedName("link")
    val link: String,
    @SerializedName("movieId")
    val movieId: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("season")
    val season: String
):Serializable