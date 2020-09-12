package de.ott.ivy.html

import fr.brouillard.oss.commonmark.ext.notifications.NotificationsExtension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

object MarkdownParser {

    private val parser: Parser by lazy { Parser.builder().extensions( listOf(AutolinkExtension.create(), TaskListItemsExtension.create(), NotificationsExtension.create() )).build() }
    private val renderer: HtmlRenderer by lazy { HtmlRenderer.builder().extensions( listOf(AutolinkExtension.create(), TaskListItemsExtension.create(), NotificationsExtension.create() )).build() }
    fun convertHtml(markdown: String): String{
        var htmlString = renderer.render(parser.parse( markdown ))

        if(htmlString.contains("""type="checkbox""""))
            htmlString = htmlString.replace("""<li>""", "").replace("</li>", "<br>").replace("<ul>", "").replace("</ul>", "")

        return htmlString
    }

}