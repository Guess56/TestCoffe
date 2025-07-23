package com.example.testcoffe.domain.model

import com.google.gson.annotations.SerializedName

data class Login(
    val token: String,
    @SerializedName("tokenLifetime") val tokenLifeTime: Number
)