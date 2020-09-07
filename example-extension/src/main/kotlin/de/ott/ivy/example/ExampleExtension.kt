package de.ott.ivy.example

import de.ott.ivy.TaskExtension
import de.ott.ivy.data.IvyLeeTask

class ExampleExtension: TaskExtension {

    override fun execute(task: IvyLeeTask) {
        task.descr = "Hello from ExampleExtension!"
    }

}