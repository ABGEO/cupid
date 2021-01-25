package dev.abgeo.cupid.entity

data class User(
        var id: String? = "",
        var name: String? = "",
        var email: String? = "",
        var gender: Int = 0,
        var interestedIn: Int = 0,
        var about: String? = "",
        var age: Int? = null,
        var school: Int = -1,
)
