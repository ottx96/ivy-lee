package de.ott.ivy.crawler

import de.ott.ivy.annotation.Extension
import java.io.File

object ExtensionCrawler {

    const val PACKAGE_REGEX = """([a-z]+\.)*[a-z]*"""

    @JvmStatic
    fun main(args: Array<String>) {
        findClasses()
    }

    fun findClasses() {
        // Get a File object for the package
        val url = javaClass.classLoader.getResource("")
        println(url)
        val directory = File(url.file)

        println("Finding classes:")
        if (directory.exists()) {
            // Get the list of the files contained in the package
            directory.walk()
                .filter { f -> f.isFile && !f.name.contains('$') && f.name.endsWith(".class") }
                .forEach {
                    val fullyQualifiedClassName = it.canonicalPath.removePrefix(directory.canonicalPath)
                                .dropLast(6) // remove .class
                                .replace('/', '.')
                                .replace('\\', '.')
                                .substring(1)

                    println(fullyQualifiedClassName)
                    try {
                        // Try to create an instance of the object
                        val c = Class.forName(fullyQualifiedClassName)
                        if(c.getAnnotation(Extension::class.java) != null){
                            println("Extension found in class: $c")
                        }
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
        }
    }

}

class WrongFormatException(msg: String?): Exception(msg)