package de.ott.ivy.example

import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask

@Extension("Example Extension")
class ExampleExtension: TaskExtension {

    override fun execute(task: IvyLeeTask) {
        task.descr = "Hello from ${javaClass.simpleName}!"
    }

}