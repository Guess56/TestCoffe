package com.example.testcoffe.domain.model

import com.google.gson.annotations.SerializedName

data class LocationId (
    val id: Int,
    val name: String,
    @SerializedName("imageURL")
    val imageUrl: String,
    val price: Int
)