package com.daniel.chickenfood.data.repository

import android.util.Log
import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.domain.reposity.MainRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val TAG = "MainRepositoryImpl"

class MainRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : MainRepository {

    override fun loadBanner(): Flow<List<BannerModel>> = callbackFlow {
        val ref = database.getReference("banners")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "Banner snapshot received: ${snapshot.childrenCount} children")
                    val banners = snapshot.children.mapNotNull { child ->
                        Log.d(TAG, "Banner child data: ${child.value}")
                        val json = gson.toJson(child.value)
                        Log.d(TAG, "Banner JSON: $json")
                        val banner = gson.fromJson(json, BannerModel::class.java)
                        Log.d(TAG, "Parsed banner: $banner")
                        banner
                    }
                    Log.d(TAG, "Sending ${banners.size} banners")
                    trySend(banners).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in banner onDataChange", e)
                    close(e)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Banner query cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun loadCategory(): Flow<List<CategoryModel>> = callbackFlow {
        val ref = database.getReference("category")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "Category snapshot received: ${snapshot.childrenCount} children")
                    val categories = snapshot.children.mapNotNull { child ->
                        Log.d(TAG, "Category child data: ${child.value}")
                        val json = gson.toJson(child.value)
                        Log.d(TAG, "Category JSON: $json")
                        val category = gson.fromJson(json, CategoryModel::class.java)
                        Log.d(TAG, "Parsed category: $category")
                        category
                    }
                    Log.d(TAG, "Sending ${categories.size} categories")
                    trySend(categories).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in category onDataChange", e)
                    close(e)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Category query cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun loadFiltered(
        categoryId: String
    ): Flow<List<FoodModel>> = callbackFlow {
        try {
            val categoryIdInt = categoryId.toIntOrNull() ?: 0
            Log.d(TAG, "Querying foods for categoryId: $categoryIdInt (from string: $categoryId)")
            val query = database
                .getReference("foods")
                .orderByChild("categoryId")
                .equalTo(categoryIdInt.toDouble())
            val listener = object : ValueEventListener {
                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {
                    try {
                        Log.d(TAG, "Food snapshot received for category $categoryId: ${snapshot.childrenCount} children")
                        val foods = snapshot.children.mapNotNull { child ->
                            try {
                                Log.d(TAG, "Food child data: ${child.value}")
                                // Usa Gson en lugar de getValue() para evitar problemas de tipo
                                val json = gson.toJson(child.value)
                                Log.d(TAG, "Food JSON: $json")
                                val food = gson.fromJson(json, FoodModel::class.java)
                                Log.d(TAG, "Parsed food: $food")
                                food
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing individual food", e)
                                null
                            }
                        }
                        Log.d(TAG, "Sending ${foods.size} foods")
                        trySend(foods)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in food onDataChange", e)
                        close(e)
                    }
                }
                override fun onCancelled(
                    error: DatabaseError
                ) {
                    Log.e(TAG, "Food query cancelled: ${error.message}")
                    close(error.toException())
                }
            }
            query.addValueEventListener(listener)
            awaitClose {
                query.removeEventListener(listener)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadFiltered", e)
            close(e)
        }
    }
}
