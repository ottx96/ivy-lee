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

import java.lang.IllegalStateException
import org.commonmark.node.CustomBlock
import org.commonmark.parser.block.AbstractBlockParser
import com.github.ottx96.commonmark.ext.notifications.NotificationBlock
import org.commonmark.parser.block.BlockContinue
import com.github.ottx96.commonmark.ext.notifications.NotificationBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.BlockStart
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlWriter
import java.util.Collections
import com.github.ottx96.commonmark.ext.notifications.NotificationNodeRenderer
import java.util.Locale

enum class Notification {
    INFO, SUCCESS, WARNING, ERROR;

    companion object {
        fun fromString(s: String?): Notification {
            return if (s == null || s.trim { it <= ' ' }.isEmpty()) {
                INFO
            } else when (s) {
                "v" -> SUCCESS
                "x" -> ERROR
                "!" -> WARNING
                else -> throw IllegalStateException()
            }
        }
    }
}