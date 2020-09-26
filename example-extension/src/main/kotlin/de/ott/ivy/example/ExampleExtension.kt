package de.ott.ivy.example

import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask

@Extension("My Example Extension")
class ExampleExtension: TaskExtension {

    override fun execute(task: IvyLeeTask) {
        println("Hello from ${javaClass.simpleName}!")
        println("my task is: $task")
    }

}