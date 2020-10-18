package de.ott.ivy.data.enums

import javafx.scene.paint.Color

enum class Priorities(val color: Color) {
    LOWEST(Color.DARKGREEN),
    LOW(Color.GREEN),
    MEDIUM(Color.ORANGE),
    HIGH(Color.ORANGERED),
    HIGHEST(Color.RED),
    CRITICAL(Color.DARKRED)
}