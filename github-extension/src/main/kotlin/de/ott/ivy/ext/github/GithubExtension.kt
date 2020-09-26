package de.ott.ivy.ext.github

import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.ext.github.ui.SetupDialog
import org.kohsuke.github.GitHub


@Extension(displayString = "Github Issues")
class GithubExtension : TaskExtension {

    val issueToTask = mutableMapOf<String, IvyLeeTask>()
//    val credentials = Credentials.fromJson(File("config/github-credentials.json"))

    override fun execute(task: IvyLeeTask) {


        SetupDialog.showDialog()
//        IssueDialog.showDialog()
        //https://github.com/settings/tokens/new



    }
}