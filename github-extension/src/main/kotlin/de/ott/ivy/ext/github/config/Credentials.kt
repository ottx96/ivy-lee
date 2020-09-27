package de.ott.ivy.ext.github.config

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.File
import java.nio.charset.Charset

data class Credentials(val user: String, val oAuthToken: String){
    companion object{
        fun fromJson(json: String): Credentials {
            return GsonBuilder().setPrettyPrinting().create().fromJson(json, Credentials::class.java)
        }
        fun fromJson(json: File): Credentials? {
            if(!json.exists()) return null
            return GsonBuilder().setPrettyPrinting().create().fromJson(json.inputStream().reader(charset = Charset.forName("UTF-8")), Credentials::class.java)
        }
    }

    fun toJson(): String =
        GsonBuilder().setPrettyPrinting().create().toJson(this)

    fun toJson(json: File): File {
        json.outputStream().writer(charset("UTF-8")).use {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(this))
        }
        return json
    }
}