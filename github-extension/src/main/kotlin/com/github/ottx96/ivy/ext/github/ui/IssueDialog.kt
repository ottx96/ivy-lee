package com.github.ottx96.ivy.ext.github.ui

import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.data.enums.TaskStatus
import com.github.ottx96.ivy.ext.github.GithubExtension
import com.github.ottx96.ivy.ext.github.config.Credentials
import com.github.ottx96.ivy.ext.github.data.GitHubIssue
import com.github.ottx96.ivy.ext.github.html.MarkdownParser
import com.github.ottx96.ivy.ext.github.threading.TableBuilderRunner
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.web.WebView
import javafx.stage.Stage
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GitHub
import tornadofx.*
import java.util.concurrent.atomic.AtomicBoolean


class IssueDialog: View("GitHub Issues") {
    override val root: BorderPane by fxml("/views/IssueDialog.fxml")

    val queryField: TextField by fxid("query")
    val table: TreeTableView<GitHubIssue> by fxid("tableview")

    val labels: FlowPane by fxid("labels")

    val markdown: TextArea by fxid("markdown")
    val web: WebView by fxid("web")

    val buttonOK: Button by fxid("ok")
    val buttonCancel: Button by fxid("cancel")

    var task: IvyLeeTask? = null

    companion object {
        val github: GitHub by lazy {
            with(Credentials.fromJson(GithubExtension.CREDENTIALS_FILE)!!) {
                return@lazy GitHub.connect(user, oAuthToken)
            }
        }

        fun showDialog(task: IvyLeeTask): IvyLeeTask {
            val dialog = IssueDialog()
            dialog.task = task
            Stage().apply {
                scene = Scene(dialog.root)
            }.showAndWait()
            return dialog.task!!
        }
    }

    val running = AtomicBoolean(false)
    init {
        table.style = "-fx-font-size: 1.4em;"
        queryField.promptText = queryField.promptText.replace("<username>", github.myself.login)

        initTableTreeView()
        Thread(TableBuilderRunner(table, queryField.promptText)).start()

        buttonOK.onAction = EventHandler {
            val t = task?:return@EventHandler
            val i = table.selectedItem?.data?:return@EventHandler
            t.name = "[GHIssue][#${i.number}] ${i.title}"
            val labels = i.labels.joinToString { "<span style=\"color:${it.color}\">[**${it.name}**]</span>" }
            t.descr = "$labels  \n${i.body}"
            t.status = when(i.state){
                GHIssueState.OPEN -> TaskStatus.UNDONE
                GHIssueState.CLOSED -> TaskStatus.DONE
                else -> TaskStatus.EMPTY
            }
            close()
        }
        buttonCancel.onAction = EventHandler { close() }

        queryField.onAction = EventHandler {
            github.refreshCache()
            initTableTreeView()
            if (queryField.text.isNullOrBlank())
                queryField.text = queryField.promptText

            // All Repositories from all Teams
            Thread(TableBuilderRunner(table, queryField.text)).start()
        }

        table.onMouseClicked = EventHandler {
            val issue = table.selectedItem?.data?:return@EventHandler
            markdown.text = issue.body
            labels.children.clear()
            issue.labels.forEach { lbl ->
                labels.add(label(" [${lbl.name}],"){
                    clip = Rectangle(1024.0,768.0)
                    font = Font.font("System", 22.0)
                    background = Background(BackgroundFill(Color.valueOf("#${lbl.color}"), CornerRadii.EMPTY, Insets.EMPTY))
                })
            }
            // refresh web view
            web.engine.loadContent(MarkdownParser.convertHtml(issue.body), "text/html")
        }
    }

    private fun initTableTreeView() {
        table.columns.clear()

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