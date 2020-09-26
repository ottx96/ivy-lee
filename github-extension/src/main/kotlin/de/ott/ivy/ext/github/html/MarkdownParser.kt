package de.ott.ivy.ext.github.html

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

object MarkdownParser {

    private val parser: Parser by lazy { Parser.builder().extensions( listOf(AutolinkExtension.create())).build() }
    private val renderer: HtmlRenderer by lazy { HtmlRenderer.builder().extensions( listOf(AutolinkExtension.create())).build() }
    fun convertHtml(markdown: String): String{
        return renderer.render(parser.parse( markdown ))
    }

}