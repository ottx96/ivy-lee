package de.ott.ivy.ext.github.threading

import de.ott.ivy.ext.github.data.GitHubIssue
import de.ott.ivy.ext.github.ui.IssueDialog
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView
import java.util.concurrent.atomic.AtomicBoolean

class TableBuilderRunner(val table: TreeTableView<GitHubIssue>, val query: String): Runnable {

    companion object{
        val running = AtomicBoolean(true)
    }

    override fun run() {
        if(running.get()) return
        running.set(true)
        IssueDialog.github.searchRepositories().q("user:${IssueDialog.github.myself.login}").list().forEach { repo ->
            val header = TreeItem(GitHubIssue(null, "", "", "${repo.fullName}"))
            header.isExpanded = true
            table.root.children.add(header)

            IssueDialog.github.searchIssues().q("$query repo:${repo.fullName}").list().forEach { issue ->
                header.children.add(TreeItem(GitHubIssue(issue)))
            }

            if(header.children.isEmpty()) table.root.children.remove(header)
        }
        running.set(false)
    }
}