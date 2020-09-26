package de.ott.ivy.ext.github

import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask

@Extension(displayString = "Github Issues")
class GithubExtension : TaskExtension {
    override fun execute(task: IvyLeeTask) {
        println("Fetching Issues from GitHub..")
    }
}