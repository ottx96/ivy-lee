package de.ott.ivy.ext.github

import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.ext.github.config.Credentials
import de.ott.ivy.ext.github.ui.IssueDialog
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import java.io.File

@Extension(displayString = "Github Issues")
class GithubExtension : TaskExtension {

    val issueToTask = mutableMapOf<String, IvyLeeTask>()
//    val credentials = Credentials.fromJson(File("config/github-credentials.json"))


    override fun execute(task: IvyLeeTask) {

        val github = GitHub.connectUsingPassword("ottx96", "2512Manuel25!2")
        github.searchIssues().q("author:ottx96").list().forEach {
            println(it.title)
        }
        IssueDialog.showDialog()


    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            // DEBUG
            GithubExtension().execute(IvyLeeTask())
        }
    }

}