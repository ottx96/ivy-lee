package com.github.ottx96.ivy.ui.overview.impl

import com.github.ottx96.ivy.ui.overview.IvyLee
import com.github.ottx96.ivy.ui.overview.IvyLeeDecorable
import com.github.ottx96.ivy.ui.overview.impl.base.IvyLeeDecoratorBase
import com.github.ottx96.ivy.ui.overview.threading.SyncRunnable
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
open class IvyLeeDecorator(base: IvyLee): IvyLeeDecoratorBase(base), IvyLeeDecorable {

    override fun initializeUI() {
        setInitialSize()
        initializeUIElements()
        populateTaskCells()
    }

    override fun startBackgroundTasks() {
        Thread(SyncRunnable(IvyLee.remoteFilesHandler)).apply {
            isDaemon = true
            start()
        }
    }

}