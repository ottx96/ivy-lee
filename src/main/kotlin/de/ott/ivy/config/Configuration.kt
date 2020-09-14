package de.ott.ivy.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.ott.ivy.Entrypoint
import de.ott.ivy.gdrive.ConnectionProvider
import de.ott.ivy.gdrive.RemoteFilesHandler
import kotlinx.serialization.ExperimentalSerializationApi
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
data class Configuration(val taskId: String, val cleanInterval: Long, val timeUnit: ChronoUnit = ChronoUnit.DAYS, val language: Locale = Locale.getDefault()) {

    companion object{
        private val gson: Gson by lazy{
            GsonBuilder().setPrettyPrinting().create()
        }

        private fun fromJson(jsonString: String): Configuration {
            return gson.fromJson(jsonString, Configuration::class.java)
        }

        val instance: Configuration by lazy {
            val jsonString = Entrypoint.CONFIG_FILE.readText()
            return@lazy fromJson(jsonString)
        }
    }

    fun toJsonString(): String {
        return gson.toJson(this)
    }

}