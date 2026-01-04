package reversi.model
import reversi.model.PlayerColor.*

enum class PlayerColor {
    BLACK, WHITE,
}

val PlayerColor.opponent: PlayerColor get() = if (this == BLACK) WHITE else BLACK

fun String.toColor(): PlayerColor = PlayerColor.valueOf(this)