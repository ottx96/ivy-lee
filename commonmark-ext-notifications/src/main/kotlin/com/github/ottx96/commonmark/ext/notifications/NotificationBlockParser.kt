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

import org.commonmark.node.Block
import org.commonmark.parser.block.*
import java.util.regex.Pattern

class NotificationBlockParser(private val type: Notification) : AbstractBlockParser() {
    private val block: NotificationBlock = NotificationBlock(type)
    override fun isContainer(): Boolean {
        return true
    }

    override fun canContain(block: Block): Boolean {
        return !NotificationBlock::class.java.isAssignableFrom(block.javaClass)
    }

    override fun getBlock(): Block {
        return block
    }

    override fun tryContinue(state: ParserState): BlockContinue {
        val fullLine = state.line.content
        val currentLine = fullLine.subSequence(state.column + state.indent, fullLine.length)
        val matcher = NOTIFICATIONS_LINE.matcher(currentLine)
        return if (!matcher.matches() || type != Notification.Companion.fromString(
                matcher.group(1)
            )
        ) BlockContinue.none() else BlockContinue.atColumn(state.column + state.indent + matcher.start(2))
    }

    class Factory : AbstractBlockParserFactory() {
        override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart? {
            val fullLine = state.line.content
            val line = fullLine.subSequence(state.column, fullLine.length)
            val matcher = NOTIFICATIONS_LINE.matcher(line)
            return if (matcher.matches()) {
                BlockStart
                    .of(NotificationBlockParser(
                            Notification.fromString(matcher.group(1))
                        ))
                    .atColumn(state.column + state.indent + matcher.start(2))
            } else BlockStart.none()
        }
    }

    companion object {
        private val NOTIFICATIONS_LINE = Pattern.compile("\\s*!([v!x]?)\\s(.*)")
    }

}