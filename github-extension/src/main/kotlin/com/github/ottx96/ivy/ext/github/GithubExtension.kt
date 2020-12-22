package com.github.ottx96.ivy.ext.github

import com.github.ottx96.ivy.TaskExtension
import com.github.ottx96.ivy.annotation.Extension
import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.ext.github.config.Credentials
import com.github.ottx96.ivy.ext.github.ui.IssueDialog
import com.github.ottx96.ivy.ext.github.ui.SetupDialog
import java.io.File


@Extension(displayString = "Github Issues")
class GithubExtension : TaskExtension {
    companion object {
        val CREDENTIALS_FILE = File("config/github-credentials.json")
    }

    val credentials = Credentials.fromJson(CREDENTIALS_FILE) ?: SetupDialog.showDialog()

    override fun execute(task: IvyLeeTask) {
        IssueDialog.showDialog(task)
    }
}