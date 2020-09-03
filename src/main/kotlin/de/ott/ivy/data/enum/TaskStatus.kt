package de.ott.ivy.data.enum

import javafx.scene.paint.Color

enum class TaskStatus(var color: Color){
    EMPTY(Color.valueOf("#dbdbdb")),
    UNDONE(Color.valueOf("#ff9933")),
    IN_WORK(Color.valueOf("#ffdd22")),
    DONE(Color.valueOf("#84ee3f"))
}