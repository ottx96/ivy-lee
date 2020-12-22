package com.github.ottx96.ivy.data.enums

import javafx.scene.paint.Color

enum class Priorities(val color: Color, val label: String) {
    LOWEST(Color.DARKGREEN, "Lowest"),
    LOW(Color.GREEN, "Low"),
    MEDIUM(Color.ORANGE, "Medium"),
    HIGH(Color.ORANGERED, "High"),
    HIGHEST(Color.RED, "Highest"),
    CRITICAL(Color.DARKRED, "Critical")
}