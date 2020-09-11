package de.ott.ivy

import de.ott.ivy.data.IvyLeeTask

/**
 * Extension class. Interface for implementing custom *Extensions*
 *
 * New extensions *have to* implement this interface.
 * Also, if a custom label should be displayed,
 * annotation [de.ott.ivy.annotation.Extension] has to be provided.
 * Extensions display string defaults to [java.lang.Class#getSimpleName].
 *
 * @author: ottx96
 * @see de.ott.ivy.annotation.Extension
 *
 * @startuml
 *
 * package "IvyLee: Main" <<Cloud>> {
 *  class TaskDialog {
 *   - tasks: List<IvyLeeTask>
 *   --
 *   # execute(extensionId: String): void
 *
 *  }
 * }
 *
 * package "IvyLee: ExtensionAPI" <<Node>> {
 * annotation Extension
 * Extension : ~ displayName: String
 * Extension : --
 *
 * class CustomExtension implements TaskExtension {
 * custom fields
 * --
 * custom methods
 * ==
 *  + execute(IvyLeeTask): void
 * }
 *
 * interface TaskExtension {
 *  --
 *  + {abstract} execute(IvyLeeTask): void
 * }
 *
 *
 * class ExtensionCrawler {
 *  --
 *  + searchExtensions(): List<? implements TaskExtension>
 * }
 *
 *   ExtensionCrawler --> CustomExtension: finds
 *   TaskDialog --> ExtensionCrawler: uses
 *   TaskDialog "1" --> "n" CustomExtension: executes
 *   CustomExtension "1" --* "0..1" Extension: contains
 * }
 *
 * @enduml
 */
interface TaskExtension {

    fun execute(task: IvyLeeTask)

}