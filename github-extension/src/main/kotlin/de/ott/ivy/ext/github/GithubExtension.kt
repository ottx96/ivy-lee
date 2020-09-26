package de.ott.ivy.ext.github

import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.ext.github.config.Credentials
import de.ott.ivy.ext.github.ui.IssueDialog
import de.ott.ivy.ext.github.ui.SetupDialog
import java.io.File


@Extension(displayString = "Github Issues")
class GithubExtension : TaskExtension {
    companion object {
        val CREDENTIALS_FILE = File("config/github-credentials.json")
    }

    val issueToTask: MutableMap<String, IvyLeeTask> by lazy{
        TODO()
    }
    val credentials: Credentials by lazy {
        Credentials.fromJson(CREDENTIALS_FILE) ?: SetupDialog.showDialog()
    }

    override fun execute(task: IvyLeeTask) {

        credentials.user
        IssueDialog.showDialog()

    }
}