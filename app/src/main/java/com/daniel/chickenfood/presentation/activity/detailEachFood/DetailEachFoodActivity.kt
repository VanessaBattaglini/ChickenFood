package com.daniel.chickenfood.presentation.activity.detailEachFood

import android.os.Bundle
import androidx.activity.compose.setContent
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.presentation.activity.BaseActivity

class DetailEachFoodActivity : BaseActivity() {

    private lateinit var item: FoodModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = intent.getSerializableExtra("object") as FoodModel

        item.numberInCart = 1

        managmentCart = ManagmentCart(applicationContext)

        setContent {

            DetailScreen(
                item = item,
                onBackClick = { finish() },
                onAddToCartClick = {
                    managmentCart.insertItem(item)
                }
            )
        }
    }
}