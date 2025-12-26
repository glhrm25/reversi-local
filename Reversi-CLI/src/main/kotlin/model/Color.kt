package model
import model.Color.*
import user_interface.*

enum class Color {
    BLACK, WHITE,
}

val Color.otherColor: Color get() = if (this == BLACK) WHITE else BLACK

fun Char.color(): Color = if (this == BLACK_SYMBOL) BLACK else WHITE

fun String.toColor(): Color = Color.valueOf(this)

// fun String.toColorOrNull(): Color? = Color.entries.firstOrNull{ it.name == this }