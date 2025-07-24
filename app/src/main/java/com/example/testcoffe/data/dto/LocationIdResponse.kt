package com.example.testcoffe.data.dto

import com.google.gson.annotations.SerializedName

data class LocationIdResponse(
    val id: Int,
    val name: String,
    @SerializedName("imageURL")
    val imageUrl: String,
    val price: Int
)
