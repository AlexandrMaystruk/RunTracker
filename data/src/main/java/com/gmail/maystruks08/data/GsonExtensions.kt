package com.gmail.maystruks08.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

inline fun <reified T> Gson.fromJson(json: String): T =
        this.fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJsonOrNull(json: String?): T? {
    return if (json.isNullOrEmpty()) {
        null
    } else {
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)
    }
}

inline fun <reified T> Gson.toJsonOrNull(clazz: T?, parentType: Type? = null): String? {
    return if (clazz == null) {
        null
    } else {
        if (parentType == null) {
            this.toJson(clazz)
        } else {
            this.toJson(clazz, parentType)
        }
    }
}

