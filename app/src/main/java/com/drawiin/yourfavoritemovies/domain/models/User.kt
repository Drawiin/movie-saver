package com.drawiin.yourfavoritemovies.domain.models

import java.util.*

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val birth: String = "11/01/2021",
    val profiles: List<Profile> = listOf()
)
