package com.github.ottx96.ivy.data

import com.github.ottx96.ivy.data.enums.Priorities
import com.github.ottx96.ivy.data.enums.TaskStatus
import com.github.ottx96.ivy.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Container (data) classes
 *
 * Project: ivy-lee
 * Package: de.mait.ott
 * Created: 28.01.2020 16:58
 * @author = manuel.ott
 * @since = 28. Januar 2020
 *
 * UML: State diagram
 * @startuml
 * State Setup {
 * State "user input" as aui
 *  aui: entry / open GUI screen
 *  aui: do / wait for user input
 *  aui: exit / save configuration data
 *  [*] --> aui
 * }
 * State Initialization {
 * State "loading" as load
 *  [*] --> load
 *  load: entry / download task data
 *  load: do / read task data
 *  load: exit / open GUI screen
 * }
 * State "task overview (task status)" as tasks{
 *   [*] -> UNDONE
 *   UNDONE: exit / refresh GUI
 *   UNDONE -> IN_WORK: [user input] / do / refresh GUI
 *   IN_WORK: exit / refresh GUI
 *   IN_WORK --> DONE: [user input] / do / refresh GUI
 *   DONE: exit / refresh GUI
 *   DONE --> UNDONE: [user input] / do / refresh GUI
 * }
 *
 * [*] -> Setup : [first run]
 * [*] --> Initialization : [else]
 * Setup --> Initialization: [user input] / do / save data
 * Initialization --> tasks: [task data read] / do / open GUI
 * tasks ---> deletion : [user input] / do / delete task
 * deletion ---> tasks : [task deleted] / do / refresh GUI
 * deletion: exit / refresh GUI
 * tasks ---> creation : [user input] / do / show GUI screen
 * creation ---> tasks : [task created] / do / refresh GUI
 * creation: entry / show GUI screen
 * creation: do / wait for user input
 * creation: exit / save task data
 * creation: exit / refresh GUI
 * tasks -> [*]: [user input] / do / exit
 * @enduml
 */
@Serializable
data class IvyLeeTask(var name: String = "", var descr: String = "",
                      var estTimeSeconds: Int = 0, var status: TaskStatus = TaskStatus.EMPTY,
                      var timeInvestedSeconds: Int = 0, var favorite: Boolean = false,
                      @Serializable(with = LocalDateSerializer::class) var dueDate: LocalDate = LocalDate.now().plusDays(7),
                      var priority: Priorities = Priorities.LOWEST) {

    companion object {
        const val REVISION = 3
    }
}
