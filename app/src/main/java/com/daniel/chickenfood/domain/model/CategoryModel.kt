package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("imagePath")
    val imagePath: String = "",
    
    @SerializedName("name")
    val name: String = ""
)
