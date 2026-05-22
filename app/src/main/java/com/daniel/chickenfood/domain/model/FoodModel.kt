package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FoodModel (
    @SerializedName("bestFood")val bestFood: Boolean = true,
    @SerializedName("categoryId")val categoryId: String = "0",
    @SerializedName("description")val description: String = "",
    @SerializedName("imagePath")val imagePath: String = "",
    @SerializedName("locationId")val locationId: Int = 0,
    @SerializedName("price")val price: Int = 0,
    @SerializedName("priceId")val priceId: Int = 0,
    @SerializedName("star")val star: Double = 0.0,
    @SerializedName("timeId")val timeId: Int = 0,
    @SerializedName("timeValue")val timeValue: Int = 0,
    @SerializedName("title")val title: String = "",
)