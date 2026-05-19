package com.daniel.chickenfood.data.repository

import com.daniel.chickenfood.domain.MainRepository
import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainRepositoryImpl(
    private val database: FirebaseDatabase
) : MainRepository {

    override suspend fun loadBanner(): Flow<List<BannerModel>> =
        callbackFlow {
            val ref = database.getReference("banners")
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val banners = mutableListOf<BannerModel>()
                        snapshot.children.forEach { it ->
                            val banner =
                                it.getValue(BannerModel::class.java)
                            banner?.let {
                                banners.add(it)
                            }
                        }
                        trySend(banners)
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            ref.addValueEventListener(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }
        }

    override suspend fun loadCategory(): Flow<List<CategoryModel>> =
        callbackFlow {
            val ref = database.getReference("category")
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categories = mutableListOf<CategoryModel>()
                        snapshot.children.forEach { it ->
                            val category =
                                it.getValue(CategoryModel::class.java)
                            category?.let {
                                categories.add(it)
                            }
                        }
                        trySend(categories)
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            ref.addValueEventListener(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }
        }
}