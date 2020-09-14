package de.ott.ivy.config

import com.google.gson.GsonBuilder
import java.util.*

data class Configuration(val taskId: String, val language: Locale = Locale.ENGLISH, val deleteInterval: Int){

    companion object{
        val gson by lazy{
            GsonBuilder().setPrettyPrinting().create()
        }

        fun fromJson(jsonString: String): Configuration{
            return gson.fromJson(jsonString, Configuration::class.java)
        }
    }

    fun toJsonString(): String{
        return gson.toJson(this)
    }
}