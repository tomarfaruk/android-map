package com.example.mymaps.models

import java.io.Serializable

data class UserMap(val title: String, val place: List<Place>) : Serializable
