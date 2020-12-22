/**
 * Copyright (C) 2016 Matthieu Brouillard [http://oss.brouillard.fr/jgitver] (matthieu@brouillard.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ottx96.commonmark.ext.notifications

import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter
import java.util.*

class NotificationNodeRenderer(private val context: HtmlNodeRendererContext) : NodeRenderer {
    private val htmlWriter: HtmlWriter
    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(NotificationBlock::class.java)
    }

    override fun render(node: Node) {
        val nb = node as NotificationBlock
        htmlWriter.line()
        htmlWriter.tag("div", Collections.singletonMap("class", classOf(nb.type)))
        renderChildren(nb)
        htmlWriter.tag("/div")
        htmlWriter.line()
    }

    private fun renderChildren(parent: Node) {
        var node = parent.firstChild
        while (node != null) {
            val next = node.next
            context.render(node)
            node = next
        }
    }

    companion object {
        private fun classOf(n: Notification): String {
            return "notification_" + n.name.toLowerCase(Locale.ENGLISH)
        }
    }

    init {
        htmlWriter = context.writer
    }
}