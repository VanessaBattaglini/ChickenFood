package com.daniel.chickenfood.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderModel(
    @SerializedName("orderId")
    val orderId: String = "",
    
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("items")
    val items: List<OrderItemModel> = emptyList(),
    
    @SerializedName("totalPrice")
    val totalPrice: Double = 0.0,
    
    @SerializedName("pointsEarned")
    val pointsEarned: Int = 0,  // 10% del total
    
    @SerializedName("orderDate")
    val orderDate: Long = System.currentTimeMillis(),
    
    @SerializedName("status")
    val status: String = "pending",  // pending, completed, cancelled
    
    @SerializedName("deliveryAddress")
    val deliveryAddress: String = "",
    
    @SerializedName("notes")
    val notes: String = ""
)

@Serializable
data class OrderItemModel(
    @SerializedName("foodId")
    val foodId: Int = 0,
    
    @SerializedName("title")
    val title: String = "",
    
    @SerializedName("price")
    val price: Double = 0.0,
    
    @SerializedName("quantity")
    val quantity: Int = 0,
    
    @SerializedName("subtotal")
    val subtotal: Double = 0.0,
    
    @SerializedName("imagePath")
    val imagePath: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        foodId = parcel.readInt(),
        title = parcel.readString() ?: "",
        price = parcel.readDouble(),
        quantity = parcel.readInt(),
        subtotal = parcel.readDouble(),
        imagePath = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(foodId)
        parcel.writeString(title)
        parcel.writeDouble(price)
        parcel.writeInt(quantity)
        parcel.writeDouble(subtotal)
        parcel.writeString(imagePath)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OrderItemModel> {
        override fun createFromParcel(parcel: Parcel) = OrderItemModel(parcel)
        override fun newArray(size: Int) = arrayOfNulls<OrderItemModel>(size)
    }
}
