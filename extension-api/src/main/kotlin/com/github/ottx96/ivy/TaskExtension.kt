package com.github.ottx96.ivy

import com.github.ottx96.ivy.data.IvyLeeTask

/**
 * Extension class. Interface for implementing custom *Extensions*
 *
 * New extensions *have to* implement this interface.
 * Also, if a custom label should be displayed,
 * annotation [com.github.ottx96.ivy.annotation.Extension] has to be provided.
 * Extensions display string defaults to [java.lang.Class#getSimpleName].
 *
 * @author: ottx96
 * @see com.github.ottx96.ivy.annotation.Extension
 *
 * @startuml
 * package "class ExtensionAPI (class diagram)" <<frame>>{
 *   package "IvyLee: Main" <<Cloud>> {
 *    class TaskDialog {
 *     - tasks: List<IvyLeeTask>
 *     --
 *     # execute(extensionId: String): void
 *    }
 *   }
 *
 *   package "IvyLee: ExtensionAPI" <<Package>> {
 *   annotation Extension
 *   Extension : ~ displayName: String
 *   Extension : --
 *
 *   class CustomExtension implements TaskExtension {
 *   custom fields
 *   --
 *   custom methods
 *   ==
 *    + execute(IvyLeeTask): void
 *   }
 *
 *   interface TaskExtension {
 *    --
 *    + {abstract} execute(IvyLeeTask): void
 *   }
 *
 *
 *   class ExtensionCrawler {
 *    --
 *    + searchExtensions(): List<? implements TaskExtension>
 *   }
 *
 *     ExtensionCrawler --> CustomExtension: searches
 *     ExtensionCrawler .> TaskExtension: <<use>>
 *     TaskDialog ..> ExtensionCrawler: <<use>>
 *     TaskDialog "1" ---> "n" CustomExtension: executes
 *     CustomExtension "1" --o "0..1" Extension: contains
 *   }
 * }
 * @enduml
 */
interface TaskExtension {

    fun execute(task: IvyLeeTask)

}