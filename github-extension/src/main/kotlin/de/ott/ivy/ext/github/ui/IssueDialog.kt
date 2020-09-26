package de.ott.ivy.ext.github.ui

import de.ott.ivy.ext.github.GithubExtension
import de.ott.ivy.ext.github.config.Credentials
import de.ott.ivy.ext.github.data.GitHubIssue
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kohsuke.github.GitHub
import tornadofx.View
import tornadofx.importStylesheet
import tornadofx.selectedItem


class IssueDialog: View("GitHub Issues") {
    override val root: BorderPane by fxml("/de/ott/ivy/ext/github/views/IssueDialog.fxml")

    val queryField: TextField by fxid("query")
    val table: TreeTableView<GitHubIssue> by fxid("tableview")

    val github by lazy {
        with(Credentials.fromJson(GithubExtension.CREDENTIALS_FILE)!!) {
            return@lazy GitHub.connect(user, oAuthToken)
        }
    }

    companion object {
        fun showDialog(): Boolean {
            val dialog = IssueDialog()
            Stage().apply {
                scene = Scene(dialog.root)
            }.showAndWait()
            return true
        }
    }

    init {
        table.style = "-fx-font-size: 1.4em;"
        queryField.promptText = queryField.promptText.replace("<username>", github.myself.login)
        initTableTreeView()

        queryField.onAction = EventHandler {
            table.root.children.clear()
            if (queryField.text.isNullOrBlank())
                queryField.text = queryField.promptText

            // All Repositories from all Teams
            Thread{
                github.searchRepositories().q("user:${github.myself.login}").list().forEach { repo ->
                    val header = TreeItem(GitHubIssue(null, "", "", "${repo.fullName}"))
                    header.isExpanded = true
                    table.root.children.add(header)

                    github.searchIssues().q("${queryField.text} repo:${repo.fullName}").list().forEach { issue ->
                        header.children.add(TreeItem(GitHubIssue(issue)))
                    }

                    if(header.children.isEmpty()) table.root.children.remove(header)
                }
            }.start()
        }
    }

    private fun initTableTreeView() {
        val columnRepository = TreeTableColumn<GitHubIssue, String>("Repository")
        val columnTitle = TreeTableColumn<GitHubIssue, String>("Title")
        val columnAuthor = TreeTableColumn<GitHubIssue, String>("Author")
        val columnLabels = TreeTableColumn<GitHubIssue, String>("Labels")
        val columnState = TreeTableColumn<GitHubIssue, String>("State")

        columnRepository.cellValueFactory = TreeItemPropertyValueFactory("repo")
        columnTitle.cellValueFactory = TreeItemPropertyValueFactory("title")
        columnAuthor.cellValueFactory = TreeItemPropertyValueFactory("author")
        columnLabels.cellValueFactory = TreeItemPropertyValueFactory("labels")
        columnState.cellValueFactory = TreeItemPropertyValueFactory("state")

        table.columns.add(columnRepository)
        table.columns.add(columnTitle)
        table.columns.add(columnAuthor)
        table.columns.add(columnLabels)
        table.columns.add(columnState)

        val repos = TreeItem(GitHubIssue(null, "", "", "Repositories"))
        table.root = repos
        table.root.isExpanded = true
    }
}