package de.ott.ivy

import de.ott.ivy.data.IvyLeeTask

abstract class TaskExtension {

    abstract fun execute(task: IvyLeeTask)

}