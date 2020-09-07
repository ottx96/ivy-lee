package de.ott.ivy

import de.ott.ivy.data.IvyLeeTask

interface TaskExtension {

    fun execute(task: IvyLeeTask)

}