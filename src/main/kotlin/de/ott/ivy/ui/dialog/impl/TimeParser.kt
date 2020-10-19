package de.ott.ivy.ui.dialog.impl

import java.lang.Exception
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * TODO: Insert Description!
 * Project: ivy-lee
 * Package: de.ott.ivy.ui.dialog.impl
 * Created: 19.10.2020 16:45
 * @author = manuel.ott
 * @since = 19. Oktober 2020
 */
object TimeParser {

    private val TIME_PATTERN_STR = DateTimeFormatter.ofPattern("[H[H]'h'][' '][m[m]'m'][' '][s[s]'s']")
    private val TIME_PATTERN_INT = DateTimeFormatter.ofPattern("HH'h 'mm'm 'ss's'")

    fun parseSeconds(str: String): Int {
        var res = str
        if(!str.contains(Regex("""\d{1,2}h"""))) res = "0h $res"
        return try{
            LocalTime.parse(res, TIME_PATTERN_STR).toSecondOfDay()
        }catch (e: Exception){
            e.printStackTrace()
            0
        }
    }

    fun parseString(seconds: Int): String {
        return try{
            LocalTime.ofSecondOfDay(seconds.toLong()).format(TIME_PATTERN_INT)
        }catch (e: Exception){
            e.printStackTrace()
            "0h 0m 0s"
        }
    }

}