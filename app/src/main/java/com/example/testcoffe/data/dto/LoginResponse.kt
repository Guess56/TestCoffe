package com.example.testcoffe.data.dto

import com.google.gson.annotations.SerializedName


class LoginResponse(
    val token: String,
    @SerializedName("tokenLifetime") val tokenLifeTime: Long?
)