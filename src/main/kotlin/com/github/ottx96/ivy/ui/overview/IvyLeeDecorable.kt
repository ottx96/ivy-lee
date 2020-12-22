package com.github.ottx96.ivy.ui.overview

import com.github.ottx96.ivy.data.IvyLeeTask
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

interface IvyLeeDecorable {

    fun initializeUI()
    fun startBackgroundTasks()

    fun setInitialSize()
    fun initializeUIElements()
    fun setScrollSpeed()
    fun populateTasks(): List<IvyLeeTask>

    fun createTaskCell(loadedTasks: List<IvyLeeTask>, task: IvyLeeTask)
    fun createAdderHandler(): EventHandler<MouseEvent>

}
