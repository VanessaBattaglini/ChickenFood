package com.daniel.chickenfood.helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import com.daniel.chickenfood.domain.model.FoodModel
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Arrays
import kotlin.jvm.java

class MyDB(appContext: Context) {

    private var preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(appContext)

    private var DEFAULT_APP_IMAGEDATA_DIRECTORY: String? = null
    private var lastImagePath: String = ""

    companion object {

        fun isExternalStorageWritable(): Boolean {
            return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        }

        fun isExternalStorageReadable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state ||
                    Environment.MEDIA_MOUNTED_READ_ONLY == state
        }
    }

    fun getImage(path: String?): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getSavedImagePath(): String {
        return lastImagePath
    }

    fun putImage(folder: String?, imageName: String?, bitmap: Bitmap?): String? {

        if (folder == null || imageName == null || bitmap == null)
            return null

        DEFAULT_APP_IMAGEDATA_DIRECTORY = folder

        val fullPath = setupFullPath(imageName)

        if (fullPath.isNotEmpty()) {
            lastImagePath = fullPath
            saveBitmap(fullPath, bitmap)
        }

        return fullPath
    }

    fun putImageWithFullPath(fullPath: String?, bitmap: Bitmap?): Boolean {
        return !(fullPath == null || bitmap == null) && saveBitmap(fullPath, bitmap)
    }

    private fun setupFullPath(imageName: String): String {

        val folder = File(
            Environment.getExternalStorageDirectory(),
            DEFAULT_APP_IMAGEDATA_DIRECTORY
        )

        if (isExternalStorageReadable() && isExternalStorageWritable() && !folder.exists()) {
            if (!folder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder")
                return ""
            }
        }

        return folder.path + '/' + imageName
    }

    private fun saveBitmap(fullPath: String?, bitmap: Bitmap?): Boolean {

        if (fullPath == null || bitmap == null)
            return false

        var fileCreated = false
        var bitmapCompressed = false
        var streamClosed = false

        val imageFile = File(fullPath)

        if (imageFile.exists())
            if (!imageFile.delete())
                return false

        try {
            fileCreated = imageFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null

        try {
            out = FileOutputStream(imageFile)
            bitmapCompressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

        } catch (e: Exception) {
            e.printStackTrace()
            bitmapCompressed = false

        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                    streamClosed = true

                } catch (e: IOException) {
                    e.printStackTrace()
                    streamClosed = false
                }
            }
        }

        return fileCreated && bitmapCompressed && streamClosed
    }

    fun getInt(key: String): Int {
        return preferences.getInt(key, 0)
    }

    fun getString(key: String): String? {
        return preferences.getString(key, "")
    }

    fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    fun getFloat(key: String): Float {
        return preferences.getFloat(key, 0f)
    }

    fun getLong(key: String): Long {
        return preferences.getLong(key, 0)
    }

    fun getDouble(key: String): Double {
        val number = getString(key)

        return try {
            number!!.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun putInt(key: String, value: Int) {
        checkForNullKey(key)
        preferences.edit().putInt(key, value).apply()
    }

    fun putString(key: String, value: String) {
        checkForNullKey(key)
        checkForNullValue(value)
        preferences.edit().putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        checkForNullKey(key)
        preferences.edit().putBoolean(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        checkForNullKey(key)
        preferences.edit().putFloat(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        checkForNullKey(key)
        preferences.edit().putLong(key, value).apply()
    }

    fun putDouble(key: String, value: Double) {
        checkForNullKey(key)
        putString(key, value.toString())
    }

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun getAll(): Map<String, *> {
        return preferences.all
    }

    fun putListObject(key: String, playerList: ArrayList<FoodModel>) {

        checkForNullKey(key)

        val gson = Gson()
        val objStrings = ArrayList<String>()

        for (player in playerList) {
            objStrings.add(gson.toJson(player))
        }

        putListString(key, objStrings)
    }

    fun getListObject(key: String): ArrayList<FoodModel> {

        val gson = Gson()

        val objStrings = getListString(key)

        val playerList = ArrayList<FoodModel>()

        for (jObjString in objStrings) {

            val player = gson.fromJson(jObjString, FoodModel::class.java)

            playerList.add(player)
        }

        return playerList
    }

    fun putListString(key: String, stringList: ArrayList<String>) {

        checkForNullKey(key)

        val myStringList = stringList.toTypedArray()

        preferences.edit()
            .putString(key, TextUtils.join("‚‗‚", myStringList))
            .apply()
    }

    fun getListString(key: String): ArrayList<String> {

        val myList = TextUtils.split(
            preferences.getString(key, ""),
            "‚‗‚"
        )

        return ArrayList(Arrays.asList(*myList))
    }

    private fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }

    private fun checkForNullValue(value: String?) {
        if (value == null) {
            throw NullPointerException()
        }
    }
}