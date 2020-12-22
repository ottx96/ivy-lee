package com.github.ottx96.ivy.crawler

import com.github.ottx96.ivy.annotation.Extension
import java.io.File

object ExtensionCrawler {

    const val PACKAGE_REGEX = """([a-z]+\.)*[a-z]*"""

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
                    // Try to create an instance of the object
                    try{
                        val c = Class.forName(fullyQualifiedClassName)
                        if(c.getAnnotation(Extension::class.java) != null){
                            println("Extension found in class: $c")
                        }
                    }catch(e: ClassNotFoundException){
                        e.printStackTrace()
                    }
                }
        }
    }
}