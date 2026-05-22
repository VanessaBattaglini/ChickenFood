package com.daniel.chickenfood.data.repository

import com.daniel.chickenfood.domain.reposity.MainRepository
import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : MainRepository {

    override fun loadBanner(): Flow<List<BannerModel>> = callbackFlow {
        val ref = database.getReference("banners")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val banners = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        gson.fromJson(json, BannerModel::class.java)
                    }
                    trySend(banners)
                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
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
                    val categories = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        gson.fromJson(json, CategoryModel::class.java)
                    }
                    trySend(categories)
                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
