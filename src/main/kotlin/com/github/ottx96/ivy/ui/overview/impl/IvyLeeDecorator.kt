package com.github.ottx96.ivy.ui.overview.impl

import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.ui.overview.IvyLee
import com.github.ottx96.ivy.ui.overview.IvyLeeDecorable
import com.github.ottx96.ivy.ui.overview.impl.base.IvyLeeDecoratorBase
import com.github.ottx96.ivy.ui.overview.threading.SyncRunnable
import javafx.scene.layout.BorderPane
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
open class IvyLeeDecorator(private val base: IvyLee): IvyLeeDecoratorBase(base), IvyLeeDecorable {

    override fun initializeUI() {
        initializeUIElements()
        val oldTasks: List<IvyLeeTask> = populateTasks()
        base.taskList.children.removeAll { it is BorderPane }

        oldTasks.forEach { createTaskCell(oldTasks, it) }
        setInitialSize()
    }

    override fun startBackgroundTasks() {
        Thread(SyncRunnable(IvyLee.remoteFilesHandler)).apply {
            isDaemon = true
            start()
        }
    }

}