package de.ott.ivy.ext.github.config

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.io.File

@JsonSerialize
data class Credentials(val user: String, val oAuthToken: String){
    companion object{
        fun fromJson(json: String): Credentials {
            TODO("unmarshal")
        }
        fun fromJson(json: File): Credentials? {
            return null
        }
        fun toJson(json: String): Credentials {
            TODO("marshal")
        }
        fun toJson(json: File): Credentials? {
            TODO("marshal")
        }
    }
}