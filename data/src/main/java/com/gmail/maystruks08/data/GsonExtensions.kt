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


//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val gson = Gson()
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}
