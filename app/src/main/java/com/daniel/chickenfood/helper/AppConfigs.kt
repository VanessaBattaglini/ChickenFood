package com.daniel.chickenfood.helper

import com.daniel.chickenfood.domain.model.UserTokenModel

object AppConfigs {
    var appToken: UserTokenModel? = null

    fun saveToken(token: UserTokenModel) {
        appToken = token
    }

    fun clearToken(){
        appToken = null
    }
}