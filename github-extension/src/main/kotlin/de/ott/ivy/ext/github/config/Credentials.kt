package de.ott.ivy.ext.github.config

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.io.File

@JsonSerialize
data class Credentials(val user: String, val oAuthToken: String){
    companion object{
        fun fromJson(json: String){
            TODO("unmarshal")
        }
        fun fromJson(json: File){
            TODO("unmarshal")
        }
        fun toJson(json: String){
            TODO("marshal")
        }
        fun toJson(json: File){
            TODO("marshal")
        }
    }
}